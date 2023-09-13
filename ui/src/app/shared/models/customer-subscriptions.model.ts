import { CustomerSubscription } from '@models/customer-subscription.model';
import { CustomerSubscriptionSuppressor } from '@models/customer-subscription-suppressor.model';

export class CustomerSubscriptions {
  public subscriptions:	CustomerSubscription[];
  public suppressors:	CustomerSubscriptionSuppressor[];
}
