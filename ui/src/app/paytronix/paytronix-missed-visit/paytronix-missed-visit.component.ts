import { Component, Input } from '@angular/core';
import { CustomerDetails } from '@models/customer-details.model';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerMissedVisitService } from '@services/customer-missed-visit.service';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-paytronix-missed-visit',
  templateUrl: './paytronix-missed-visit.component.html',
  styleUrls: ['./paytronix-missed-visit.component.scss']
})
export class PaytronixMissedVisitComponent {
  @Input() customerDetails: CustomerDetails;

  public missedVisitCodeValue = '';

  constructor(private _customerPropagatorService: CustomerPropagatorService
    , private _customerMissedVisitService: CustomerMissedVisitService) {
  }

  public onSubmitMissedVisit() {
    this._customerMissedVisitService.missedVisit(this.customerDetails.customerId, this.missedVisitCodeValue).subscribe(
      () => {
        $('#redeemSuccessModal').modal('show');
      },
      (error) => console.error(error)
    );
  }

  public onClickCloseRedeemSuccessModal(): void {
    $('#redeemSuccessModal').modal('hide');
  }

}
