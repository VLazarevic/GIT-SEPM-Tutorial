import {Component, OnInit} from '@angular/core';
import {Horse} from "../../../dto/horse";
import {Sex} from "../../../dto/sex";
import {HorseService} from "../../../service/horse.service";
import {OwnerService} from "../../../service/owner.service";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {Owner} from "../../../dto/owner";
import {of, zip} from "rxjs";
import {ConfirmDeleteDialogComponent} from "../../confirm-delete-dialog/confirm-delete-dialog.component";
import {FormsModule} from "@angular/forms";
import {ImageService} from "../../../service/image.service";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-horse-detail',
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    ConfirmDeleteDialogComponent
  ],
  templateUrl: './horse-detail.component.html',
  standalone: true,
  styleUrl: './horse-detail.component.scss'
})
export class HorseDetailComponent implements OnInit{
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
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(data => {
      const horseId = Number(data.get('id'));
      if (!Number.isNaN(horseId)) {
        this.service.getById(horseId).subscribe((horse) => {
          this.horse = horse;

          zip(
            horse.motherId ? this.service.getById(horse.motherId) : of(null),
            horse.fatherId ? this.service.getById(horse.fatherId) : of(null)
          ).subscribe(([mother, father]) => {
            this.horse.mother = mother ?? undefined;
            this.horse.father = father ?? undefined;
          })
        });
      }
    });
  }

  public get imageUrl(): string {
    if (!this.horse || !this.horse.imageId) {
      return '';
    } else {
      return this.imageService.getImageUrl(this.horse.imageId);
    }
  }

  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }

  public formatHorseName(horse: Horse | null | undefined): string {
    return (horse == null)
      ? ''
      : `${horse.name} `;
  }

  public deleteHorse() {
    if (!this.horse || !this.horse.id) {
      console.error('Cannot delete horse: Invalid horse or missing ID');
      return;
    }

    this.service.delete(this.horse.id).subscribe({
      next: () => {
      this.notification.success(`Horse ${this.horse.name} was successfully deleted`, 'Delete Successful');

      this.router.navigate(['/horses']);
    },
      error: error => {
      console.error('Error deleting horse', error);
      const errorMessage = error.status === 0
        ? 'Is the backend up?'
        : error.message || 'Unknown error';
      this.notification.error(errorMessage, 'Could Not Delete Horse');
    }
    });
  }
}
