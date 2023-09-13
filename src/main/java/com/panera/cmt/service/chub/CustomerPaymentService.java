package com.panera.cmt.service.chub;

import com.panera.cmt.dto.chub.ApplePayDTO;
import com.panera.cmt.dto.chub.BonusCardDTO;
import com.panera.cmt.dto.chub.CreditCardDTO;
import com.panera.cmt.dto.chub.GiftCardDTO;
import com.panera.cmt.dto.chub.PayPalDTO;
import com.panera.cmt.dto.proxy.chub.*;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.PaymentOptionType;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_PAYMENT_OPTION;
import static com.panera.cmt.util.SharedUtils.isNull;

@Service
@Slf4j
@SuppressWarnings("unchecked")
public class CustomerPaymentService extends BaseCustomerHubService implements ICustomerPaymentService {

    @Override
    public <T> Optional<ResponseHolder<T>> addPaymentOption(Long customerId, PaymentOptionType type, T entity) {
        if (isNull(customerId, type, entity)) {
            return Optional.empty();
        }

        Object dto = null;
        Class eClass = null;
        StopWatch stopWatch;
        switch (type) {
            case giftcard:
                dto = GiftCardDTO.fromEntity((GiftCard) entity);
                eClass = GiftCard.class;
                stopWatch = new StopWatch(log, "addPaymentOption", String.format("Adding giftCard to customerId=%d", customerId));
                break;
            case bonuscard:
                dto = BonusCardDTO.fromEntity((BonusCard) entity);
                eClass = BonusCard.class;
                stopWatch = new StopWatch(log, "addPaymentOption", String.format("Adding bonusCard to customerId=%d", customerId));
                break;
            default:
                stopWatch = new StopWatch(log, "addPaymentOption", String.format("Adding PaymentOptionType=%s is not currently supported", type));
        }

        if (isNull(dto, eClass)) {
            return Optional.empty();
        }

        return Optional.ofNullable(doPost(eClass, stopWatch, createAudit(ActionType.CREATE, customerId, entity), dto, ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, customerId, type.name()));
    }

    @Override
    public <T> Optional<ResponseHolder<T>> deletePaymentOption(Long customerId, String id, PaymentOptionType type) {
        if (isNull(customerId, id, type)) {
            return Optional.empty();
        }

        Class eClass = null;
        StopWatch stopWatch;
        switch (type) {
            case applepay:
                eClass = ApplePay.class;
                stopWatch = new StopWatch(log, "deletePaymentOption", String.format("Deleting ApplePay accountNumber=%s from customerId=%d", id, customerId));
                break;
            case creditcard:
                eClass = CreditCard.class;
                stopWatch = new StopWatch(log, "deletePaymentOption", String.format("Deleting credit card token=%s from customerId=%d", id, customerId));
                break;
            case giftcard:
                eClass = GiftCard.class;
                stopWatch = new StopWatch(log, "deletePaymentOption", String.format("Deleting gift cardNumber=%s from customerId=%d", id, customerId));
                break;
            case paypal:
                eClass = PayPal.class;
                stopWatch = new StopWatch(log, "deletePaymentOption", String.format("Deleting PayPal accountNumber=%s from customerId=%d", id, customerId));
                break;
            case bonuscard:
                eClass = BonusCard.class;
                stopWatch = new StopWatch(log, "deletePaymentOption", String.format("Deleting bonus card accountNumber=%s from customerId=%d", id, customerId));
                break;
            default:
                stopWatch = new StopWatch(log, "deletePaymentOption", String.format("Deleting PaymentOptionType=%s is not currently supported", type));
        }

        if (eClass == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(doDelete(eClass, stopWatch, createAudit(ActionType.DELETE, customerId, String.format("Deleting paymentOption id=%s", id)), ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, customerId, type.name(), id));
    }

    @Override
    public <T> Optional<T> getPaymentOption(Long customerId, String id, PaymentOptionType type) {
        if (isNull(customerId, id, type)) {
            return Optional.empty();
        }

        Class eClass = null;
        StopWatch stopWatch;
        switch (type) {
            case applepay:
                eClass = ApplePay.class;
                stopWatch = new StopWatch(log, "getPaymentOption", String.format("Adding ApplePay accountNumber=%s to customerId=%d", id, customerId));
                break;
            case creditcard:
                eClass = CreditCard.class;
                stopWatch = new StopWatch(log, "getPaymentOption", String.format("Adding credit card token=%s to customerId=%d", id, customerId));
                break;
            case giftcard:
                eClass = GiftCard.class;
                stopWatch = new StopWatch(log, "getPaymentOption", String.format("Adding gift cardNumber=%s to customerId=%d", id, customerId));
                break;
            case paypal:
                eClass = PayPal.class;
                stopWatch = new StopWatch(log, "getPaymentOption", String.format("Adding PayPal accountNumber=%s to customerId=%d", id, customerId));
                break;
            default:
                stopWatch = new StopWatch(log, "getPaymentOption", String.format("Getting PaymentOptionType=%s is not currently supported", type));
        }

        if (eClass == null) {
            return Optional.empty();
        }

        return Optional.ofNullable((T) doGet(eClass, stopWatch, ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, customerId, type.name(), id));
    }

    @Override
    public Optional<PaymentOptions> getPaymentOptions(Long customerId) {
        if (customerId == null) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getPaymentOptions", String.format("Getting payment options for customerId=%d", customerId));

        return Optional.ofNullable(doGet(PaymentOptions.class, stopWatch, ChubEndpoints.PAYMENT_OPTIONS_BASE, customerId));
    }

    @Override
    public <T> Optional<ResponseHolder<T>> updatePaymentOption(Long customerId, String id, PaymentOptionType type, T updatedEntity) {
        if (isNull(customerId, id, type, updatedEntity)) {
            return Optional.empty();
        }

        Object dto = null;
        Class eClass = null;
        StopWatch stopWatch;
        switch (type) {
            case applepay:
                dto = ApplePayDTO.fromEntity((ApplePay) updatedEntity);
                eClass = ApplePay.class;
                stopWatch = new StopWatch(log, "updatePaymentOption", String.format("Updating ApplePay accountNumber=%s of customerId=%d", id, customerId));
                break;
            case creditcard:
                dto = CreditCardDTO.fromEntity((CreditCard) updatedEntity);
                eClass = CreditCard.class;
                stopWatch = new StopWatch(log, "updatePaymentOption", String.format("Updating credit card token=%s of customerId=%d", id, customerId));
                break;
            case giftcard:
                dto = GiftCardDTO.fromEntity((GiftCard) updatedEntity);
                eClass = GiftCard.class;
                stopWatch = new StopWatch(log, "updatePaymentOption", String.format("Updating gift cardNumber=%s of customerId=%d", id, customerId));
                break;
            case paypal:
                dto = PayPalDTO.fromEntity((PayPal) updatedEntity);
                eClass = PayPal.class;
                stopWatch = new StopWatch(log, "updatePaymentOption", String.format("Updating PayPal accountNumber=%s of customerId=%d", id, customerId));
                break;
            default:
                stopWatch = new StopWatch(log, "updatePaymentOption", String.format("Updating PaymentOptionType=%s is not currently supported", type));
        }

        if (isNull(dto, eClass)) {
            return Optional.empty();
        }

        return Optional.ofNullable(doPut(eClass, stopWatch, createAudit(ActionType.UPDATE, customerId, updatedEntity), dto, ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, customerId, type.name(), id));
    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_PAYMENT_OPTION;
    }
}
