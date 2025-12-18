import {Component, OnInit} from '@angular/core';
import {FormsModule, NgForm, NgModel} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Observable, of, zip} from 'rxjs';
import {AutocompleteComponent} from 'src/app/component/autocomplete/autocomplete.component';
import {Horse, convertFromHorseToCreate, convertFromHorseToUpdate} from 'src/app/dto/horse';
import {Owner} from 'src/app/dto/owner';
import {Sex} from 'src/app/dto/sex';
import {ErrorFormatterService} from 'src/app/service/error-formatter.service';
import {HorseService} from 'src/app/service/horse.service';
import {OwnerService} from 'src/app/service/owner.service';
import {formatIsoDate} from "../../../utils/date-helper";
import {ImageService} from "../../../service/image.service";

export enum HorseCreateEditMode {
  create,
  edit
}

@Component({
  selector: 'app-horse-create-edit',
  templateUrl: './horse-create-edit.component.html',
  imports: [
    FormsModule,
    AutocompleteComponent,
    FormsModule
  ],
  standalone: true,
  styleUrls: ['./horse-create-edit.component.scss']
})
export class HorseCreateEditComponent implements OnInit {

  mode: HorseCreateEditMode = HorseCreateEditMode.create;
  horse: Horse = {
    name: '',
    description: '',
    dateOfBirth: new Date(),
    sex: Sex.female,
  };
  horseBirthDateIsSet = false;


  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private imageService: ImageService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create New Horse';
      case HorseCreateEditMode.edit:
        return 'Edit Horse';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create';
      case HorseCreateEditMode.edit:
        return 'Save';
      default:
        return '?';
    }
  }

  public get horseBirthDateText(): string {
    if (!this.horseBirthDateIsSet) {
      return '';
    } else {
      return formatIsoDate(this.horse.dateOfBirth);
    }
  }

  public set horseBirthDateText(date: string) {
    if (date == null || date === '') {
      this.horseBirthDateIsSet = false;
    } else {
      this.horseBirthDateIsSet = true;
      this.horse.dateOfBirth = new Date(date);
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateEditMode.create;
  }


  get sex(): string {
    switch (this.horse.sex) {
      case Sex.male:
        return 'Male';
      case Sex.female:
        return 'Female';
      default:
        return '';
    }
  }


  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'created';
      case HorseCreateEditMode.edit:
        return 'edited';
      default:
        return '?';
    }
  }

  public get imageUrl(): string {
    if (!this.horse || !this.horse.imageId) {
      return '';
    } else {
      return this.imageService.getImageUrl(this.horse.imageId);
    }
  }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.searchByName(input, 5);
  motherSuggestions = (input: string) => (input === '')
    ? of([])
    : this.service.searchHorses({name: input, sex: Sex.female, limit: 5});

  fatherSuggestions = (input: string) => (input === '')
    ? of([])
    : this.service.searchHorses({name: input, sex: Sex.male, limit: 5});

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    const horseId = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isNaN(horseId) && this.mode === HorseCreateEditMode.edit) {
      this.service.getById(horseId).subscribe((horse) => {
        this.horse = horse;
        this.horseBirthDateIsSet = true;

        zip(
          horse.motherId ? this.service.getById(horse.motherId) : of(null),
          horse.fatherId ? this.service.getById(horse.fatherId) : of(null)
        ).subscribe(([mother, father]) => {
          this.horse.mother = mother ?? undefined;
          this.horse.father = father ?? undefined;
        })
      });
    }
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public formatHorseName(horse: Horse | null | undefined): string {
    return (horse == null)
      ? ''
      : `${horse.name} `;
  }

  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }

  public onFileSelect(input: HTMLInputElement) {
    if (input.files && input.files[0]) {
      this.horse.image = input.files[0];
    }
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.horse);
    if (form.valid) {
      if (this.horse.description === '') {
        delete this.horse.description;
      }
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateEditMode.create:
          observable = this.service.create(
            convertFromHorseToCreate(this.horse)
          );
          break;
        case HorseCreateEditMode.edit:
          observable = this.service.update(
            convertFromHorseToUpdate(this.horse)
          );
          break;
        default:
          console.error('Unknown HorseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/horses']);
        },
        error: error => {
          console.error('Error creating horse', error);
          this.notification.error(this.errorFormatter.format(error), 'Could Not Create Horse', {
            enableHtml: true,
            timeOut: 10000,
          });
        }
      });
    }
  }
}
