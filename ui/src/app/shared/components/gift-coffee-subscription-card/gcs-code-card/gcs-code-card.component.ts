import { Component, Input, OnInit } from '@angular/core';
import { GiftCode } from '@models/gift-code.model';
import { PngService } from '@services/png.service';
import { GiftCoffeeSubscription } from '@models/gift-coffee-subscription.model';
import { AuthenticationService } from '@services/authentication.service';
import { GiftCoffeeEmail } from '@models/gift-coffee-email.model';

@Component({
  selector: 'app-gcs-code-card',
  templateUrl: './gcs-code-card.component.html',
  styleUrls: ['./gcs-code-card.component.scss']
})
export class GcsCodeCardComponent implements OnInit {

  @Input() giftCode: GiftCode;
  @Input() giftCoffeeSubscription: GiftCoffeeSubscription;

  public resend: boolean;
  private giftCoffeeEmail: GiftCoffeeEmail;

  constructor(private _authService: AuthenticationService,
              private _pngService: PngService) {
  }

  ngOnInit() {
    this._authService.checkComponentPrivilege('cof.gift.resend')
      .subscribe(details => this.resend = details);
  }

  public clickResendEmail(): void {
    this.giftCoffeeEmail.code = this.giftCode.code;
    this.giftCoffeeEmail.purchaserEmail =  'jason.houk@panerabread.com'; //this.giftCoffeeSubscription.purchaserEmail;
    this.giftCoffeeEmail.customerId = this.giftCoffeeSubscription.customerId;
    this.giftCoffeeEmail.program = this.giftCode.program;
    this.giftCoffeeEmail.description = this.giftCode.description;
    this.giftCoffeeEmail.purchaseOrderId = this.giftCoffeeSubscription.purchaseOrderId;

    this._pngService.resendGiftCoffeeSubscription(this.giftCode.code, this.giftCoffeeEmail).subscribe(
      () => {},
      error => console.error(error)
    );
  }

}
