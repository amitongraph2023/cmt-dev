package com.panera.cmt.service.chub;

import com.panera.cmt.dto.chub.AddressDTO;
import com.panera.cmt.dto.proxy.chub.Address;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_ADDRESS;
import static com.panera.cmt.util.SharedUtils.isNull;
import static java.util.Arrays.asList;

@Service
@Slf4j
public class CustomerAddressService extends BaseCustomerHubService implements ICustomerAddressService {

    @Override
    public Optional<ResponseHolder<Address>> createAddress(Long customerId, Address address) {
        if (isNull(customerId, address)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "createAddress", String.format("Adding address to customerId=%d", customerId));

        return Optional.ofNullable(doPost(Address.class, stopWatch, createAudit(ActionType.CREATE, customerId, address), AddressDTO.fromEntity(address), ChubEndpoints.CUSTOMER_ADDRESS_BASE, customerId));
    }

    @Override
    public Optional<ResponseHolder<Address>> deleteAddress(Long customerId, Long addressId) {
        if (isNull(customerId, addressId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "deleteAddress", String.format("Deleting addressId=%d for customerId=%d", addressId, customerId));

        return Optional.ofNullable(doDelete(Address.class, stopWatch, createAudit(ActionType.DELETE, customerId, addressId, "delete"), ChubEndpoints.CUSTOMER_ADDRESS_BY_ID, customerId, addressId));
    }

    @Override
    public Optional<Address> getAddress(Long customerId, Long addressId) {
        if (isNull(customerId, addressId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getAddress", String.format("Getting addressId=%d for customerId=%d", addressId, customerId));

        return Optional.ofNullable(doGet(Address.class, stopWatch, ChubEndpoints.CUSTOMER_ADDRESS_BY_ID, customerId, addressId));
    }

    @Override
    public Optional<List<Address>> getAddresses(Long customerId) {
        if (customerId == null) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getAddresses", String.format("Getting addresses for customerId=%d", customerId));

        Address[] addresses = doGet(Address[].class, stopWatch, ChubEndpoints.CUSTOMER_ADDRESS_BASE, customerId);

        return Optional.of((addresses != null) ? asList(addresses) : new ArrayList<>());
    }

    @Override
    public Optional<ResponseHolder<Address>> updateAddress(Long customerId, Long addressId, Address updatedAddress) {
        if (isNull(customerId, addressId, updatedAddress)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "updateAddress", String.format("Updating addressId=%d of customerId=%d", addressId, customerId));

        return Optional.ofNullable(doPut(Address.class, stopWatch, createAudit(ActionType.UPDATE, customerId, addressId, updatedAddress), AddressDTO.fromEntity(updatedAddress), ChubEndpoints.CUSTOMER_ADDRESS_BY_ID, customerId, addressId));
    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_ADDRESS;
    }
}
