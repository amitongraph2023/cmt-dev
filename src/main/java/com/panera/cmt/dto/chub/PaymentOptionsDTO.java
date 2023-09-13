package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentOptionsDTO {

    private List<CreditCardDTO> creditCards;
    private List<ApplePayDTO> applePays;
    private List<PayPalDTO> payPals;
    private List<GiftCardDTO> giftCards;
    private List<CorporateCateringAccountDTO> corporateCateringAccounts;
    private List<CampusCardDTO> campusCards;
    private List<BonusCardDTO> bonusCards;



    public static PaymentOptionsDTO fromEntity(PaymentOptions entity) {
        PaymentOptionsDTO paymentOptionsDTO = new PaymentOptionsDTO();

        for (CreditCard card : entity.getCreditCards()) {
            paymentOptionsDTO.addCreditCard(CreditCardDTO.fromEntity(card));
        }

        for (ApplePay applePay : entity.getApplePays()) {
            paymentOptionsDTO.addApplePay(ApplePayDTO.fromEntity(applePay));
        }

        for (GiftCard giftCard : entity.getGiftCards()) {
            paymentOptionsDTO.addGiftCard(GiftCardDTO.fromEntity(giftCard));
        }

        for (PayPal payPal : entity.getPayPals()) {
            paymentOptionsDTO.addPayPal(PayPalDTO.fromEntity(payPal));
        }

        for (CampusCard campusCard : entity.getCampusCards()) {
            paymentOptionsDTO.addCampusCard(CampusCardDTO.fromEntity(campusCard));
        }

        for (CorporateCateringAccount corporateCateringAccount : entity.getCorporateCateringAccounts()) {
            paymentOptionsDTO.addCorporateCateringAccount(CorporateCateringAccountDTO.fromEntity(corporateCateringAccount));
        }

        for (BonusCard bonusCard : entity.getBonusCards()) {
            paymentOptionsDTO.addBonusCard(BonusCardDTO.fromEntity(bonusCard));
        }
        return paymentOptionsDTO;
    }

    public void addCreditCard(CreditCardDTO creditCardDTO) {
        if (this.creditCards == null) {
            this.creditCards = new ArrayList<>();
        }
        this.creditCards.add(creditCardDTO);
    }

    public void addApplePay(ApplePayDTO applePayDTO) {
        if (this.applePays == null) {
            this.applePays = new ArrayList<>();
        }
        this.applePays.add(applePayDTO);
    }

    public void addPayPal(PayPalDTO payPalDTO) {
        if (this.payPals == null) {
            this.payPals = new ArrayList<>();
        }
        this.payPals.add(payPalDTO);
    }

    public void addGiftCard(GiftCardDTO giftCardDTO) {
        if (this.giftCards == null) {
            this.giftCards = new ArrayList<>();
        }
        this.giftCards.add(giftCardDTO);
    }

    public void addCampusCard(CampusCardDTO campusCardDTO) {
        if (this.campusCards == null) {
            this.campusCards = new ArrayList<>();
        }
        this.campusCards.add(campusCardDTO);
    }

    public void addCorporateCateringAccount(CorporateCateringAccountDTO corporateCateringAccountDTO) {
        if (this.corporateCateringAccounts == null) {
            this.corporateCateringAccounts = new ArrayList<>();
        }
        this.corporateCateringAccounts.add(corporateCateringAccountDTO);
    }

    public void addBonusCard(BonusCardDTO bonusCardDTO) {
        if (this.bonusCards == null) {
            this.bonusCards = new ArrayList<>();
        }
        this.bonusCards.add(bonusCardDTO);
    }
}
