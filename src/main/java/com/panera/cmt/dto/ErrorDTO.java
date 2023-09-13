package com.panera.cmt.dto;

import com.panera.cmt.enums.ErrorType;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@ApiModel(value="Error", description="Information about an error within the request payload or during request processing")
@Data
public class ErrorDTO {

    private ErrorType source;
    private String reasonCode;
    private String details;
    private String description;

    public ErrorDTO() {
        super();
    }
}
