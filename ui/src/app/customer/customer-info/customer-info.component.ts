import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import * as _ from 'lodash';
// Models
import { Customer } from '@models/customer.model';
import { CustomerDetails } from '@models/customer-details.model';
import { NewPassword } from '@models/new-password.model';
import { LoyaltyRewardsEnabled } from '@models/customer-loyalty-rewards-enabled.model';
// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPasswordService } from '@services/customer-password.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerService } from '@services/customer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';
import { CustomerLoyaltyService } from '@services/customer-loyalty.service';
// Enums
import { AccountActionType } from '@enums/account-action.enum';
import { CustomerEmailService } from '@services/customer-email.service';
import { CustomerEmail } from '@models/customer-email.model';
import { AccountStatusType, AccountStatusTypeMap } from '@enums/account-status-type.enum';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-customer-info',
  templateUrl: './customer-info.component.html',
  styleUrls: ['./customer-info.component.scss']
})
export class CustomerInfoComponent implements OnInit, OnChanges {
  @Input() customerDetails: CustomerDetails;

  public act_sts = false;
  public act_sts_suspend = false;
  public act_sts_terminate = false;
  public act_sts_reinstate = false;
  public act_sts_protect = false;
  public details = false;
  public details_edit = false;
  public password_generate = false;
  public password_send = false;
  public password_set_new = false;
  public social_integrations = false;
  public tax_exemptions = false;
  public update_username = false;

  public accountStatusTypeMap;
  public customerDetailsCopy: CustomerDetails;
  public customerEmail: CustomerEmail;
  public editable: boolean;
  public showNewPassword = false;
  public newPassword: string;

  private pageTitle = 'Customer Information';

  public statusActive = AccountStatusType[AccountStatusType.ACTIVE];
  public statusProtected = AccountStatusType[AccountStatusType.FORCE_RESET];
  public statusTerminated = AccountStatusType[AccountStatusType.TERMINATED];
  public statusLocked = AccountStatusType[AccountStatusType.LOCKED];
  public statusSuspended = AccountStatusType[AccountStatusType.SUSPENDED];
  public statusPending = AccountStatusType[AccountStatusType.PENDING];

  public account_pending = false;

  public rewardsEnabled: boolean;
  public rewardsEnabledDto: LoyaltyRewardsEnabled;
  public act_rewards_sts = false;

    constructor(private _authService: AuthenticationService
    , private _customerEmailService: CustomerEmailService
    , private _customerPasswordService: CustomerPasswordService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _route: ActivatedRoute
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
	, private _customerLoyaltyService: CustomerLoyaltyService
  ) {
    this.accountStatusTypeMap = AccountStatusTypeMap;
  }

