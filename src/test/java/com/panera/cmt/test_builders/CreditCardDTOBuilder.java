package com.panera.cmt.test_builders;

import com.panera.cmt.dto.chub.CreditCardDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class CreditCardDTOBuilder extends BaseObjectBuilder<CreditCardDTO> {

    private String token = RandomStringUtils.random(15, false, true);
    private String expirationDate =  setupExpDate();
    private String cardholderName = UUID.randomUUID().toString();
    private String lastFour = StringUtils.right(token, 4);
    private String creditCardType = "MASTERCARD";
    private String paymentProcessor = "HEARTLAND";
    private String paymentLabel = UUID.randomUUID().toString();
    private boolean isDefault = new Random().nextBoolean();
    private boolean isDefaultSubscription = new Random().nextBoolean();

    private static String setupExpDate() {
        LocalDate now = LocalDate.now().plusYears(5);
        return now.format(DateTimeFormatter.ofPattern("MMyy"));
    }

    @Override
    CreditCardDTO getTestClass() {
        return new CreditCardDTO();
    }
}
