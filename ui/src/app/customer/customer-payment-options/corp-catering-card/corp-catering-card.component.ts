import { Component, Input, OnInit } from '@angular/core';

// Models
import { CorporateCateringAccount } from '@models/corporate-catering-account.model';
import { CustomerDetails } from '@models/customer-details.model';

@Component({
  selector: 'app-corp-catering-card',
  templateUrl: './corp-catering-card.component.html',
  styleUrls: ['./corp-catering-card.component.scss']
})
export class CorpCateringCardComponent implements OnInit {

  @Input() corporateCateringAccount: CorporateCateringAccount;
  @Input() customerDetails: CustomerDetails;

  public edit = false;

  constructor() {}

  ngOnInit() {
    this.corporateCateringAccount.orgStartDate = this.formatDate(this.corporateCateringAccount.orgStartDate);
    this.corporateCateringAccount.orgEndDate = this.formatDate(this.corporateCateringAccount.orgEndDate);
    this.corporateCateringAccount.clientStartDate = this.formatDate(this.corporateCateringAccount.clientStartDate);
    this.corporateCateringAccount.clientEndDate = this.formatDate(this.corporateCateringAccount.clientEndDate);
  }

  /**
   * Format the string passed in to a human readable date
   *
   * @param {string} stringDate The string containing a date passed in
   * @returns {string} The formatted string
   */
  private formatDate (stringDate: string): string {
    return new Date(stringDate.substr(0, 19)).toLocaleDateString();
  }

}
