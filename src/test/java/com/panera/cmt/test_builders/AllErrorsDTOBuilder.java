package com.panera.cmt.test_builders;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.ErrorDTO;

import java.util.List;

import static java.util.Arrays.asList;

public class AllErrorsDTOBuilder extends BaseObjectBuilder<AllErrorsDTO> {

    private List<ErrorDTO> errors = asList(new ErrorDTOBuilder().build());

    @Override
    AllErrorsDTO getTestClass() {
        return new AllErrorsDTO();
    }
}
