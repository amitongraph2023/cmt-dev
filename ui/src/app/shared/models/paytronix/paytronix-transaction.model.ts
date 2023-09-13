import { PaytronixTransactionDetail } from '@models/paytronix/paytronix-transaction-detail.model';

export class PaytronixTransaction {
  public storeName: string;
  public transactionIdLong: number;
  public transactionType: string;
  public details: PaytronixTransactionDetail[];
  public posTransactionNumber: string;
  public storeCode: string;
  public cardNumber: string;
  public datetime: string;
}
