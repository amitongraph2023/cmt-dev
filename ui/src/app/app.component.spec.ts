import { TestBed, async } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';

import { AppComponent } from './app.component';
import { NavbarComponent } from '@components/navbar/navbar.component';

import { PipesModule } from './shared/pipes/pipes.module';

import { AuthenticationService } from '@services/authentication.service';
import { ErrorPropagatorService } from '@services/error-propagator.service';
import { LoadingPropagatorService } from '@services/loading-propagator.service';
import { SessionTimerService } from '@services/session-timer.service';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,

        NavbarComponent
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([]),

        PipesModule
      ],
      providers: [
        AuthenticationService,
        ErrorPropagatorService,
        LoadingPropagatorService,
        SessionTimerService
      ]
    }).compileComponents();
  }));
  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));
  it('should render title', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('.sidebar-header').querySelector('span').textContent).toContain('Customer Management Tool');
  }));
});
