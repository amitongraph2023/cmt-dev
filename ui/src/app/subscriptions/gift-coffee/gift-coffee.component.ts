import { Component, OnInit } from '@angular/core';

// Enums
import { CoffeeSubscriptionSearchMap, CoffeeSubscriptionSearchType } from '@enums/coffee-subscription-search-type.enum';
import { GiftCoffeeSubscription } from '@models/gift-coffee-subscription.model';
import { GiftCoffeeSubscriptionService } from '@services/gift-coffee-subscription.service';
import { SubscriptionServiceResults } from '@models/subscription-service-results.model';

// Declare $ as jQuery
declare var $: any;


@Component({
  selector: 'app-gift-coffee',
  templateUrl: './gift-coffee.component.html',
  styleUrls: ['./gift-coffee.component.scss']
})
export class GiftCoffeeComponent implements OnInit {

  public searchTypes = CoffeeSubscriptionSearchType;

  public searchType = 'GIFT_CODE';
  public searchTerm: String;

  public results: SubscriptionServiceResults;

  constructor(private _giftCoffeeSubscriptionService: GiftCoffeeSubscriptionService) { }

  ngOnInit() {
  }

  public search(): void {
    this._giftCoffeeSubscriptionService.searchGiftCoffeeSubscriptions(this.searchType.toString(), this.searchTerm).subscribe(
      (results) => this.results = results,
      (error) => console.error()
    );
  }

}
