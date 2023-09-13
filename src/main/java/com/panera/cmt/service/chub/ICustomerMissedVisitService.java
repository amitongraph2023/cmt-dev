package com.panera.cmt.service.chub;

import com.panera.cmt.entity.ResponseHolder;

import java.util.Optional;

public interface ICustomerMissedVisitService {

    Optional<ResponseHolder<String>> redeemMissedVisit(Long customerId, String missedVisitCode, boolean validateOnly);

}