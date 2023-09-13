package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.Phone;
import com.panera.cmt.entity.ResponseHolder;

import java.util.List;
import java.util.Optional;

public interface ICustomerPhoneService {

    Optional<ResponseHolder<Phone>> createPhone(Long customerId, Phone phone);

    Optional<ResponseHolder<Phone>> deletePhone(Long customerId, Long phoneId);

    Optional<Phone> getPhone(Long customerId, Long phoneId);

    Optional<List<Phone>> getPhones(Long customerId);

    Optional<ResponseHolder<Phone>> setDefault(Long customerId, Long phoneId);

    Optional<ResponseHolder<Phone>> updatePhone(Long customerId, Long phoneId, Phone updatedPhone);
}
