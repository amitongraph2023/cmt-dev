package com.panera.cmt.dto.proxy.chub;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;

@Data
@SuppressWarnings("unchecked")
public class SearchCustomer {

    private Long customerId;
    private String username;
    private String firstName;
    private String lastName;
    private String defaultEmail;
    private String defaultPhone;

    public static SearchCustomer fromJsonObject(JsonObject customer) {
        if (customer == null) {
            return null;
        }

        SearchCustomer entity = new SearchCustomer();
        if (!customer.get("customerId").isJsonNull()) {
            entity.setCustomerId(customer.get("customerId").getAsLong());
        }
        if (!customer.get("username").isJsonNull()) {
            entity.setUsername(customer.get("username").getAsString());
        }
        if (!customer.get("firstName").isJsonNull()) {
            entity.setFirstName(customer.get("firstName").getAsString());
        }
        if (!customer.get("lastName").isJsonNull()) {
            entity.setLastName(customer.get("lastName").getAsString());
        }
        if (!customer.get("defaultEmail").isJsonNull() && !customer.getAsJsonObject("defaultEmail").get("emailAddress").isJsonNull()) {
            entity.setDefaultEmail(customer.getAsJsonObject("defaultEmail").get("emailAddress").getAsString());
        }
        if (!customer.get("defaultPhone").isJsonNull() && !customer.getAsJsonObject("defaultPhone").get("phoneNumber").isJsonNull()) {
            entity.setDefaultPhone(customer.getAsJsonObject("defaultPhone").get("phoneNumber").getAsString());
        }

        return entity;
    }

    public static SearchCustomer detailsFromJsonObject(JsonObject customer) {
        if (customer == null || customer.getAsJsonObject().size() == 0) {
            return null;
        }

        SearchCustomer entity = new SearchCustomer();
        if (!customer.get("customerId").isJsonNull()) {
            entity.setCustomerId(customer.get("customerId").getAsLong());
        }
        if (!customer.get("username").isJsonNull()) {
            entity.setUsername(customer.get("username").getAsString());
        }
        if (!customer.get("firstName").isJsonNull()) {
            entity.setFirstName(customer.get("firstName").getAsString());
        }
        if (!customer.get("lastName").isJsonNull()) {
            entity.setLastName(customer.get("lastName").getAsString());
        }

        if (!customer.get("emails").isJsonNull() && customer.get("emails").getAsJsonArray().size() > 0) {
            JsonArray array = customer.get("emails").getAsJsonArray();

            for (int i = 0; i < array.size(); i++) {
                JsonObject email = array.get(i).getAsJsonObject();
                if (email.get("isDefault").getAsBoolean()) {
                    entity.setDefaultEmail(email.get("emailAddress").getAsString());
                    break;
                }
            }
        }

        if (!customer.get("phones").isJsonNull() && customer.get("phones").getAsJsonArray().size() > 0) {
            JsonArray array = customer.get("phones").getAsJsonArray();

            for (int i = 0; i < array.size(); i++) {
                JsonObject phone = array.get(i).getAsJsonObject();
                if (phone.get("isDefault").getAsBoolean()) {
                    entity.setDefaultPhone(phone.get("phoneNumber").getAsString());
                    break;
                }
            }
        }

        return entity;
    }
}
