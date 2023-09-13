import { CustomerPreferenceType } from '@enums/customer-preference-type.enum';

export class GeneralPreferencesValue {
 public code: number;
 public displayName: string;
 public type: CustomerPreferenceType;
 public sortKey: number;
}
