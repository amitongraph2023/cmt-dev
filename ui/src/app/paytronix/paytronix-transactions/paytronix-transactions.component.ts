import { Component, Input, OnInit } from '@angular/core';
import { CustomerDetails } from '@models/customer-details.model';
import { PaytronixService } from '@services/paytronix.service';
import { PaytronixTransaction } from '@models/paytronix/paytronix-transaction.model';
import { PaytronixWallet } from '@models/paytronix/paytronix-wallet.model';
import { PaytronixTransactionDetail } from '@models/paytronix/paytronix-transaction-detail.model';
import { isNullOrUndefined } from 'util';
import { PaytronixDayOption } from '@models/paytronix/paytronix-dayOption.model';

@Component({
  selector: 'app-paytronix-transactions',
  templateUrl: './paytronix-transactions.component.html',
  styleUrls: ['./paytronix-transactions.component.scss']
})
export class PaytronixTransactionsComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;

  public config: any;
  public days = 30;
  public daysOptions: PaytronixDayOption[] = [
    { name: '1 Month', value: 30 },
    { name: '6 Months', value: 180},
    { name: '1 Year', value: 365} ];
  public transactions: PaytronixTransaction[];

  constructor(private _paytronixService: PaytronixService) {
    this.config = {
      itemsPerPage: 5,
      currentPage: 1,
      totalItems: 0
    };
  }

  ngOnInit() {
  this.getTransactions();
  }

  public pageChanged(event): void {
    this.config.currentPage = event;
  }

  public formatTransactionHistories(rawTransactions: PaytronixTransaction[], wallets: PaytronixWallet[]): PaytronixTransaction[] {
    let transactions: PaytronixTransaction[] = [];
    if (wallets && wallets.length > 0) {
      for (const transaction of rawTransactions) {
        if (transaction.transactionType !== 'Identify Customer') {
          const details: PaytronixTransactionDetail[] = [];
          for ( const detail of transaction.details ) {
            detail.walletName = this.getWalletCode(wallets, detail.walletCode);
            details.push(detail);
          }
          transaction.details = details;
          transactions.push(transaction);
        }
      }
    } else {
      transactions = rawTransactions;
    }
    return transactions;
  }

  public onDayOptionChange($event: any): void {
    this.days = $event.target.value;
    this.getTransactions();
  }

  private getStartDate(days: number) {
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - days);
    return startDate.getFullYear() + '-' + (startDate.getMonth() + 1) + '-' + startDate.getDate();
  }

  private getTransactions(): void {
    let rawTransactions: PaytronixTransaction[];
    let wallets: PaytronixWallet[];
    this._paytronixService.getTransactionHistory(this.customerDetails.loyaltyCardNumber, this.getStartDate(this.days)).subscribe(
      (transactions) => rawTransactions = transactions.transactions,
      (error) => console.error(error),
      () => {
        this._paytronixService.getWalletCodes().subscribe(
          (rawWallets) => wallets = rawWallets.wallets,
          (error) => console.error(error),
          () => {
            this.transactions = this.formatTransactionHistories(rawTransactions, wallets);
            this.config.totalItems = this.transactions.length;
          }
        );
      }
    );
  }

  private getWalletCode(wallets: PaytronixWallet[], walletCode: number): string {
    const wallet = wallets.filter(w => w.walletCode === walletCode)[0];
    if (isNullOrUndefined(wallet)) {
      return '';
    } else {
      return wallet.walletName;
    }
  }
}
