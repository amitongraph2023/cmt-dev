import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

// Models
import { CustomerDetails } from '@models/customer-details.model';
import { CustomerEmail } from '@models/customer-email.model';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerEmailService } from '@services/customer-email.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerService } from '@services/customer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Enum
import { CustomerEmailType } from '@enums/cusotmer-email-type.enum';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-customer-emails',
  templateUrl: './customer-emails.component.html',
  styleUrls: ['./customer-emails.component.scss']
})
export class CustomerEmailsComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;

  public customerEmails: CustomerEmail[];
  public successMessage: string;
  public addEmail: Boolean;
  public newEmail: CustomerEmail;
  public emailTypes = CustomerEmailType;

  public canCreate = false;

  private pageTitle = 'Customer Email';

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerEmailService: CustomerEmailService
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
  ) {}

  ngOnInit() {
    this._authService.checkCredentials().subscribe((canView) => {
      if (canView) {

        this._titlePropagatorService.setNewTitle(this.pageTitle);

        this._authService.checkComponentPrivilege('cust.email.create')
          .subscribe(canCreate => this.canCreate = canCreate);

      } else {
        this._customerPropagatorService.setCustomerId(null);
      }

    });

    this.loadCustomerEmails();
  }

  public addEmailAddress(): void {
    this.newEmail.isDefault = true;
    $('#addEmailModal').modal('hide');
    this._customerEmailService.addEmailAddressByCustomerId(
      this.customerDetails.customerId
      , this.newEmail
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.newEmail = null;
      this.addEmail = false;
      this.loadCustomerEmails();
    });
  }

  public clickAddNewEmail(): void {
    this.addEmail = true;
    this.newEmail = new CustomerEmail();
    $('#addEmailModal').modal('show');
  }

  /**
   * Load customer phones from an API call
   */
  public loadCustomerEmails(): void {
    this._customerEmailService.getEmailsByCustomerId(this.customerDetails.customerId).subscribe(
      (customerEmails) => this.customerEmails = customerEmails,
      (error) => console.error(error)
    );

    this.successMessage = null;
  }

  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal').modal('show');
  }
}
