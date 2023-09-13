package com.panera.cmt.controller;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.ErrorDTO;
import com.panera.cmt.enums.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.util.Arrays.asList;

@ControllerAdvice
@Slf4j
public class DefaultExceptionAdviseController {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity handleException(RuntimeException ex) {
        log.info("failed to process request", ex);
        if(ex.getClass().isAnnotationPresent(ResponseStatus.class)) {
            throw ex; // If exception already has a response status then just let it through
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public AllErrorsDTO processValidationError(MethodArgumentNotValidException ex) {
        log.info("failed to process request", ex);
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        List<ObjectError> globalErrors = result.getGlobalErrors();
        AllErrorsDTO allErrorsDTO = new AllErrorsDTO();

        processFieldErrors(allErrorsDTO, fieldErrors);
        processObjectErrors(allErrorsDTO, globalErrors);
        return allErrorsDTO;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public AllErrorsDTO handleHttpMessageNotReadableException(HttpServletRequest request, Exception exception) {
        log.info("failed to process request", exception);
        return processInputErrors(request, exception);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public AllErrorsDTO handleMethodArgumentTypeMismatchException(HttpServletRequest request, Exception exception) {
        log.info("failed to process request", exception);
        return processInputErrors(request, exception);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public AllErrorsDTO handleAccessDeniedException(HttpServletRequest request, Exception exception) {
        log.info("Client is not authorized to perform this action. Method: {}, URI: {}", request.getMethod(), request.getRequestURI(), exception);
        AllErrorsDTO allErrorsDTO = new AllErrorsDTO();
        ErrorDTO error = new ErrorDTO();
        error.setDetails("The calling client is not authorized to perform this task");
        error.setReasonCode("client.not.authorized");
        error.setSource(ErrorType.SYSTEM);
        error.setDescription("The calling client is not authorized to access this resource. Method: "+ request.getMethod() + ", URI: " + request.getRequestURI());
        allErrorsDTO.setErrors(asList(error));
        return allErrorsDTO;
    }

    private AllErrorsDTO processInputErrors(HttpServletRequest request, Exception exception) {
        AllErrorsDTO allErrorsDTO = new AllErrorsDTO();
        ErrorDTO error = new ErrorDTO();
        error.setDetails(exception.getCause().toString());
        error.setReasonCode(exception.getClass().getCanonicalName());
        error.setSource(ErrorType.INPUT);
        error.setDescription("Unable to parse input for " + request.getMethod() + " at " + request.getRequestURI());
        allErrorsDTO.setErrors(asList(error));
        return allErrorsDTO;
    }

    private void processFieldErrors(AllErrorsDTO allErrorsDTO, List<FieldError> fieldErrors) {
        fieldErrors.stream().filter(Objects::nonNull).forEach(fieldError -> {
            String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
            allErrorsDTO.addError(ErrorType.FIELD, fieldError.getDefaultMessage(), localizedErrorMessage, fieldError.getField());
        });
    }

    private void processObjectErrors(AllErrorsDTO allErrorsDTO, List<ObjectError> fieldErrors) {

        fieldErrors.stream().filter(Objects::nonNull).forEach(fieldError -> {
            String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
            allErrorsDTO.addError(ErrorType.INPUT, fieldError.getDefaultMessage(), localizedErrorMessage, fieldError.getObjectName());
        });
    }

    private String resolveLocalizedErrorMessage(ObjectError fieldError) {
        Locale currentLocale =  LocaleContextHolder.getLocale();
        return messageSource.getMessage(fieldError.getDefaultMessage(), null, currentLocale);
    }
}
