import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { CONSTANTS } from '../../constants';

// Models
import { CustomerPaymentOptions } from '@models/customer-payment-options.model';
import { CreditCard } from '@models/credit-card.model';
import { GiftCard } from '@models/gift-card.model';
import { PayPal } from '@models/paypal.model';
import { BonusCard } from '@models/bonus-card.model';

// Services
import { HttpService } from '@services/http.service';

// Enums
import { PaymentOptionType } from '@enums/payment-option-type.enum';
import { ApplePay } from '@models/apple-pay.model';


@Injectable()
export class CustomerPaymentOptionsService {

  constructor(private _httpService: HttpService) {}

  /**
   * Adds a gift card
   *
   * @param {number} customerId The id of the customer
   * @param {Observable<GiftCard>} giftCard The new GiftCard
   * @returns {Observable<{}>} Observable of empty set
   */
  public addGiftCard(customerId: number, giftCard: GiftCard): Observable<GiftCard> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.PAYMENT_OPTIONS.GIFT_CARD, giftCard, {
      customerId: customerId
    });
  }

  /**
   * Delete customer payment option from the server by customer id and payment option id
   *
   * @param {number} customerId The id of the customer
   * @param {PaymentOptionType} paymentOptionType The type of option to delete
   * @param {string} paymentOptionId The updated id of the paymentOption
   * @returns {Observable<{}>} Observable with empty set
   */
  public deletePaymentOptionByCustomerIdAndPaymentOptionType(
    customerId: number
    , paymentOptionType: PaymentOptionType
    , paymentOptionId: string
  ): Observable<{}> {
    return this._httpService.doDelete(CONSTANTS.API_ROUTES.CUSTOMER.PAYMENT_OPTIONS.BY_TYPE_AND_ID, {
      customerId: customerId
      , type: PaymentOptionType[paymentOptionType].toString().toLowerCase()
      , id: paymentOptionId
    });
  }

  /**
   * Get credit card info by token
   *
   * @param {number} customerId The id of the customer
   * @param {string} token The credit card token
   * @returns {Observable<CreditCard>} Observable of CreditCard
   */
  public getCreditCardByToken(customerId: number, token: string): Observable<CreditCard> {
    return this._httpService.doGet<CreditCard>(CONSTANTS.API_ROUTES.CUSTOMER.PAYMENT_OPTIONS.CREDIT_CARD_BY_TOKEN,
      {customerId: customerId, token: token});
  }

  /**
   * Get GiftCard by Id
   *
   * @param {number} customerId The id of the customer
   * @param {number} cardNumber The id of the gift card
   * @returns {Observable<GiftCard>} The GiftCard of that Id
   */
  public getGiftCardById(customerId: number, cardNumber: string): Observable<GiftCard> {
    return this._httpService.doGet<GiftCard>(CONSTANTS.API_ROUTES.CUSTOMER.PAYMENT_OPTIONS.BY_TYPE_AND_ID,
      {
        customerId: customerId
        , type: PaymentOptionType[PaymentOptionType.GIFTCARD].toString().toLowerCase()
        , id: cardNumber
      });
  }

  /**
   * Retrieves customer payment options from the server by customerId
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<PaymentOptions>} Observable with PaymentOptions
   */
  public getPaymentOptionsByCustomerId(customerId: number): Observable<CustomerPaymentOptions> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.CUSTOMER.PAYMENT_OPTIONS.BASE, {customerId: customerId});
  }

  /**
   * Get PayPal account by account number
   *
   * @param {number} customerId The id of the customer
   * @param {string} accountNumber The PayPal account number
   * @returns {Observable<PayPal>} Observable of PayPal
   */
  public getPayPalByAccountNumber(customerId: number, accountNumber: string ): Observable<PayPal> {
    return this._httpService.doGet<PayPal>(CONSTANTS.API_ROUTES.CUSTOMER.PAYMENT_OPTIONS.BY_TYPE_AND_ID,
      {
        customerId: customerId
        , type: PaymentOptionType[PaymentOptionType.PAYPAL].toString().toLowerCase()
        , id: accountNumber
      });
  }

  /**
   * Update credit card by token
   *
   * @param {number} customerId The id of the customer
   * @param {string} token The credit card token
   * @param {CreditCard} creditCard The updated credit card object
   * @returns {Observable<{}>} Observable of empty set
   */
  public updateCreditCardByToken(customerId: number, token: string, creditCard: CreditCard): Observable<{}> {
    return this._httpService.doPut(
      CONSTANTS.API_ROUTES.CUSTOMER.PAYMENT_OPTIONS.CREDIT_CARD_BY_TOKEN
      , creditCard
      , {
        customerId: customerId
        , token: token
      });
  }

  /**
   * Update gift card by Id
   *
   * @param {number} customerId The id of the customer
   * @param {number} cardNumber The id of the gift card
   * @param {GiftCard} giftCard The updated giftCard object
   * @returns {Observable<{}>} Observable of empty set
   */
  public updateGiftCardById(customerId: number, cardNumber: string, giftCard: GiftCard): Observable<{}> {
    console.log(cardNumber);
    return this._httpService.doPut(CONSTANTS.API_ROUTES.CUSTOMER.PAYMENT_OPTIONS.BY_TYPE_AND_ID, giftCard,
      {
        customerId: customerId
        , type: PaymentOptionType[PaymentOptionType.GIFTCARD].toString().toLowerCase()
        , id: cardNumber
      });
  }

  /**
   * Update PayPal account by account number
   *
   * @param {number} customerId The id of the customer
   * @param {string} accountNumber The PayPal account number
   * @param {PayPal} payPal The updated payPal object
   * @returns {Observable<{}>} Observable of empty set
   */
  public updatePayPalByAccountNumber(customerId: number, accountNumber: string, payPal: PayPal): Observable<{}> {
    return this._httpService.doPut(CONSTANTS.API_ROUTES.CUSTOMER.PAYMENT_OPTIONS.BY_TYPE_AND_ID, payPal,
      {
        customerId: customerId
        , type: PaymentOptionType[PaymentOptionType.PAYPAL].toString().toLowerCase()
        , id: accountNumber
      });
  }

  /**
   * Update ApplePay account by account number
   *
   * @param {number} customerId The id of the customer
   * @param {string} accountNumber The ApplePay account number
   * @param {ApplePay} applePay The updated applePay object
   * @returns {Observable<{}>} Observable of empty set
   */
  public updateApplePayByAccountNumber(customerId: number, accountNumber: string, applePay: ApplePay): Observable<{}> {
    return this._httpService.doPut(CONSTANTS.API_ROUTES.CUSTOMER.PAYMENT_OPTIONS.BY_TYPE_AND_ID, applePay,
      {
        customerId: customerId
        , type: PaymentOptionType[PaymentOptionType.APPLEPAY].toString().toLowerCase()
        , id: accountNumber
      });
  }


  /**
   * Adds a bonus card
   *
   * @param {number} customerId The id of the customer
   * @param {Observable<BonusCard>} bonusCard The new BonusCard
   * @returns {Observable<{}>} Observable of empty set
   */
  public addBonusCard(customerId: number, bonusCard: BonusCard): Observable<BonusCard> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.PAYMENT_OPTIONS.BONUS_CARD, bonusCard, {
      customerId: customerId
    });
  }


}
