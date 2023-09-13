import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { CustomerDetails } from '@models/customer-details.model';
import { CustomerPropagatorService } from '@services/customer-propagator.service';

@Component({
  selector: 'app-lto',
  templateUrl: './lto.component.html',
  styleUrls: ['./lto.component.scss']
})
export class LtoComponent implements OnInit {
  public loading = false;
  public customerDetails: CustomerDetails;

  private navigationSubscription;

  constructor(
    private _customerPropagatorService: CustomerPropagatorService
    , private _route: ActivatedRoute
    , private _router: Router
  ) {
    this._customerPropagatorService.customer$.subscribe(
      customerDetails => {
        this.customerDetails = customerDetails;
      }
    );
    this.navigationSubscription = this._router.events.subscribe((e: any) => {
      if ( e instanceof NavigationEnd ) {
        this.customerDetails = this._customerPropagatorService.getCustomer();
      }
    });
  }

  ngOnInit() {
  }

  public isRoute(route: string): boolean {
    return this._router.url === '/lto/' + route;
  }
}
