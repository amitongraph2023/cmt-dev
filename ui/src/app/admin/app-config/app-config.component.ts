import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/observable/of';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { isNullOrUndefined } from 'util';

import * as _ from 'lodash';

// Enums
import { AppConfigServiceType } from '@enums/app-config-service-type.enum';
import { AppConfigSortColumn } from '@enums/sort/app-config-sort-column.enum';
import { SortDirection } from '@enums/sort/sort-direction.enum';
// Models
import { AppConfig } from '@models/app-config.model';
import { Page } from '@components/pagination/pagination.component';
// Services
import { AppConfigService } from '@services/app-config.service';
import { AuthenticationService } from '@services/authentication.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Declare $ as jQuery
declare var $: any;

const CMT_PAGE_SIZE = 12;
const OTHER_PAGE_SIZE = 100;
const SORT_COL_KEY = 'AppConfigSortColumn';
const SORT_DIR_KEY = 'AppConfigSortDirection';

@Component({
  selector: 'app-admin-app-config',
  templateUrl: './app-config.component.html',
  styleUrls: ['./app-config.component.scss']
})
export class AdminAppConfigComponent implements OnDestroy, OnInit {

  public serviceTypes = AppConfigServiceType;
  public service: string;

  public consented = false;
  public appConfig: AppConfig = new AppConfig();
  public appConfigs$: Observable<any>;
  public appConfigs: AppConfig[];
  public isLoading = true;
  public isNew = false;
  public isSaved = false;
  public isSaving = false;
  public originalAppConfig: AppConfig;
  public query: string;
  public response: Page;
  public responseModalParams = {action: '', property: <AppConfig>null};
  public sortCol: AppConfigSortColumn = AppConfigSortColumn.CODE;
  public sortColEnum;
  public sortDir: SortDirection = SortDirection.ASC;
  public sortDirEnum;

  private pageTitle = 'Application Config';
  private preemptModalCleanup = false;
  private searchText = new Subject<string>();

  constructor(private _appConfigService: AppConfigService,
              private _authService: AuthenticationService,
              private _route: ActivatedRoute,
              private _router: Router,
              private _titlePropagatorService: TitlePropagatorService) {
    this.sortColEnum = AppConfigSortColumn;
    this.sortDirEnum = SortDirection;
  }

  ngOnDestroy(): void {
    $('.modal').remove();
  }

  ngOnInit() {

    if (!isNullOrUndefined(localStorage.getItem(SORT_COL_KEY))) {
      this.sortCol = AppConfigSortColumn[localStorage.getItem(SORT_COL_KEY)];
    }
    if (!isNullOrUndefined(localStorage.getItem(SORT_DIR_KEY))) {
      this.sortDir = SortDirection[localStorage.getItem(SORT_DIR_KEY)];
    }

    this._authService.checkCredentials().subscribe((canView) => {
      if (canView) {
        this._titlePropagatorService.setNewTitle(this.pageTitle);

        this.service = this.getRouteParam('service');
        if (isNullOrUndefined(this.service)) {
          this.service = AppConfigServiceType[AppConfigServiceType.CMT];
        }

        const page = this.getRouteParam('p');
        if (isNullOrUndefined(page)) {
          this.goToPage(1, this.service);
        }
      }
    });



    this._route.params.subscribe(params => {
      if (!isNullOrUndefined(params.q)) {
        this.query = params.q;
      }
      if (!isNullOrUndefined(params.p)) {
        this.getByPage(+params.p).subscribe(data => this.extractPagePayload(data));
      }
    });

    // Subscribe to the searchText Subject
    this.appConfigs$ = this.searchText.pipe(
      debounceTime(500), // Delay and wait to see if any other keys were pressed
      distinctUntilChanged(), // Ignore the new value if it is the same as the last value
      switchMap((query: string) => { // Switch to the newest observable request
        if (query.trim()) {
          // Search for the query
          this.goToPage(1, (query.trim()) ? query : null);
        } else {
          this.query = null;
          this.goToPage(1, this.service);
        }
        return Observable.of('');
      }),
    );
  }

