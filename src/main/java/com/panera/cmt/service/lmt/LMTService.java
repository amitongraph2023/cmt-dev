package com.panera.cmt.service.lmt;

import com.panera.cmt.enums.LMTEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_LMT;

@Slf4j
@Service
public class LMTService extends BaseLMTService implements ILMTService{

    private static final Logger logger = LoggerFactory
            .getLogger(LMTService.class);

    @Override
    public Optional<String> getLTObyCode(@PathVariable String specialCode) {
        logger.info("Attempting to get specific LTO offer: {}"
                , specialCode);

        StopWatch stopWatch = new StopWatch(log, "lmtGetLTObyCode", String.format("Get LTO by code: %s", specialCode));

        return Optional.ofNullable(
                doGet(String.class, stopWatch, LMTEndpoints.TRANSACTION_HISTORY, specialCode ));
    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_LMT;
    }
}
