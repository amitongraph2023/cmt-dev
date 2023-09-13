import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

// Models
import { CustomerPaymentOptions } from '@models/customer-payment-options.model';
import { GiftCard } from '@models/gift-card.model';
import { BonusCard } from '@models/bonus-card.model';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerPaymentOptionsService } from '@services/customer-payment-options.service';
import { CustomerService } from '@services/customer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';
import { CustomerDetails } from '@models/customer-details.model';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-customer-payment-options',
  templateUrl: './customer-payment-options.component.html',
  styleUrls: ['./customer-payment-options.component.scss']
})
export class CustomerPaymentOptionsComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;

  public customerPaymentOptions: CustomerPaymentOptions;
  public newGiftCard: GiftCard;
  public newBonusCard: BonusCard;
  public successMessage: string;

  public canAddGC = false;
  public canViewAP = false;
  public canViewCampusCard = false;
  public canViewCatering = false;
  public canViewCC = false;
  public canViewGC = false;
  public canViewPP = false;
  public canViewBC = false;
  public canAddBC = false;

  private pageTitle = 'Customer Information';

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerPaymentOptionsService: CustomerPaymentOptionsService
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
  ) {}

  ngOnInit() {
    this._authService.checkCredentials().subscribe((canView) => {
      if (canView) {

        this._titlePropagatorService.setNewTitle(this.pageTitle);

        this._authService.checkComponentPrivilege('cust.pmt.ap')
          .subscribe( canViewAP => this.canViewAP = canViewAP);
        this._authService.checkComponentPrivilege('cust.pmt.campus_card')
          .subscribe(canViewCampusCard => this.canViewCampusCard = canViewCampusCard);
        this._authService.checkComponentPrivilege('cust.pmt.catering')
          .subscribe(canViewCatering => this.canViewCatering = canViewCatering);
        this._authService.checkComponentPrivilege('cust.pmt.cc')
          .subscribe(canViewCC => this.canViewCC = canViewCC);
        this._authService.checkComponentPrivilege('cust.pmt.gc')
          .subscribe(canViewGC => this.canViewGC = canViewGC);
        this._authService.checkComponentPrivilege('cust.pmt.gc.create')
          .subscribe(canAddGC => this.canAddGC = canAddGC);
        this._authService.checkComponentPrivilege('cust.pmt.pp')
          .subscribe(canViewPP => this.canViewPP = canViewPP);
		this._authService.checkComponentPrivilege('cust.pmt.bc')
          .subscribe(canViewBC => this.canViewBC = canViewBC);
        this._authService.checkComponentPrivilege('cust.pmt.bc.create')
          .subscribe(canAddBC => this.canAddBC = canAddBC);
      } else {
        this._customerPropagatorService.setCustomerId(null);
      }
    });
    this.loadCustomerPaymentOptions();
  }

  /**
   *  Add a new gift card by API call and refresh the payment options page
   */
  public addGiftCard() {
    $('#editGiftCardModal').modal('hide');
    this._customerPaymentOptionsService.addGiftCard(
      this.customerDetails.customerId
      , this.newGiftCard
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.loadCustomerPaymentOptions();
    });
  }

  /**
   * Click event handler to set newGiftCard to new instance, then call to open the editGiftCard modal
   */
  public clickAddNewGiftCard(): void {
    this.newGiftCard = new GiftCard();
    this._titlePropagatorService.setNewTitle('Add New Gift Card');
    const modalEl = $('#editGiftCardModal');
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Load customer payment options from an API call
   */
  public loadCustomerPaymentOptions(): void {
    this._customerPaymentOptionsService.getPaymentOptionsByCustomerId(this.customerDetails.customerId).subscribe(
      (customerPaymentOptions) => this.customerPaymentOptions = customerPaymentOptions,
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


  /**
   *  Add a new bonus card by API call and refresh the payment options page
   */
  public addBonusCard() {
    $('#editBonusCardModal').modal('hide');
    this._customerPaymentOptionsService.addBonusCard(
      this.customerDetails.customerId
      , this.newBonusCard
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.loadCustomerPaymentOptions();
    });
  }

  /**
   * Click event handler to set newBonusCard to new instance, then call to open the editBonusCard modal
   */
  public clickAddNewBonusCard(): void {
    this.newBonusCard = new BonusCard();
    this._titlePropagatorService.setNewTitle('Add New Bonus Card');
    const modalEl = $('#editBonusCardModal');
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

}
