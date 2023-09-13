package com.panera.cmt.dto;

import com.panera.cmt.enums.ErrorType;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value="ValidationError", description="Collection of all field validation errors within the request payload and other application errors")
@Data
public class AllErrorsDTO {

    private List<ErrorDTO> errors = new ArrayList<>();

    public void addError(ErrorType source, String reasonCode, String description, String details) {
        ErrorDTO error = new ErrorDTO(source, reasonCode, details, description);
        errors.add(error);
    }
}
