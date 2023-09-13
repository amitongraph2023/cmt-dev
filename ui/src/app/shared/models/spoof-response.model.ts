import * as moment from 'moment';
import _date = moment.unitOfTime._date;

export class SpoofResponse {
  public accessToken: string;
  public channelType: string;
  public customerId: number;
  public expirationDate: _date;
  public loginDate: _date;
  public loginType: string;
  public originSourceType: string;
  public proxyUser: string;
  public url: string;
  public username: string;

  public static createInstance(
    accessToken, channelType, customerId, expirationDate, loginDate, loginType, originSourceType, proxyUser, url, username
  ): SpoofResponse {
    return new SpoofResponse(
      accessToken, channelType, customerId, expirationDate, loginDate, loginType, originSourceType, proxyUser, url, username);
  }

  constructor(accessToken, channelType, customerId, expirationDate, loginDate, loginType, originSourceType, proxyUser, url, username) {
    this.accessToken = accessToken;
    this.channelType = channelType;
    this.customerId = customerId;
    this.expirationDate = expirationDate;
    this.loginDate = loginDate;
    this.loginType = loginType;
    this.originSourceType = originSourceType;
    this.proxyUser = proxyUser;
    this.url = url;
    this.username = username;
  }

}

