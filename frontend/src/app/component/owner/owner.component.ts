import {Component, OnInit} from '@angular/core';
import {Owner} from "../../dto/owner";
import {OwnerService} from "../../service/owner.service";
import {ToastrService} from "ngx-toastr";
import {CommonModule} from "@angular/common";
import {RouterLink} from "@angular/router";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-owner',
  imports: [
    CommonModule,
    RouterLink,
    FormsModule
  ],
  templateUrl: './owner.component.html',
  standalone: true,
  styleUrl: './owner.component.scss'
})
export class OwnerComponent implements OnInit{
  owners: Owner[] = [];

  constructor(
    private service: OwnerService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadOwners();
  }

  reloadOwners() {
    this.service.getAll()
      .subscribe({
        next: data => {
          this.owners = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Owners');
        }
      });
  }
}
