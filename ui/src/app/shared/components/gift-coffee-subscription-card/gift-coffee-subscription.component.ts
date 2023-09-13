import { Component, Input, OnInit } from '@angular/core';
import { GiftCoffeeSubscription } from '@models/gift-coffee-subscription.model';

@Component({
  selector: 'app-gift-coffee-subscription',
  templateUrl: './gift-coffee-subscription.component.html',
  styleUrls: ['./gift-coffee-subscription.component.scss']
})
export class GiftCoffeeSubscriptionComponent implements OnInit {

  @Input() giftCoffeeSubscription: GiftCoffeeSubscription;

  constructor() { }

  ngOnInit() {
  }

}
