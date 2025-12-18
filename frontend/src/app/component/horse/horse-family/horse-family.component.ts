import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {HorseFamily} from 'src/app/dto/horse';
import {ErrorFormatterService} from 'src/app/service/error-formatter.service';
import {HorseService} from 'src/app/service/horse.service';
import {ConfirmDeleteDialogComponent} from "../../confirm-delete-dialog/confirm-delete-dialog.component";
import {FamilyTreeNode} from "./horse-family-tree/horse-family-tree.component";

@Component({
  selector: 'app-horse-family',
  templateUrl: './horse-family.component.html',
  imports: [
    FormsModule,
    ConfirmDeleteDialogComponent,
    FamilyTreeNode
  ],
  standalone: true,
  styleUrls: ['./horse-family.component.scss']
})
export class HorseFamilyComponent implements OnInit {
  horseFamily?: HorseFamily;

  horseForDeletion?: HorseFamily;

  gen?: number;

  constructor(
    private service: HorseService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService
  ) {
  }

  reload() {
    this.route.params.subscribe(params => {
      this.service.getFamilyById(params.id, this.gen!).subscribe({
        next: (data) => {
          this.horseFamily = data;
        },
        error: error => {
          console.error('Error fetching family tree', error);
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horse ' + params.id);
        }
      });
    })
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(queryParams => {
      this.gen = queryParams.gen;

      this.reload();
    });
  }

  deleteHorse(horse: HorseFamily) {
    const observable = this.service.delete(horse.id);

    observable.subscribe({
      next: data => {
        this.notification.success(`Horse ${horse.name} successfully deleted.`);
        this.reload();
      },
      error: error => {
        console.error('Error deleting horse', error);
        this.notification.error(this.errorFormatter.format(error), 'Could Not Create Horse', {
          enableHtml: true,
          timeOut: 10000,
        });
      }
    });
  }

  markHorseForDeletion(horse: HorseFamily) {
    this.horseForDeletion = horse;
  }
}