  public onSelectedServiceChange(): void {
    this.goToPage(1, this.service);
  }

  /**
   * Closes the modal and resets the modal's properties
   */
  public closeAppConfigModal(): void {
    $('#appConfigModal').modal('hide');
    this.cleanUpAppConfigModal();
  }

  /**
   * Deletes the passed in property and then displays the success modal
   *
   * @param {AppConfig} property The AppConfig property to delete
   * @param {boolean} consented The consented status
   */
  public deleteProperty(property: AppConfig, consented = false): void {
    const modalEl = $('#responseModal');
    const _self = this;
    this.consented = consented;

    if (consented) {
      this._appConfigService.deleteProperty(property.id, this.service).subscribe(() => {
        this.responseModalParams = {action: 'deleted', property: property};
        this.goToPage(this.response.number + 1, this.query);

        modalEl.on('hidden.bs.modal', function () {
          _self.cleanUpAppConfigModal();
        });

      });
    } else {
      this.responseModalParams = {action: 'Delete', property: property};
      modalEl.appendTo('body').modal('show').css('transform', 'translateX(125px)');
      modalEl.on('hidden.bs.modal', function () {
        _self.cleanupResponseModal();
      });
    }
  }

  /**
   * Opens the modal with the selected app config
   *
   * @param {AppConfig} property The property to edit
   */
  public editProperty(property: AppConfig): void {
    if (!isNullOrUndefined(property)) {
      this.appConfig = _.cloneDeep(property);
      this.originalAppConfig = _.cloneDeep(property);
      this.isNew = false;

      this.openAppConfigModal();
    }
  }

  /**
   * Creates an object with the appropriate classes for the sorting icon
   *
   * @param {AppConfigSortColumn} col The column to get the sort icon for
   * @returns {any} The classes object
   */
  public getSortClasses(col: AppConfigSortColumn): any {
    const classes = {fa: true};

    if (this.sortCol === col) {
      if (this.sortDir === SortDirection.ASC) {
        classes['fa-sort-alpha-asc'] = true;
      } else {
        classes['fa-sort-alpha-desc'] = true;
      }
    } else {
      classes['fa-sort'] = true;
    }

    return classes;
  }

  /**
   * Handles the clicking of the pagination strip and calls the service to get the new page
   *
   * @param {number} pageNum The page to go to
   * @param {string} service The service to show in appConfig
   * @param {string} query The query to search (Optional)
   */
  public goToPage(pageNum: number, service: string = this.service, query: string = null): void {
    const params = {p: pageNum, service: service};

    if (!isNullOrUndefined(query)) {
      params['q'] = query;
    }

    this._router.navigate(['/admin/app-config', params]);
  }

  /**
   * Opens the modal with a new empty app config
   */
  public newProperty(): void {
    this.appConfig = new AppConfig();
    this.isNew = true;
    this.openAppConfigModal();
  }

  /**
   * Opens the modal to view/edit an AppConfig property
   */
  public openAppConfigModal(): void {
    if (!isNullOrUndefined(this.appConfig)) {
      if (isNullOrUndefined(this.appConfig.code)) {
        this._titlePropagatorService.setNewTitle('Create Property');
      } else {
        this._titlePropagatorService.setNewTitle('Edit Property: ' + this.appConfig.code);
      }

      const modalEl = $('#appConfigModal');
      const _self = this;
      modalEl.appendTo('body')
        .modal({
          focus: true
        })
        .css('transform', 'translateX(125px)');
      modalEl.on('hidden.bs.modal', function () {
        _self.cleanUpAppConfigModal();
      });
    }
  }

