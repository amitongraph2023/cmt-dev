import { LtoCafe } from '@models/lto-cafe.model';

export class LTO {
  public specialCode: string;
  public discount: string;
  public businessUnit: string;
  public startDateTime: string;
  public endDateTime: string;
  public cafes: LtoCafe[];

  constructor(
    specialCode: string
    , discount: string
    , businessUnit: string
    , startDateTime: string
    , endDateTime: string
    , cafes: LtoCafe[]
  ) {
    this.specialCode = specialCode;
    this.discount = discount;
    this.businessUnit = businessUnit;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.cafes = cafes;
  }
}
