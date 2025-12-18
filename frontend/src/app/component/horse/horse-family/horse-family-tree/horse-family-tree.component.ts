import {Component, Input, OnInit} from '@angular/core';
import {RouterLink} from "@angular/router";
import {HorseFamily} from "../../../../dto/horse";

/** Component for an family-tree-node input, similar to a combo box,
 * that lets the user search a list of options by entering a search text.
 * The using site needs to supply a callback that produces the family-tree-node options
 * and a callback that formats the model objects to readable text.
 *
 * @param T the model type. In practice only used as a placeholder and helper for typesafety inside this class.
 */
@Component({
  selector: 'app-horse-family-tree',
  templateUrl: './horse-family-tree.component.html',
  styleUrls: ['./horse-family-tree.component.scss'],
  imports: [
    RouterLink,
  ],
  standalone: true
})
export class FamilyTreeNode implements OnInit {
  // See documentation of NgClass for comparison
  // <https://angular.io/api/common/NgClass>
  @Input() data?: HorseFamily;

  @Input() deleteHorse?: (horse: HorseFamily) => void;

  collapsed: boolean = false;

  constructor() {

  }

  ngOnInit(): void {

  }

  get formattedDateOfBirth() {
    if (!this.data?.dateOfBirth) {
      return '';
    }
    return (new Date(this.data.dateOfBirth)).toLocaleDateString();
  }

}
