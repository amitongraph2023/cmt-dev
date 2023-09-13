package com.panera.cmt.config.client_auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "client")
public class ClientPropertiesConfig {

    private List<ConfigUser> accounts;

    public List<ConfigUser> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<ConfigUser> accounts) {
        this.accounts = accounts;
    }

    public ConfigUser getClientByName(String name) {
        ConfigUser result = null;
        for (ConfigUser account : accounts) {
            if(account.getUsername().equalsIgnoreCase(name)) {
                result = account;
            }
        }
        return result;
    }
}
