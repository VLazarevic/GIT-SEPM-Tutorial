import {Component, OnInit} from '@angular/core';
import {Owner} from "../../../dto/owner";
import {OwnerService} from "../../../service/owner.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {FormsModule, NgForm} from "@angular/forms";
import {Observable} from "rxjs";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-owner-create',
  imports: [CommonModule, FormsModule],
  templateUrl: './owner-create.component.html',
  standalone: true,
  styleUrl: './owner-create.component.scss'
})
export class OwnerCreateComponent implements OnInit{
  owner: Owner = {
    firstName: '',
    lastName: '',
    description: ''
  };

  constructor(
    private service: OwnerService,
    private notification: ToastrService,
    private router: Router,
  ) { }

  ngOnInit(): void {
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.owner);
    if (form.valid) {
      const observable: Observable<Owner> = this.service.create(this.owner);
      observable.subscribe({
        next: data => {
          this.notification.success(`Owner ${this.owner.firstName} ${this.owner.lastName} successfully created.`);
          this.router.navigate(['/owners']);
        },
        error: error => {
          console.error('Error creating owner', error);
          this.notification.error('Error: ' + error.error.errors.join('\nand ') + '.');
        }
      });
    }
  }
}
