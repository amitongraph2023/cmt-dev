package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.Address;
import com.panera.cmt.entity.ResponseHolder;

import java.util.List;
import java.util.Optional;

public interface ICustomerAddressService {

    Optional<ResponseHolder<Address>> createAddress(Long customerId, Address address);

    Optional<ResponseHolder<Address>> deleteAddress(Long customerId, Long addressId);

    Optional<Address> getAddress(Long customerId, Long addressId);

    Optional<List<Address>> getAddresses(Long customerId);

    Optional<ResponseHolder<Address>> updateAddress(Long customerId, Long addressId, Address updatedAddress);
}
