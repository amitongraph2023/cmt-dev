package com.panera.cmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChubEndpoints {

    // App Config
    APP_CONFIG_BASE("/restricted/v1/app-config"),
    APP_CONFIG_BY_ID("/restricted/v1/app-config/{id}"),
    APP_CONFIG_PAGE("/restricted/v1/app-config?col={col}&dir={dir}&page={pageNumber}&query={query}&size={pageSize}"),

    // Card Exchange
    CARD_EXCHANGE("/v1/customer/{customerId}/loyalty/exchange/{existingLoyaltyCard}?excludePX={excludePX}"),

    // Customer Address
    CUSTOMER_ADDRESS_BASE("/v1/customer/{customerId}/address"),
    CUSTOMER_ADDRESS_BY_ID("/v1/customer/{customerId}/address/{id}"),

    // Customer
    CUSTOMER_BASE("/v1/customer/{customerId}"),
    CUSTOMER_DETAILS("/v1/customer/{customerId}/details"),
    CUSTOMER_DETAILS_FOR_SEARCH("/v1/customer/{customerId}/details?exclude=SUBSCRIPTIONS&exclude=USER_PREFERENCES&exclude=ADDRESSES&exclude=PAYMENT_OPTIONS&exclude=TAX_EXEMPTIONS&exclude=FAVORITE_CAFES&exclude=SMS_PREFERENCES&exclude=SOCIAL_INTEGRATIONS"),
    CUSTOMER_BY_ID("/v1/customer/{customerId}"),
    USERNAME_LOOKUP("/v1/customer/username/{username}"),

    // Customer Email
    CUSTOMER_EMAIL_BASE("/v1/customer/{customerId}/emailaddress"),
    CUSTOMER_EMAIL_BY_ID("/v1/customer/{customerId}/emailaddress/{id}"),
    CUSTOMER_EMAIL_RESEND_VERIFICATION("/v1/verification/email/resend"),
    CUSTOMER_EMAIL_SET_DEFAULT("/v1/customer/{customerId}/emailaddress/{id}/default"),

    // Customer Mfa
    CUSTOMER_MFA_BASE( "/v1/customer/{id}/mfa"),
    CUSTOMER_MFA_SMS("/v1/customer/{id}/mfa/sms"),

    // Customer Phone
    CUSTOMER_PHONE_BASE("/v1/customer/{customerId}/phone"),
    CUSTOMER_PHONE_BY_ID("/v1/customer/{customerId}/phone/{id}"),
    CUSTOMER_PHONE_SET_DEFAULT("/v1/customer/{customerId}/phone/{id}/default"),

    // Customer Search
    CUSTOMER_SEARCH("/v1/customer/search/{searchType}/{value}"),

    // Loyalty
    LOYALTY_EXCHANGE("/v1/customer/{customerId}/loyalty/exchange/{existingLoyaltyCard}"),
    LOYALTY_UPDATE("/v1/customer/{customerId}/loyalty"),
    LOYALTY_REWARDS_UPDATE("/v1/customer/{customerId}/loyalty/rewards-status"),

    // Manage Account
    MANAGE_ACCOUNT("/restricted/v1/customer/{customerId}/{action}"),

    // Missed Visit
    MISSED_VISIT("/v1/customer/{customerId}/missedvisit/{missedVisitCode}/redeem?validateOnly={validateOnly}"),

    // Password
    PASSWORD_SEND_RESET("/v1/passwordreset/customer/{customerId}/sendPasswordResetEmail"),
    PASSWORD_SET("/restricted/v1/customer/{customerId}/password"),

    // Payment Options
    PAYMENT_OPTIONS_BASE("/v1/customer/{customerId}/paymentoptions"),
    PAYMENT_OPTIONS_BY_TYPE("/v1/customer/{customerId}/paymentoptions/{type}"),
    PAYMENT_OPTIONS_BY_TYPE_AND_VALUE("/v1/customer/{customerId}/paymentoptions/{type}/{value}"),

    // Restricted
    RESTRICTED_BY_TYPE("/v1/restricted/{type}"),
    RESTRICTED_BY_TYPE_AND_VALUE("/v1/restricted/{type}/{value}"),

    // Social Integrations
    SOCIAL_INTEGRATIONS_BASE("/v1/customer/{customerId}/social"),
    SOCIAL_INTEGRATIONS_BY_TYPE("/v1/customer/{customerId}/social/{type}"),

    // Static Data
    STATIC_DATA_BY_TYPE("/v1/static/{type}"),

    // Subscriptions
    SUBSCRIPTIONS("/v1/customer/{customerId}/subscriptions"),
    UNSUBSCRIBE("/v1/unsubscribe/{emailToken}"),

    // Tax Exemption
    TAX_EXEMPTION("/v1/customer/{customerId}/taxexemption"),

    // User Preferences
    USER_PREFERENCES_BASE("/v1/customer/{customerId}/userpreferences"),
    USER_PREFERENCES_BY_TYPE("/v1/customer/{customerId}/userpreferences/{type}");


    private String stub;
}
