import { Component, OnInit, ViewChildren } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
// Models
import { AuthRequest } from '@models/auth-request.model';
// Services
import { AuthenticationService } from '@services/authentication.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  @ViewChildren('username') username;

  public authRequest = new AuthRequest();

  private pageTitle = 'Customer Management Tool';

  constructor(private _authService: AuthenticationService,
              private _route: ActivatedRoute,
              private _titlePropagatorService: TitlePropagatorService) {}

  ngAfterViewInit(): void {
    if ( true ) {
      this.username.first.nativeElement.focus();
    }
  }

  ngOnInit() {
    this._titlePropagatorService.setNewTitle(this.pageTitle);
  }

  public onSubmit(): void {
    this._authService.login(this.authRequest, this._route.snapshot.params.r);
  }
}
