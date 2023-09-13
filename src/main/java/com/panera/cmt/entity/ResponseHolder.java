package com.panera.cmt.entity;

import com.panera.cmt.dto.AllErrorsDTO;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseHolder<T> {

    private T entity;
    private AllErrorsDTO errors;
    private HttpStatus status;

    public ResponseHolder() {
    }
    public ResponseHolder(T entity) {
        this.entity = entity;
    }
    public ResponseHolder(T entity, HttpStatus status) {
        this.entity = entity;
        this.status = status;
    }
    public ResponseHolder(T entity, AllErrorsDTO errors, HttpStatus status) {
        this.entity = entity;
        this.errors = errors;
        this.status = status;
    }
}
