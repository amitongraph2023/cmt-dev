package com.panera.cmt.service.chub;

import java.util.Optional;

public interface IStaticDataService {

    Optional<Object[]> getStaticData(String type);
}
