package com.panera.cmt.service.app_config;

import java.util.List;
import java.util.Map;

public interface IAuthGroupsService {

    Map<String, List<String>> getAuthGroups();

    List<String> getAllAuthGroups();
}
