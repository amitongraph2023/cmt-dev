import { Component, Input, OnInit } from '@angular/core';
import { CustomerDetails } from '@models/customer-details.model';
import { PaytronixService } from '@services/paytronix.service';
import { PaytronixBalance } from '@models/paytronix/paytronix-balance.model';

@Component({
  selector: 'app-paytronix-balance',
  templateUrl: './paytronix-balance.component.html',
  styleUrls: ['./paytronix-balance.component.scss']
})
export class PaytronixBalanceComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;

  public balance: PaytronixBalance;
  public expirations: string[] = [];

  constructor(private _paytronixService: PaytronixService) {}

  ngOnInit() {
    this._paytronixService.getBalance(this.customerDetails.loyaltyCardNumber).subscribe(
      (balance) => this.balance = balance,
      (error) => console.error(error)
    );
  }

}
