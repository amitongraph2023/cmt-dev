package com.panera.cmt.test_builders;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.entity.ResponseHolder;
import org.springframework.http.HttpStatus;

public class ResponseHolderBuilder<T> extends BaseObjectBuilder<ResponseHolder<T>> {

    private T entity;
    private AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
    private HttpStatus status = HttpStatus.OK;

    public ResponseHolderBuilder<T> withEntity(T entity) {
        this.entity = entity;
        this.status = HttpStatus.OK;
        return this;
    }

    public ResponseHolderBuilder<T> withErrors(AllErrorsDTO errors) {
        this.errors = errors;
        this.status = HttpStatus.NOT_ACCEPTABLE;
        return this;
    }

    public ResponseHolderBuilder<T> withStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    @Override
    ResponseHolder<T> getTestClass() {
        return new ResponseHolder<>();
    }
}
