import { Component, Input, OnInit } from '@angular/core';
import { CustomerDetails } from '@models/customer-details.model';
import { CustomerCardExchangeForm } from '@models/customer-card-exchange-form.model';
import { CustomerLoyaltyService } from '@services/customer-loyalty.service';
import { CustomerCardExchange } from '@models/customer-card-exchange.model';
import { PermissionType } from '@enums/permission-type.enum';
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';

@Component({
  selector: 'app-paytronix-card-exchange',
  templateUrl: './paytronix-card-exchange.component.html',
  styleUrls: ['./paytronix-card-exchange.component.scss']
})
export class PaytronixCardExchangeComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;

  public customerCardExchangeForm: CustomerCardExchangeForm;
  public hasAdminPermission: boolean;

  constructor(private _authService: AuthenticationService
    , private _customerLoyaltyService: CustomerLoyaltyService
    , private _customerPropagatorService: CustomerPropagatorService) {
    this.customerCardExchangeForm = new CustomerCardExchangeForm();
    this.customerCardExchangeForm.customerCardExchange = new CustomerCardExchange();
    this.customerCardExchangeForm.excludePX = false;
    this.customerCardExchangeForm.customerCardExchange.opt = true;
  }

  ngOnInit() {
    this._authService.checkCredentials().subscribe((canView) => {
      if ( canView ) {
        this.hasAdminPermission = this._authService.isPermissible([ PermissionType.ADMIN ]);
      }
    });
  }

  public exchangeCard(): void {
    this._customerLoyaltyService.cardExchange(
      this.customerDetails.customerId
      , this.customerDetails.loyaltyCardNumber
      , this.customerCardExchangeForm.customerCardExchange
      , this.customerCardExchangeForm.excludePX
    ).subscribe(
      () => {
        this._customerPropagatorService.setCustomerId(this.customerDetails.customerId);
      },
      (error) => console.error(error)
    );
  }

}
