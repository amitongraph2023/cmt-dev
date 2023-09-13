package com.panera.cmt.controller.paytronix;

import com.panera.cmt.controller.BaseController;
import com.panera.cmt.entity.ResponseHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BasePaytronixController extends BaseController {
    protected ResponseEntity<?> returnResponse(ResponseHolder<?> response, HttpStatus status) {
        ResponseEntity responseEntity;
        if (status.equals(HttpStatus.NO_CONTENT)) {
            responseEntity = ResponseEntity.noContent().build();
        } else {
            responseEntity = new ResponseEntity<>(status);
        }
        return returnResponse(response, responseEntity);
    }
    protected ResponseEntity<?> returnResponse(ResponseHolder<?> response, Object o, HttpStatus status) {
        return returnResponse(response, new ResponseEntity<>(status));
    }
    private ResponseEntity<?> returnResponse(ResponseHolder<?> response, ResponseEntity elseResponse) {
        if (response.getStatus().equals(HttpStatus.NOT_FOUND)) {
            return ResponseEntity.notFound().build();
        } else if (response.getStatus().is4xxClientError()) {
            return new ResponseEntity<>(response.getErrors(), HttpStatus.NOT_ACCEPTABLE);
        } else {
            return elseResponse;
        }
    }
}
