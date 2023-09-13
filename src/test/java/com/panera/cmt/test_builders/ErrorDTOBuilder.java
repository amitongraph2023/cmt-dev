package com.panera.cmt.test_builders;

import com.panera.cmt.dto.ErrorDTO;
import com.panera.cmt.enums.ErrorType;

import java.util.UUID;

import static com.panera.cmt.test_util.SharedTestUtil.nextEnum;

public class ErrorDTOBuilder extends BaseObjectBuilder<ErrorDTO> {

    private ErrorType source = nextEnum(ErrorType.class);
    private String reasonCode = UUID.randomUUID().toString();
    private String details = UUID.randomUUID().toString();
    private String description = UUID.randomUUID().toString();

    @Override
    ErrorDTO getTestClass() {
        return new ErrorDTO();
    }
}
