package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.CreditCard;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class CreditCardBuilder extends BaseObjectBuilder<CreditCard> {

    private String token = randomNumeric(15);
    private String expirationDate =  setupExpDate();
    private String cardholderName = UUID.randomUUID().toString();
    private String lastFour = StringUtils.right(token, 4);
    private String creditCardType = "MASTERCARD";
    private String paymentProcessor = "HEARTLAND";
    private String paymentLabel = UUID.randomUUID().toString();
    private boolean isDefault = false;

    private static String setupExpDate() {
        LocalDate now = LocalDate.now().plusYears(5);
        return now.format(DateTimeFormatter.ofPattern("MMyy"));
    }

    @Override
    CreditCard getTestClass() {
        return new CreditCard();
    }
}