  /**
   * Creates or updates an app config property
   */
  public save(): void {
    // Do not upsert if the object has not been changed
    if (!_.isEqual(this.appConfig, this.originalAppConfig)) {
      this.isSaving = true;

      if (isNullOrUndefined(this.appConfig.id)) {
        this._appConfigService.addProperty(this.appConfig, this.service)
          .subscribe(() => {
            this.isSaving = false;
            this.isSaved = true;
          });
      } else {
        this._appConfigService.updateAppConfig(this.appConfig.id, this.appConfig, this.service)
          .subscribe(() => {
            this.isSaving = false;
            this.isSaved = true;
          });
      }
    } else {
      this.isSaving = false;
    }
  }

  /**
   * Handles the value changing of the search box by passing it off to the searchText Subject
   *
   * @param {string} query The new query string
   */
  public search(query: string): void {
    this.searchText.next(query);
  }

  /**
   * Handles the clicking of the sortable table headers
   *
   * @param {AppConfigSortColumn} col The column clicked
   */
  public sort(col: AppConfigSortColumn): void {
    let dir = SortDirection.ASC;
    if (col === this.sortCol) {
      if (this.sortDir === SortDirection.ASC) {
        dir = SortDirection.DESC;
      } else {
        dir = SortDirection.ASC;
      }
    }

    this.getByPage(1, dir, col).subscribe(data => this.extractPagePayload(data));
  }

  /**
   * Cleans up after the modal by resetting all parameters
   */
  private cleanUpAppConfigModal(): void {
    if (!this.preemptModalCleanup) {
      this.appConfig = new AppConfig();
      this.isNew = false;
      this.isSaved = false;
      this.isSaving = false;
      this.originalAppConfig = null;
      this.getByPage(this.response.number + 1).subscribe(data => this.extractPagePayload(data));
    }
  }

  /**
   * Cleans up after the response modal has been closed
   */
  private cleanupResponseModal(): void {
    this.consented = false;
    this.responseModalParams = {action: '', property: <AppConfig>null};
    this.goToPage(this.response.number + 1, this.service);
  }

  /**
   * Parses the response and sets the appropriate params
   *
   * @param {Page} data The response data
   */
  private extractPagePayload(data: Page): void {
    this._titlePropagatorService.setNewTitle(this.pageTitle + ': Page ' + (data.number + 1) + ' of ' + data.totalPages);
    this.appConfigs = data.content;
    this.response = data;
    this.isLoading = false;

    this.sortCol = this.sortColEnum[data.sortCol.toUpperCase()];
    this.sortDir = this.sortDirEnum[data.sortDir.toUpperCase()];
    this.setSort();
  }

  /**
   * Gets by the passed in page number and this.query
   *
   * @param {number} page The page number to get
   * @param {SortDirection} dir The direction to sort
   * @param {AppConfigSortColumn} col The column to sort
   * @returns {Observable<any>} The page response from the server
   */
  private getByPage(page: number, dir: SortDirection = null, col: AppConfigSortColumn = null): Observable<any> {
    if (isNullOrUndefined(dir)) {
      dir = this.sortDir;
    }
    if (isNullOrUndefined(col)) {
      col = this.sortCol;
    }

    const pageSize = this.service.toUpperCase() === 'CMT' ? CMT_PAGE_SIZE : OTHER_PAGE_SIZE;

    return this._appConfigService.getPropertiesPaged(this.query, page, pageSize, dir, col, this.service);
  }

  /**
   * Gets and returns a route param from the route snapshot for given key
   *
   * @param {string} key The key of the param to get
   * @returns {string} The param's value or undefined
   */
  private getRouteParam(key: string): string {
    return this._route.snapshot.paramMap.get(key);
  }

  /**
   * Sets the local storage value of the current sort
   */
  private setSort(): void {
    localStorage.setItem(SORT_COL_KEY, this.sortColEnum[this.sortCol]);
    localStorage.setItem(SORT_DIR_KEY, SortDirection[this.sortDir]);
  }
}
