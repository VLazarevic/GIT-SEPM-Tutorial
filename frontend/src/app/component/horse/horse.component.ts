import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AutocompleteComponent } from 'src/app/component/autocomplete/autocomplete.component';
import { HorseService } from 'src/app/service/horse.service';
import {Horse, HorseSearch} from 'src/app/dto/horse';
import { Owner } from 'src/app/dto/owner';
import { ConfirmDeleteDialogComponent } from 'src/app/component/confirm-delete-dialog/confirm-delete-dialog.component';
import {of} from "rxjs";
import {OwnerService} from "../../service/owner.service";


@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  imports: [
    RouterLink,
    FormsModule,
    AutocompleteComponent,
    ConfirmDeleteDialogComponent
  ],
  standalone: true,
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  horses: Horse[] = [];
  bannerError: string | null = null;
  horseForDeletion: Horse | undefined;
  searchParams: HorseSearch = {};

  constructor(
    private service: HorseService,
    private notification: ToastrService,
    private ownerService: OwnerService
  ) { }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.searchByName(input, 5);

  ngOnInit(): void {
    this.reloadHorses();
  }

  reloadHorses() {
    this.service.searchHorses(this.searchParams)
      .subscribe({
        next: data => {
          this.horses = data;
          this.bannerError = null;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }

  ownerName(owner: Owner | null): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    if (!horse.dateOfBirth) {
      return '';
    }
    try {
      const date = horse.dateOfBirth;
      return date.toLocaleDateString();
    } catch (e) {
      console.error('Error formatting date', e);
      return String(horse.dateOfBirth);
    }
  }


  deleteHorse(horse: Horse) {
    if (!horse || !horse.id) {
      console.error('Cannot delete horse: Invalid horse or missing ID');
      return;
    }

    this.service.delete(horse.id)
      .subscribe({
        next: () => {
          this.notification.success(`Horse ${horse.name} was successfully deleted`, 'Delete Successful');
          // Remove the horse from the local array
          this.horses = this.horses.filter(h => h.id !== horse.id);
          // Reset the horse for deletion
          this.horseForDeletion = undefined;
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
