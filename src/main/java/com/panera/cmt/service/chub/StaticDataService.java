package com.panera.cmt.service.chub;

import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@Slf4j
public class StaticDataService extends BaseCustomerHubService implements IStaticDataService {

    @Override
    public Optional<Object[]> getStaticData(String type) {
        if (isEmpty(type)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getStaticData", String.format("Getting static data of type=%s", type));

        ResponseHolder<Object[]> response = doGetResponse(Object[].class, stopWatch, ChubEndpoints.STATIC_DATA_BY_TYPE, type);

        if (response != null && response.getStatus().equals(HttpStatus.OK)) {
            return Optional.ofNullable(response.getEntity());
        } else {
            return Optional.empty();
        }
    }

    @Override
    protected String getSubjectName() {
        return "staticData";
    }
}
