import {Component, Input, OnInit} from '@angular/core';
import {isNullOrUndefined} from "util";
import {CustomerDetails} from "@models/customer-details.model";
import {CustomerMfa} from "@models/customer-mfa.model";
import {AuthenticationService} from "@services/authentication.service";
import {CustomerPropagatorService} from "@services/customer-propagator.service";
import {CustomerService} from "@services/customer.service";
import {Router} from "@angular/router";
import {TitlePropagatorService} from "@services/title-propagator.service";
import {CustomerMfaService} from "@services/customer-mfa.service";

@Component({
  selector: 'app-mfa',
  templateUrl: './mfa.component.html',
  styleUrls: ['./mfa.component.scss']
})
export class MfaComponent implements OnInit {

  @Input() customerDetails: CustomerDetails;

  public customerMfas: CustomerMfa[];

  public canRemoveSmsMfa = false;

  private pageTitle = 'Customer Mfas';


  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerMfaService: CustomerMfaService
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
  ) {}

  ngOnInit() {
    this._authService.checkCredentials().subscribe((canView) => {
      if (canView) {
        this._authService.checkComponentPrivilege('cust.mfa.remove')
          .subscribe(canRemoveSmsMfa => this.canRemoveSmsMfa = canRemoveSmsMfa);

        this._titlePropagatorService.setNewTitle(this.pageTitle);

      } else {
        this._customerPropagatorService.setCustomerId(null);
      }

    });

    this.loadCustomerMfas();
  }

  /**
   * Load customer mfas from an API call
   */
  public loadCustomerMfas(): void {
    this._customerMfaService.getMfasByCustomerId(this.customerDetails.customerId).subscribe(
      (customerMfas) => this.customerMfas = customerMfas,
      (error) => console.error(error)
    );
  }

  /**
   * On Remove SMS Button Click
   */
  public onRemoveSmsButtonClick(){
    this._customerMfaService.removeCustomerSmsMfaByCustomerId(this.customerDetails.customerId).subscribe(
      () => {},
      (error) => console.error(error),
      () => {
        this.loadCustomerMfas();
      }
    )
  }

  /**
   * For the HTML, verifies that the customerSubscription object and the isEmailGlobalOpt are set
   */
  public pageCheck(): boolean {
    return (!isNullOrUndefined(this.customerDetails, this.customerMfas));
  }

}
