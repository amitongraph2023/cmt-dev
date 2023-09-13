package com.panera.cmt.dto.proxy.chub;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentOptions {

    private List<CreditCard> creditCards;
    private List<ApplePay> applePays;
    private List<PayPal> payPals;
    private List<GiftCard> giftCards;
    private List<CorporateCateringAccount> corporateCateringAccounts;
    private List<CampusCard> campusCards;
    private List<BonusCard> bonusCards;

    // Getters
    public List<CreditCard> getCreditCards() {
        return creditCards == null ? new ArrayList<>() : creditCards;
    }

    public List<ApplePay> getApplePays() { return applePays == null ? new ArrayList<>() : applePays; }

    public List<PayPal> getPayPals() {
        return payPals == null ? new ArrayList<>() : payPals;
    }

    public List<GiftCard> getGiftCards() {
        return giftCards == null ? new ArrayList<>() : giftCards;
    }

    public List<CorporateCateringAccount> getCorporateCateringAccounts() {
        return corporateCateringAccounts == null ? new ArrayList<>() : corporateCateringAccounts;
    }

    public List<CampusCard> getCampusCards() {
        return campusCards == null ? new ArrayList<>() : campusCards;
    }

    public List<BonusCard> getBonusCards() {
        return bonusCards == null ? new ArrayList<>() : bonusCards;
    }
}
