package com.panera.cmt.service.chub;

import com.panera.cmt.dto.chub.PhoneDTO;
import com.panera.cmt.dto.proxy.chub.Phone;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_PHONE;
import static com.panera.cmt.util.SharedUtils.isNull;
import static java.util.Arrays.asList;

@Service
@Slf4j
public class CustomerPhoneService extends BaseCustomerHubService implements ICustomerPhoneService {

    @Override
    public Optional<ResponseHolder<Phone>> createPhone(Long customerId, Phone phone) {
        if (isNull(customerId, phone)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "createPhone", String.format("Adding phone to customerId=%d", customerId));

        return Optional.ofNullable(doPost(Phone.class, stopWatch, createAudit(ActionType.CREATE, customerId, phone), PhoneDTO.fromEntity(phone), ChubEndpoints.CUSTOMER_PHONE_BASE, customerId));
    }

    @Override
    public Optional<ResponseHolder<Phone>> deletePhone(Long customerId, Long phoneId) {
        if (isNull(customerId, phoneId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "deletePhone", String.format("Deleting phoneId=%d for customerId=%d", phoneId, customerId));

        return Optional.ofNullable(doDelete(Phone.class, stopWatch, createAudit(ActionType.DELETE, customerId, phoneId, "delete"), ChubEndpoints.CUSTOMER_PHONE_BY_ID, customerId, phoneId));
    }

    @Override
    public Optional<Phone> getPhone(Long customerId, Long phoneId) {
        if (isNull(customerId, phoneId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getPhone", String.format("Getting phoneId=%d for customerId=%d", phoneId, customerId));

        return Optional.ofNullable(doGet(Phone.class, stopWatch, ChubEndpoints.CUSTOMER_PHONE_BY_ID, customerId, phoneId));
    }

    @Override
    public Optional<List<Phone>> getPhones(Long customerId) {
        if (customerId == null) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getPhones", String.format("Getting phones for customerId=%d", customerId));

        Phone[] phones = doGet(Phone[].class, stopWatch, ChubEndpoints.CUSTOMER_PHONE_BASE, customerId);

        return Optional.of((phones != null) ? asList(phones) : new ArrayList<>());
    }

    @Override
    public Optional<ResponseHolder<Phone>> setDefault(Long customerId, Long phoneId) {
        if (isNull(customerId, phoneId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "setDefault", String.format("Setting default phone to phoneId=%d for customerId=%d", phoneId, customerId));

        return Optional.ofNullable(doPost(Phone.class, stopWatch, createAudit(ActionType.UPDATE, customerId, phoneId, "setDefault"), null, ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT, customerId, phoneId));
    }

    @Override
    public Optional<ResponseHolder<Phone>> updatePhone(Long customerId, Long phoneId, Phone updatedPhone) {
        if (isNull(customerId, phoneId, updatedPhone)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "updatePhone", String.format("Updating phoneId=%d of customerId=%d", phoneId, customerId));

        return Optional.ofNullable(doPut(Phone.class, stopWatch, createAudit(ActionType.UPDATE, customerId, phoneId, updatedPhone), PhoneDTO.fromEntity(updatedPhone), ChubEndpoints.CUSTOMER_PHONE_BY_ID, customerId, phoneId));
    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_PHONE;
    }
}
