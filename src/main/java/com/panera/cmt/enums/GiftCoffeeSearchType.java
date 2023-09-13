package com.panera.cmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GiftCoffeeSearchType {
    GIFT_CODE("giftCode"),
    ORDER_ID("orderId"),
    CUSTOMER_ID("customerId"),
    CUSTOMER_EMAIL("customerEmail");

    private String type;
}
