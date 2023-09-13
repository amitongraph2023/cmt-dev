export class SearchCustomer {
  public customerId:	number;
  public defaultEmail:	string;
  public defaultPhone:	string;
  public firstName:	string;
  public lastName:	string;
  public username:	string;

  public static createInstance({ customerId, defaultEmail, defaultPhone, firstName, lastName, username }): SearchCustomer {
    return new SearchCustomer(customerId, defaultEmail, defaultPhone, firstName, lastName, username);
  }

  constructor(customerId, defaultEmail, defaultPhone, firstName, lastName, username) {
    this.customerId = customerId;
    this.defaultEmail = defaultEmail;
    this.defaultPhone = defaultPhone;
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
  }

}
