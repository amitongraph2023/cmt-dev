import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { isNullOrUndefined } from 'util';

// Models
import { CustomerPreferences } from '@models/customer-preferences.model';
import { PersonGeneralPreference } from '@models/person-general-preference.model';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPreferencesService } from '@services/customer-preferences.service';
import { CustomerService } from '@services/customer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Enums
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerDetails } from '@models/customer-details.model';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-customer-preferences',
  templateUrl: './customer-preferences.component.html',
  styleUrls: ['./customer-preferences.component.scss']
})
export class CustomerPreferencesComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;

  public customerPreferences: CustomerPreferences;
  public allFoodPreferences: PersonGeneralPreference[];
  public allGatherPreferences: PersonGeneralPreference[];
  public isFoodPreferencesNull: boolean;
  public isGatherPreferencesNull: boolean;
  public successMessage: string;

  public canViewGatherPrefs = false;
  public canUpdateGatherPrefs = false;
  public canViewFoodPrefs = false;
  public canUpdateFoodPrefs = false;

  private pageTitle = 'Customer Preferences';

  constructor(private _authService: AuthenticationService
    , private _customerService: CustomerService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerPreferencesService: CustomerPreferencesService
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
  ) {}

  ngOnInit() {
    this._authService.checkCredentials().subscribe((canView) => {
      if (canView) {

        this._titlePropagatorService.setNewTitle(this.pageTitle);

        this._authService.checkComponentPrivilege('cust.prefs.gather')
          .subscribe(canViewGatherPrefs => this.canViewGatherPrefs = canViewGatherPrefs);
        this._authService.checkComponentPrivilege('cust.prefs.gather.update')
          .subscribe(canUpdateGatherPrefs => this.canUpdateGatherPrefs = canUpdateGatherPrefs);
        this._authService.checkComponentPrivilege('cust.prefs.food')
          .subscribe(canViewFoodPrefs => this.canViewFoodPrefs = canViewFoodPrefs);
        this._authService.checkComponentPrivilege('cust.prefs.food.update')
          .subscribe(canUpdateFoodPrefs => this.canUpdateFoodPrefs = canUpdateFoodPrefs);

      } else {
        this._customerPropagatorService.setCustomerId(null);
      }
    });

    if (!isNullOrUndefined(this.customerDetails)) {
      this._customerPreferencesService.getCustomerPreferences(this.customerDetails.customerId).subscribe(
        (customerPreferences) => this.customerPreferences = customerPreferences,
        (error) => console.error(error),
        () => {
          this.allFoodPreferences = this._customerPreferencesService.getAllFoodPreferences(this.customerPreferences.foodPreferences);
          this.allGatherPreferences = this._customerPreferencesService.getAllGatherPreferences();
          this.isFoodPreferencesNull = isNullOrUndefined(this.customerPreferences.foodPreferences);
          this.isGatherPreferencesNull = isNullOrUndefined(this.customerPreferences.gatherPreference);
        }
      );
    } else {
      this._customerPropagatorService.setCustomerId(null);
    }
  }

  /**
   * Event handler for the food preferences checkboxes
   *
   * @param {any} $event The event passed by the the handler
   */
  public checkChangeFoodPrefsHandler($event: any): void {
    this.allFoodPreferences[
      this.allFoodPreferences.indexOf(
        this.allFoodPreferences.filter( pref => pref.code === Number($event.target.value))[0]
      )].value = !(this.allFoodPreferences[
      this.allFoodPreferences.indexOf(
        this.allFoodPreferences.filter( pref => pref.code === Number($event.target.value))[0]
      )].value);
  }

  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal').modal('show');
  }

  /**
   * Event handler for the gather prferences radio buttons
   *
   * @param {any} $event The event passed by the the handler
   */
  public radioChangeGatherPrefsHandler($event: any): void {
    this.customerPreferences.gatherPreference =
      this.allGatherPreferences.filter( pref => pref.code === Number($event.target.value))[0];
  }

  /**
   * Update the food preferences via an api call
   */
  public updateFoodPreferences(): void {
    this._customerPreferencesService.updateCustomerFoodPreferences(
      this.customerDetails.customerId,
      this.allFoodPreferences.filter( pref => pref.value === true)
    ).subscribe( () => {}, error => error, () => {
      this.openSuccessModal('Food preferences updated successfullly');
    });
  }

  /**
   * Update the gather preferences via an api call
   */
  public updateGatherPreferences(): void {
    this._customerPreferencesService.updateCustomerGatherPreferences(
      this.customerDetails.customerId
      , this.customerPreferences.gatherPreference
    ).subscribe( () => {} , error => error, () => {
      this.openSuccessModal('Gather preference updated successfully');
    });
  }
}
