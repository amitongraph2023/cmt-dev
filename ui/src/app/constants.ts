export const CONSTANTS = {
  API_ROUTES: {
    API_BASE: '/api/v1',

    CONFIG: {
      BASE: '/app-config',
      BY_ID: '/app-config/{id}',
      PERMISSION_WHITELISTS: '/ui-config/permission-whitelist',
      ROUTES: {
        CUST: {
          INF: 'customer/customer-info',
          PHN: 'customer/phones',
          ADDR: 'customer/addresses',
          EMAIL: 'customer/emails',
          MFA: 'customer/mfa',
          PMT: 'customer/payment-options',
          PREFS: '/customer/preferences',
          SUBSCR: '/customer/subscriptions',
        },
        LTO: {
          FIND_BY_CODE: '/lto/code',
        },
        PNG: {
          RESEND_GIFT_COFFEE_SUBSCRIPTION: '/png/resendGiftCoffeeEmail/{giftCode}',
        },
        PTX: {
          BALANCE: 'paytronix/balance',
          TRANSACTION_HISTORY: '/paytronix/transactions',
          CARD_EXCHANGE: 'paytronix/card-exchange',
          MISSED_VISIT: '/paytronix/missed-visit',
        },
      }
    },

    AUTHENTICATION: {
      BASE: '/authentication',
    },

    CATERING_REDIRECT: {
      BASE: '/catering',
    },

    CUSTOMER: {
      BY_ID: '/customer/{customerId}',
      BY_TYPE: '/customer/{customerId}/{type}',
      STATUS: '/customer/{customerId}/status',

      ADDRESS: {
        BASE: '/customer/{customerId}/address',
        BY_ID: '/customer/{customerId}/address/{addressId}',
      },
      COFFEE_SUBSCRIPTION_USAGE: {
        BASE: '/subscriptionService/coffeeSubscriptionUsage/{customerId}',
        CANCEL: '/subscriptionService/cancel/{customerId}/{programId}',
      },
      EMAIL: {
        BASE: '/customer/{customerId}/email',
        BY_ID: '/customer/{customerId}/email/{emailId}',
        RESEND_VERIFICATION: '/customer/{customerId}/email/resendVerificationEmail',
        SET_DEFAULT_BY_ID: '/customer/{customerId}/email/{emailId}/default',
      },
      LOYALTY: {
        BASE: '/customer/{customerId}/loyalty',
        CARD_EXCHANGE: '/customer/{customerId}/loyalty/cardExchange/{existingLoyaltyCard}/?excludePX={excludePX}',
        MISSED_VISIT: '/customer/{customerId}/missedvisit/{missedVisitCode}/redeem',
        REWARDS_ENABLED: '/customer/{customerId}/loyalty',
        REWARDS_ENABLED_UPDATE: '/customer/{customerId}/loyalty/rewards-status',
      },
      MFA: {
        BASE: '/customer/{customerId}/mfa',
        SMS: '/customer/{customerId}/mfa/sms',
      },
      PASSWORD: {
        ADMIN: {
          SET: '/customer/{customerId}/password/admin/set',
        },
        RESET: '/customer/{customerId}/password/reset',
        SEND_RESET: '/customer/{customerId}/password/sendreset',
        WOTD: '/customer/{customerId}/password/wotd',
      },
      PAYMENT_OPTIONS: {
        BASE: '/customer/{customerId}/paymentoptions',
        BY_TYPE_AND_ID: '/customer/{customerId}/paymentoptions/{type}/{id}',
        CREDIT_CARD_BY_TOKEN: '/customer/{customerId}/paymentoptions/creditcard/{token}',
        GIFT_CARD: '/customer/{customerId}/paymentoptions/giftcard',
		BONUS_CARD: '/customer/{customerId}/paymentoptions/bonuscard'
      },
      PHONE: {
        BASE: '/customer/{customerId}/phone',
        BY_ID: '/customer/{customerId}/phone/{phoneId}',
        SET_DEFAULT_BY_ID: '/customer/{customerId}/phone/{phoneId}/default',
      },
      PREFERENCES: {
        BASE: '/customer/{customerId}/userpreferences',
        BY_TYPE: '/customer/{customerId}/userpreferences/{type}',
      },
      SEARCH: {
        BASE: '/customer/search',
      },
      SOCIAL_INTEGRATIONS: {
        BASE: '/customer/{customerId}/socialintegrations',
      },
      SUBSCRIPTIONS: {
        BASE: '/customer/{customerId}/subscriptions',
        UNSUBSCRIBE_BY_EMAIL_TOKEN: '/unsubscribe/{emailToken}',
      },
    },

    GIFT_COFFEE_SUBSCRIPTIONS: {
      BASE: '/subscriptionService/giftcoffeesub/{searchType}/{searchTerm}',
    },

    LTO: {
      BY_CODE: '/lmt/LTO/{code}',
    },

    PAYTRONIX: {
      BALANCE: '/paytronixEsb/{cardNumber}/balance',
      TRANSACTION_HISTORY: '/paytronixEsb/{cardNumber}/transactionHistory/{startDate}',
      WALLET_CODES: '/paytronixEsb/walletCodes',
    },

    PNG: {
      RESEND_GIFT_COFFEE_SUBSCRIPTION: '/png/resendGiftCoffeeEmail/{giftCode}',
    },

    SPOOF: {
      BUTTONS: '/sso/spoof/buttons',
      LOGIN: '/sso/spoof/{customerId}/{unit}',
      LOGOUT: '/sso/logout/{ssoToken}/{customerId}/{unit}',
    },

    STATIC_DATA: '/static/chub/{type}',
  },
  AUTHORITIES: {
    ADMIN: 'admin',
    CBSS: 'cbss',
    CBSS_MANAGER: 'cbssManager',
    COFFEE: 'coffee',
    PROD_SUPPORT: 'prodSupport',
    READ_ONLY: 'readOnly',
    SALES_ADMIN: 'salesAdmin',
    SECURITY: 'security'
  },
  SETTINGS: {
    CUSTOMER_LANDING_PAGE: '/customer/customer-info',
    IS_DEV: false,
    SESSION_TIMEOUT_MINUTES: 8,
    SESSION_TIMEOUT_MODAL_SHOWN_AT_MINUTES: 5
  },
  STUBS: {
    PERMISSIONS_STUB: 'perm',
    USER_STUB: 'user'
  }
};