  /**
   * Format the string passed in to a human readable date
   *
   * @param {string} stringDate The string containing a date passed in
   * @returns {string} The formatted string
   */
  private formatDate (stringDate: string): string {
    return new Date(stringDate.substr(0, 19)).toLocaleDateString();
  }
  ngOnInit() {
      this.customerDetails.accountCreationDate = this.formatDate(this.customerDetails.accountCreationDate);
      this.customerDetails.loyaltyCardNumber = null;
      this.account_pending = this.customerDetails.status.toString().toUpperCase() === this.statusPending.toString().toUpperCase();
      this._authService.checkCredentials().subscribe((canView) => {
        if ( canView ) {

        this._titlePropagatorService.setNewTitle(this.pageTitle);

        this._authService.checkComponentPrivilege('cust.inf.details')
          .subscribe(details => this.details = details);
        this._authService.checkComponentPrivilege('cust.inf.social_integrations')
          .subscribe(social_integrations => this.social_integrations = social_integrations);
        this._authService.checkComponentPrivilege('cust.inf.act_sts')
          .subscribe(act_sts => this.act_sts = act_sts);


          this._authService.checkComponentPrivilege('cust.inf.details.edit')
            .subscribe(details_edit => this.details_edit = details_edit);
          this._authService.checkComponentPrivilege('cust.inf.password.generate')
            .subscribe(password_generate => this.password_generate = password_generate);
          this._authService.checkComponentPrivilege('cust.inf.password.send')
            .subscribe(password_send => this.password_send = password_send);
          this._authService.checkComponentPrivilege('cust.inf.password.set_new')
            .subscribe(password_set_new => this.password_set_new = password_set_new);
          this._authService.checkComponentPrivilege('cust.inf.username.edit')
            .subscribe(update_username => this.update_username = update_username);
          this._authService.checkComponentPrivilege('cust.inf.act_sts.protect')
            .subscribe(act_sts_protect => this.act_sts_protect = act_sts_protect);
          this._authService.checkComponentPrivilege('cust.inf.act_sts.reinstate')
            .subscribe(act_sts_reinstate => this.act_sts_reinstate = act_sts_reinstate);
          this._authService.checkComponentPrivilege('cust.inf.act_sts.suspend')
            .subscribe(act_sts_suspend => this.act_sts_suspend = act_sts_suspend);
          this._authService.checkComponentPrivilege('cust.inf.act_sts.terminate')
            .subscribe(act_sts_terminate => this.act_sts_terminate = act_sts_terminate);
          this._authService.checkComponentPrivilege('cust.inf.tax_exemptions')
            .subscribe(tax_exemptions => this.tax_exemptions = tax_exemptions);
          this._authService.checkComponentPrivilege('cust.inf.act_sts.act_rewards_sts')
            .subscribe(act_rewards_sts => this.act_rewards_sts = act_rewards_sts);

        this._customerEmailService.getEmailsByCustomerId(this.customerDetails.customerId).subscribe(
          (customerEmails) => {
            this.customerEmail = customerEmails.filter(ce => ce.isDefault)[0];
          },
          (error) => console.error(error)
        );


		this._customerLoyaltyService.getLoyaltyRewardsEnabled(this.customerDetails.customerId).subscribe(
	      response => {
	        if (response) {
	          this.customerDetails.rewardsEnabled = response.isRewardsEnabled;

	        }
	      }
	    );

      } else {
        this._customerPropagatorService.setCustomerId(null);
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    this._customerEmailService.getEmailsByCustomerId(this.customerDetails.customerId).subscribe(
      (customerEmails) => {
        this.customerEmail = customerEmails.filter(ce => ce.isDefault)[0];
      },
      (error) => console.error(error)
    );


		this._customerLoyaltyService.getLoyaltyRewardsEnabled(this.customerDetails.customerId).subscribe(
	      response => {
	        if (response) {
	          this.customerDetails.rewardsEnabled = response.isRewardsEnabled;
	        }
	      }
	    );
  }

  /**
   * Reverts the customerDetails object and sets editable to false
   */
  public onClickCancelEditCustomerButton(): void {
    this.editable = false;
    this.customerDetails = _.cloneDeep(this.customerDetailsCopy);
    this.redirectToHome();
  }

  public onClickConfirmTerminateAccountButton(): void {
    const tempCustDetails = _.cloneDeep(this.customerDetails);
    tempCustDetails.lastName = tempCustDetails.lastName + '-delete me';
    this._customerService.updateCustomer(
      this.customerDetails.customerId
      , tempCustDetails
    ).subscribe( () => {}, (error) => console.log(error),
      () => {
        this._customerService.updateAccountByCustomerIdAndActionType(
          this.customerDetails.customerId, AccountActionType.TERMINATE).subscribe(
          response => {
            if (response) {
              this.openTerminateAccountSuccessModal();
            } else {
              this.openTerminateAccountFailedModal();
            }
          }
        );
      });
  }

  /**
   * Makes the userDetails form editable
   */
  public onClickEditCustomerButton(): void {
    this.editable = true;
  }
  /**
   * on Reset Password Button click, show the reset password modal
   */
  public onGenerateNewPasswordClick(): void {
    $('#generateNewPasswordModal').modal('show');
  }

  /**
   * on Send Password button click, do call to reset the password
   */
  public onSendForgotPasswordEmailClick(): void {
    this._customerPasswordService.sendResetPassword(this.customerDetails.customerId).subscribe(
      () => {
        $('#sendForgotPasswordSuccessModal').modal('show');
      },
      (error) => console.error(error)
    );
  }

  public onClickProtectButton(): void {
    this._customerService.updateAccountByCustomerIdAndActionType(
      this.customerDetails.customerId, AccountActionType.PROTECT).subscribe(
      response => {
        if (response) {
          this._customerPropagatorService.setCustomerId(this.customerDetails.customerId);
        } else {
          console.error('There was an error reinstating account id: ' + this.customerDetails.customerId);
        }
      }
    );
  }

  public onClickRegeneratePassword(): void {
     this.onVerifyResetPasswordClick();
  }

  public onClickReinstateButton(): void {
    this._customerService.updateAccountByCustomerIdAndActionType(
      this.customerDetails.customerId, AccountActionType.REINSTATE).subscribe(
      response => {
        if (response) {
          this._customerPropagatorService.setCustomerId(this.customerDetails.customerId);
        } else {
          console.error('There was an error reinstating account id: ' + this.customerDetails.customerId);
        }
      }
    );
  }

  public onClickSuspendButton(): void {
    this._customerService.updateAccountByCustomerIdAndActionType(
      this.customerDetails.customerId, AccountActionType.SUSPEND).subscribe(
      response => {
        if (response) {
          this._customerPropagatorService.setCustomerId(this.customerDetails.customerId);
        } else {
          console.error('There was an error suspending account id: ' + this.customerDetails.customerId);
        }
      }
    );
  }

  public onClickTerminateButton(): void {
    this.openTerminateAccountConfirmationModal();
  }

  public onClickCloseGeneratePasswordModal(): void {
    $('#generateNewPasswordModal').modal('hide');
    this._customerPropagatorService.setCustomerId(this.customerDetails.customerId);
  }

  public onClickCloseSendForgotPasswordSuccessModal(): void {
    $('#sendForgotPasswordSuccessModal').modal('hide');
    this.redirectToHome();
  }

  public onClickCloseSetNewPasswordSuccessModal(): void {
    $('#setNewPasswordSuccessModal').modal('hide');
    this._customerPropagatorService.setCustomerId(this.customerDetails.customerId);
  }

  public onClickCloseTerminateAccountSuccessModal(): void {
    $('#terminateAccountSuccessModal').modal('hide');
    this._customerPropagatorService.setCustomerId(null);
    this.redirectToHome();
  }

  /**
   * on Set Password click, call to get suggested password and open set password modal
   */
  public onSetPasswordClick(): void {
    $('#setPasswordModal').modal('show');
  }

  /**
   * on Submit button click in set password modal, do call to set password
   */
  public onSubmitNewPassword(): void {
    $('#setPasswordModal').modal('hide');
    this._customerPasswordService.adminSetPassword(this.customerDetails.customerId, new NewPassword(this.newPassword)).subscribe(
      (response) => {},
      (error) => console.error(error),
      () =>  {
        $('#setNewPasswordSuccessModal').modal('show');
      }
    );
  }

  /**
   * on verify reset password, display set new password to dispay in modal
   */
  public onVerifyResetPasswordClick(): void {
    this.showNewPassword = true;
    let returnedObject = null;
    this._customerPasswordService.resetPassword(this.customerDetails.customerId).subscribe(
      (retObject) => returnedObject = retObject,
      (error) => console.error(error),
      () => {
        this.newPassword = returnedObject.password;
      }
    );
  }

  public onResendEmailVerificationClick(): void {
    this._customerEmailService.resendVerficationEmail(this.customerDetails.customerId, this.customerEmail.emailAddress).subscribe(
      () => {},
      (error) => console.error(error)
    );
  }

  /**
   * Open success modal
   */
  public openSuccessModal(): void {
    this.editable = false;
    $('#successModal').modal('show');
  }

  public openTerminateAccountConfirmationModal(): void {
    $('#confirmTerminateModal').modal('show');
  }

  public openTerminateAccountFailedModal(): void {
    $('#confirmTerminateModal').modal('hide');
    $('#terminateAccountFailedModal').modal('show');
  }

  public openTerminateAccountSuccessModal(): void {
    $('#confirmTerminateModal').modal('hide');
    $('#terminateAccountSuccessModal').modal('show');
  }

  public redirectToHome(): void {
    this._router.navigate(['home']);
  }

  /**
   * Update the customer info from userDetails form and toggles editable back to false
   */
  public updateCustomerInfo(): void {
    this.editable = false;

    this._customerService.updateCustomer(
      this.customerDetails.customerId
      , Customer.fromCustomerDetails(this.customerDetails)
    ).subscribe( () => {}, (error) => console.log(error),
      () => {
        this.openSuccessModal();
      });
  }

  public onClickRewardsActivateButton(): void {
    this.openActivateRewardsConfirmationModal();
  }

  public onClickRewardsInactivateButton(): void {
    this.openInActivateRewardsConfirmationModal();
  }

  public openActivateRewardsConfirmationModal(): void {
    $('#confirmActivateRewardsModal').modal('show');
  }

  public openInActivateRewardsConfirmationModal(): void {
    $('#confirmInActivateRewardsModal').modal('show');
  }

  public onClickConfirmActivateRewardsButton(): void {
    $('#confirmActivateRewardsModal').modal('hide');
    this._customerPropagatorService.setCustomerId(this.customerDetails.customerId);
    this.onClickToggleRewardsButton();
  }


  public onClickConfirmInActivateRewardsButton(): void {
    $('#confirmInActivateRewardsModal').modal('hide');
    this._customerPropagatorService.setCustomerId(this.customerDetails.customerId);
    this.onClickToggleRewardsButton();
  }

  public onClickToggleRewardsButton(): void {
    this.customerDetails.rewardsEnabledDto = new LoyaltyRewardsEnabled();
    this.customerDetails.rewardsEnabledDto.isRewardsEnabled = !(this.customerDetails.rewardsEnabled);

    this._customerLoyaltyService.updateRewardsEnabledByCustomerId(
      this.customerDetails.customerId, this.customerDetails.rewardsEnabledDto).subscribe(
        response => {
          if (response) {
            this.customerDetails.rewardsEnabled = response['isRewardsEnabled'];
          }
        }
      );
  }
}
