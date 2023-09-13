package com.panera.cmt.service.app_config;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.panera.cmt.config.Constants.*;

@Service
public class UIConfigService extends AppConfigService implements IUIConfigService {

    @Cacheable(CACHE_APP_UI_ROUTE_WHITELISTS)
    @Override
    public Map<String, List<String>> getRouteWhiteLists() {
        return getValuesArrayMap(APP_CONFIG_UI_ROUTE_WHITELIST);
    }

    @Cacheable(CACHE_APP_UI_PERMISSION_WHITELIST)
    @Override
    public Map<String, List<String>> getPermissionWhiteLists() {
        Map<String, List<String>> permissionWhiteLists = getValuesArrayMap(APP_CONFIG_UI_PERMISSIONS);

        for (Map.Entry<String,List<String>> value : permissionWhiteLists.entrySet()){
            value.setValue(value.getValue().stream().map(String::trim).collect(Collectors.toList()));
        }

        String[] routeMaps = (getValuesArrayMap(APP_CONFIG_UI_ROUTE_MAP).toString()
                .substring(15,getValuesArrayMap(APP_CONFIG_UI_ROUTE_MAP).toString().length()-3))
                .replaceAll(" ", "")
                .split(",");

        for(Map.Entry<String,List<String>> value : permissionWhiteLists.entrySet()){
                getRouteMappings(value, routeMaps);
        }

        for(Map.Entry<String,List<String>> value : permissionWhiteLists.entrySet()){
            if(value.getValue().toString().contains("base_rights")) {
                List<String> newValue = value.getValue();
                newValue.addAll(permissionWhiteLists.get("ui.permissions.base"));
                value.setValue(newValue);
            }
        }

        return permissionWhiteLists;
    }


    private Map.Entry<String, List<String>> getRouteMappings(Map.Entry<String, List<String>> value, String[] routeMaps){
        for(String routeMap : routeMaps) {
            if(value.getValue().toString().contains(routeMap.substring(routeMap.indexOf("=")+1)+",")){
                List<String> newValue = value.getValue();
                newValue.add((routeMap.substring(0,routeMap.indexOf("="))).trim());
                value.setValue(newValue);
            }
        }
        return value;
    }


}
