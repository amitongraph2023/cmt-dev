package com.panera.cmt.controller;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.enums.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

@Slf4j
public abstract class BaseController {

    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected  ResponseEntity<AllErrorsDTO> buildAuthValidationErrorResponse() {
        return new ResponseEntity<>(buildValidationError(ErrorType.SYSTEM, "error.username.password.invalid", "username and password combination is invalid", "username.password"), HttpStatus.UNAUTHORIZED);
    }

    protected ResponseEntity<AllErrorsDTO> buildValidationErrorResponse(String reasonCode, String details) {
        return new ResponseEntity<>(buildValidationError(ErrorType.FIELD, reasonCode, null, details), HttpStatus.NOT_ACCEPTABLE);
    }

    private AllErrorsDTO buildValidationError(ErrorType source, String reasonCode, String description, String details) {
        if(description == null) {
            description = resolveLocalizedErrorMessage(reasonCode);
        }
        AllErrorsDTO allErrorsDTO = new AllErrorsDTO();
        allErrorsDTO.addError(source, reasonCode, description, details);
        return allErrorsDTO;
    }

    private String resolveLocalizedErrorMessage(String reasonCode) {
        try {
            Locale currentLocale =  LocaleContextHolder.getLocale();
            return messageSource.getMessage(reasonCode, null, currentLocale);
        } catch (Exception e) {
            log.info("No error message for reason code {}", reasonCode);
            log.info("returning null error description", e);
            return null;
        }
    }
}
