import { SocialIntegrationsModel } from '@models/social-integrations.model';
import { TaxExemption } from '@models/tax-exemption.model';
import { AccountStatusType } from '@enums/account-status-type.enum';
import { LoyaltyRewardsEnabled } from './customer-loyalty-rewards-enabled.model';


export class CustomerDetails {
  public customerId: number;
  public dob: string;
  public firstName: string;
  public isEmailGlobalOpt: boolean;
  public isDoNotShare: boolean;
  public isMobilePushOpt: boolean;
  public isSmsGlobalOpt: boolean;
  public lastName: string;
  public loyaltyCardNumber: string;
  public regCampaign: string;
  public regReferrer: string;
  public socialIntegration: SocialIntegrationsModel;
  public status: AccountStatusType;
  public taxExemptions: TaxExemption[];
  public username: string;
  public rewardsEnabled: boolean;
  public rewardsEnabledDto: LoyaltyRewardsEnabled;
  public accountCreationDate: string;

  constructor(
    customerId: number
    , dob: string
    , firstName: string
    , isEmailGlobalOpt: boolean
    , isDoNotShare: boolean
    , isMobilePushOpt: boolean
    , isSmsGlobalOpt: boolean
    , lastName: string
    , loyaltyCardNumber: string
    , regCampaign: string
    , regReferrer: string
    , socialIntegration: SocialIntegrationsModel
    , status: AccountStatusType
    , taxExemptions: TaxExemption[]
    , username: string
    , rewardsEnabled: boolean
    , rewardsEnabledDto: LoyaltyRewardsEnabled
    , accountCreationDate: string
  ) {
    this.customerId = customerId;
    this.dob = dob;
    this.firstName = firstName;
    this.isEmailGlobalOpt = isEmailGlobalOpt;
    this.isDoNotShare = isDoNotShare;
    this.isMobilePushOpt = isMobilePushOpt;
    this.isSmsGlobalOpt = isSmsGlobalOpt;
    this.lastName = lastName;
    this.loyaltyCardNumber = loyaltyCardNumber;
    this.regCampaign = regCampaign;
    this.regReferrer = regReferrer;
    this.socialIntegration = socialIntegration;
    this.status = status;
    this.taxExemptions = taxExemptions;
    this.username = username;
    this.rewardsEnabled = rewardsEnabled;
    this.rewardsEnabledDto = new LoyaltyRewardsEnabled();
    this.accountCreationDate = accountCreationDate;
  }
}

