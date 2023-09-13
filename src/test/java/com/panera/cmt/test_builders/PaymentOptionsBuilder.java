package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.*;

import java.util.List;

import static java.util.Arrays.asList;

public class PaymentOptionsBuilder extends BaseObjectBuilder<PaymentOptions> {

    private List<CreditCard> creditCards = asList(new CreditCardBuilder().build());
    private List<PayPal> payPals = asList(new PayPalBuilder().build());
    private List<GiftCard> giftCards = asList(new GiftCardBuilder().build());
    private List<CorporateCateringAccount> corporateCateringAccounts = asList(new CorporateCateringAccountBuilder().build());

    @Override
    PaymentOptions getTestClass() {
        return new PaymentOptions();
    }
}
