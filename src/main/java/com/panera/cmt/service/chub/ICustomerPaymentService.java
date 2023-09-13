package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.PaymentOptions;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.PaymentOptionType;

import java.util.Optional;

public interface ICustomerPaymentService {

    <T> Optional<ResponseHolder<T>> addPaymentOption(Long customerId, PaymentOptionType type, T entity);

    <T> Optional<ResponseHolder<T>> deletePaymentOption(Long customerId, String id, PaymentOptionType type);

    <T> Optional<T> getPaymentOption(Long customerId, String id, PaymentOptionType type);

    Optional<PaymentOptions> getPaymentOptions(Long customerId);

    <T> Optional<ResponseHolder<T>> updatePaymentOption(Long customerId, String id, PaymentOptionType type, T updatedEntity);
}
