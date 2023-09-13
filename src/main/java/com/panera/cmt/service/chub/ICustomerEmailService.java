package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.Email;
import com.panera.cmt.dto.proxy.chub.EmailVerification;
import com.panera.cmt.entity.ResponseHolder;

import java.util.List;
import java.util.Optional;

public interface ICustomerEmailService {

    Optional<ResponseHolder<Email>> createEmail(Long customerId, Email email);

    Optional<Email> getEmail(Long customerId, Long emailId);

    Optional<List<Email>> getEmails(Long customerId);

    Optional<ResponseHolder<Email>> setDefault(Long customerId, Long emailId);

    Optional<ResponseHolder<Email>> updateEmail(Long customerId, Long emailId, Email updatedEmail);

    Optional<ResponseHolder<EmailVerification>> resendVerificationEmail(Long customerId, EmailVerification emailVerification);
}
