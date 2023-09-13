package com.panera.cmt.service.chub;

import com.panera.cmt.dto.chub.WotdDTO;
import com.panera.cmt.entity.ResponseHolder;

import java.util.Optional;

public interface ICustomerPasswordService {

    Optional<ResponseHolder<String>> adminSetPassword(Long customerId, String password);

    Optional<ResponseHolder<String>> generatePassword(Long customerId);

    Optional<WotdDTO> getWordOfTheDay();

    void sendResetPasswordEmail(Long customerId);


}
