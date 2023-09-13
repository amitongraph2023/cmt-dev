package com.panera.cmt.config;

import com.panera.cmt.enums.sort.AppConfigSortColumn;
import org.springframework.data.domain.Sort;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Constants {

    public static final String APP_CONFIG_AUDIT_RETENTION_TIME_AMOUNT = "audit.retention.time_amount";
    public static final String APP_CONFIG_AUDIT_RETENTION_TIME_UNIT = "audit.retention.time_unit";
    public static final String APP_CONFIG_AUTH_GROUP = "auth.group";
    public static final String APP_CONFIG_GIFT_COFFEE_TEMPLATE_ID = "template_id.gift_coffee_subscription";
    public static final String APP_CONFIG_UI_ANONYMOUS_ORDER_URL = "ui.anonymous_order_url";
    public static final String APP_CONFIG_UI_PERMISSIONS = "ui.permissions";
    public static final String APP_CONFIG_UI_ROUTE_MAP = "ui.route.map";
    public static final String APP_CONFIG_UI_ROUTE_WHITELIST = "ui.route.whitelist";
    public static final String APP_CONFIG_CATERING_REDIRECT = "catering.redirect";

    public static final String CACHE_APP_CONFIG = "AppConfig";
    public static final String CACHE_APP_CONFIG_AUTH_GROUPS = "AppConfigAuthGroups";
    public static final String CACHE_APP_CONFIG_ALL_AUTH_GROUPS = "AppConfigAllAuthGroups";
    public static final String CACHE_APP_CONFIG_SEARCH = "AppConfigSearch";
    public static final String CACHE_APP_UI_GENERIC = "AppConfigUIGeneric";
    public static final String CACHE_APP_UI_ROUTE_WHITELISTS = "AppConfigUIRoutesWhiteList";
    public static final String CACHE_APP_UI_PERMISSION_WHITELIST = "AppConfigUIPermissionWhiteList";

    public static final String AUDIT_SUBJECT_ADDRESS = "address";
    public static final String AUDIT_SUBJECT_CARD_EXCHANGE = "cardExchange";
    public static final String AUDIT_SUBJECT_CUSTOMER = "customer";
    public static final String AUDIT_SUBJECT_EMAIL = "email";
    public static final String AUDIT_SUBJECT_LMT = "lmt";
    public static final String AUDIT_SUBJECT_MFA = "mfa";
    public static final String AUDIT_SUBJECT_MISSED_VISIT = "missedVisit";
    public static final String AUDIT_SUBJECT_PASSWORD = "password";
    public static final String AUDIT_SUBJECT_PAYMENT_OPTION = "paymentOption";
    public static final String AUDIT_SUBJECT_PAYTRONIX = "paytronix";
    public static final String AUDIT_SUBJECT_PHONE = "phone";
    public static final String AUDIT_SUBJECT_PREFERENCES = "preferences";
    public static final String AUDIT_SUBJECT_PNG = "png";
    public static final String AUDIT_SUBJECT_SOCIAL_INTEGRATIONS = "socialIntegrations";
    public static final String AUDIT_SUBJECT_SPOOFING = "spoofing";
    public static final String AUDIT_SUBJECT_SUBSCRIPTIONS = "subscriptions";

    public static final int AUDIT_EXPIRATION_DEFAULT_UNIT = 45;

    public static final AppConfigSortColumn SORT_COL_APP_CONFIG = AppConfigSortColumn.CODE;

    public static final Sort.Direction SORT_DIR_APP_CONFIG = Sort.Direction.ASC;

    public static final String CHUB_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss,SSSZ";
    public static final String DATE_FORMAT = "MM/dd/yyyy";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss ZZ";
    public static final String PLATFORM_CODE = "CMT";
    public static final String SSO_COOKIE_NAME = "accessToken";
    public static final String SSO_SPOOF_COOKIE_NAME = "ssoToken";
    public static final String APP_CONFIG_CODE_APIGEE_BEARER_TOKEN = "apigee.bearer_token";

    public static SimpleDateFormat getChubDateTimeFormat() {
        return new SimpleDateFormat(CHUB_DATE_TIME_FORMAT);
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT);
    }

    public static SimpleDateFormat getDateTimeFormat() {
        return new SimpleDateFormat(DATE_TIME_FORMAT);
    }

    public static Date getExpirationDate() {
        return new Date(new Date().getTime() + TimeUnit.MINUTES.toMillis(30));
    }
}
