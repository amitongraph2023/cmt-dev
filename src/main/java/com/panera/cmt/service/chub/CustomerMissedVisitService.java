package com.panera.cmt.service.chub;

import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_MISSED_VISIT;
import static com.panera.cmt.util.SharedUtils.isNull;

@Service
@Slf4j
public class CustomerMissedVisitService extends BaseCustomerHubService  implements ICustomerMissedVisitService {

    @Override
    public Optional<ResponseHolder<String>> redeemMissedVisit(Long customerId, String missedVisitCode, boolean validateOnly) {
        if (isNull(customerId, missedVisitCode)) {
            return Optional.empty();
        }

        StopWatch stopwatch = new StopWatch(log, "missedVisit", String.format("Missed visit for customerId=%d with missedVisitCode=%s and validateOnly=%b",
                customerId, missedVisitCode, validateOnly));

        return Optional.ofNullable(doPost(String.class, stopwatch, createAudit(ActionType.UPDATE, customerId, null), null, ChubEndpoints.MISSED_VISIT, customerId, missedVisitCode, validateOnly));
    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_MISSED_VISIT;
    }
}
