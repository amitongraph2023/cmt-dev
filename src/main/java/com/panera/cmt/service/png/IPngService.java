package com.panera.cmt.service.png;

import com.panera.cmt.dto.proxy.subscription_service.GiftCoffeeEmail;

public interface IPngService {

    Boolean resendGiftCoffeeSubscriptionEmail(GiftCoffeeEmail giftCoffeeEmail, String giftCode);

}
