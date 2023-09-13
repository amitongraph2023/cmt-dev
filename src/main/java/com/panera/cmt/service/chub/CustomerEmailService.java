package com.panera.cmt.service.chub;

import com.panera.cmt.dto.chub.EmailDTO;
import com.panera.cmt.dto.chub.EmailVerificationDTO;
import com.panera.cmt.dto.proxy.chub.Email;
import com.panera.cmt.dto.proxy.chub.EmailVerification;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_EMAIL;
import static com.panera.cmt.util.SharedUtils.isNull;
import static java.util.Arrays.asList;

@Service
@Slf4j
public class CustomerEmailService extends BaseCustomerHubService implements ICustomerEmailService {

    @Override
    public Optional<ResponseHolder<Email>> createEmail(Long customerId, Email email) {
        if (isNull(customerId, email)) {
            return Optional.empty();
        }

        StopWatch stopwatch = new StopWatch(log, "createEmail", String.format("Adding email to customerId=%d", customerId));

        return Optional.ofNullable(doPost(Email.class, stopwatch, createAudit(ActionType.CREATE, customerId, email), EmailDTO.fromEntity(email), ChubEndpoints.CUSTOMER_EMAIL_BASE, customerId));
    }

    @Override
    public Optional<Email> getEmail(Long customerId, Long emailId) {
        if (isNull(customerId, emailId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getEmail", String.format("Getting emailId=%d for customerId=%d", emailId, customerId));

        return Optional.ofNullable(doGet(Email.class, stopWatch, ChubEndpoints.CUSTOMER_EMAIL_BY_ID, customerId, emailId));
    }

    @Override
    public Optional<List<Email>> getEmails(Long customerId) {
        if (customerId == null) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getEmails", String.format("Getting emails for customerId=%d", customerId));

        Email[] emails = doGet(Email[].class, stopWatch, ChubEndpoints.CUSTOMER_EMAIL_BASE, customerId);

        return Optional.of((emails != null) ? asList(emails) : new ArrayList<>());
    }

    @Override
    public Optional<ResponseHolder<Email>> setDefault(Long customerId, Long emailId) {
        if (isNull(customerId, emailId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "setDefault", String.format("Setting default email to emailId=%d for customerId=%d", emailId, customerId));

        return Optional.ofNullable(doPost(Email.class, stopWatch, createAudit(ActionType.UPDATE, customerId, emailId, "setDefault"), null, ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT, customerId, emailId));
    }

    @Override
    public Optional<ResponseHolder<Email>> updateEmail(Long customerId, Long emailId, Email updatedEmail) {
        if (isNull(customerId, emailId, updatedEmail)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "updateEmail", String.format("Updating emailId=%d of customerId=%d", emailId, customerId));

        return Optional.ofNullable(doPut(Email.class, stopWatch, createAudit(ActionType.UPDATE, customerId, emailId, updatedEmail), EmailDTO.fromEntity(updatedEmail), ChubEndpoints.CUSTOMER_EMAIL_BY_ID, customerId, emailId));
    }

    @Override
    public Optional<ResponseHolder<EmailVerification>> resendVerificationEmail(Long customerId, EmailVerification emailVerification) {
        if (isNull(emailVerification)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "resendEmailVerfication", String.format("Resending Verification Email to emailAddress=%s", emailVerification.getEmailAddress()));

        return Optional.ofNullable(doPost(EmailVerification.class, stopWatch, createAudit(ActionType.RESEND, customerId, emailVerification), EmailVerificationDTO.fromEntity(emailVerification), ChubEndpoints.CUSTOMER_EMAIL_RESEND_VERIFICATION));

    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_EMAIL;
    }
}
