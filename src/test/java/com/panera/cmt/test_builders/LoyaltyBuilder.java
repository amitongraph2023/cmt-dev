package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.Loyalty;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;

public class LoyaltyBuilder extends BaseObjectBuilder<Loyalty> {

    private String cardNumber = generateRandomLuhnNumber();

    public LoyaltyBuilder withCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public static String generateRandomLuhnNumber() {
        //String positiveNumber = Integer.toString(new Random().nextInt(Integer.SIZE -1));
        String positiveNumber = "1234567";
        try {
            return positiveNumber + (new LuhnCheckDigit().calculate(positiveNumber));
        } catch (CheckDigitException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    Loyalty getTestClass() {
        return new Loyalty();
    }
}
