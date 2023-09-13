package com.panera.cmt.service.lmt;

import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

public interface ILMTService {
    Optional<String> getLTObyCode(@PathVariable String specialCode);
}
