package com.panera.cmt.service.app_config;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IUIConfigService {

    Map<String, List<String>> getRouteWhiteLists();

    Map<String, List<String>> getPermissionWhiteLists();
}
