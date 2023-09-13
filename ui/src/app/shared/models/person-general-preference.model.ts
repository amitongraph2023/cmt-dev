export class PersonGeneralPreference {
  public code:	number;
  public displayName:	string;
  public value?: boolean;

  constructor(code: number, displayName: string, value?: boolean) {
    this.code = code;
    this.displayName = displayName;
    this.value = value;
  }
}


