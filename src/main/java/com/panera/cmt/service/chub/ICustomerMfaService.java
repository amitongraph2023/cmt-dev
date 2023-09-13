package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.Mfa;
import com.panera.cmt.entity.ResponseHolder;

import java.util.List;
import java.util.Optional;

public interface ICustomerMfaService {

    Optional<List<Mfa>> getMfas(Long customerId);

    Optional<ResponseHolder<Mfa>> disableSmsMfa(Long customerId);
}
