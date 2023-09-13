package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.Mfa;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_MFA;
import static com.panera.cmt.util.SharedUtils.isNull;
import static java.util.Arrays.asList;

@Service
@Slf4j
public class CustomerMfaService extends BaseCustomerHubService implements ICustomerMfaService {


    @Override
    public Optional<List<Mfa>> getMfas(Long customerId) {

        if (customerId == null) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getMfas", String.format("Getting Mfas for customerId=%d", customerId));

        Mfa[] mfas = doGet(Mfa[].class, stopWatch, ChubEndpoints.CUSTOMER_MFA_BASE, customerId);

        return Optional.of((mfas != null) ? asList(mfas) : new ArrayList<>());

    }

    @Override
    public Optional<ResponseHolder<Mfa>> disableSmsMfa(Long customerId) {

        if (isNull(customerId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "deleteSmsMfa", String.format("Deleting Sms Mfa for customerId=%d", customerId));

        return Optional.ofNullable(doDelete(Mfa.class, stopWatch, createAudit(ActionType.DELETE, customerId, "delete"), ChubEndpoints.CUSTOMER_MFA_SMS, customerId));

    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_MFA;

    }
}
