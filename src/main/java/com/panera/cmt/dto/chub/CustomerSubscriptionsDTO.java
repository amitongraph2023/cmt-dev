package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.CustomerSubscriptions;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class CustomerSubscriptionsDTO {

    private List<SubscriptionDTO> subscriptions;
    private List<SubscriptionSuppressorDTO> suppressors;

    // Getters
    public List<SubscriptionDTO> getSubscriptions() {
        return (subscriptions != null) ? subscriptions : new ArrayList<>();
    }
    public List<SubscriptionSuppressorDTO> getSuppressors() {
        return (suppressors != null) ? suppressors : new ArrayList<>();
    }

    public static CustomerSubscriptionsDTO fromEntity(CustomerSubscriptions entity) {
        if (entity == null) {
            return null;
        }

        CustomerSubscriptionsDTO dto = new CustomerSubscriptionsDTO();
        dto.setSubscriptions(entity.getSubscriptions().stream()
                .map(SubscriptionDTO::fromEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        dto.setSuppressors(entity.getSuppressors().stream()
                .map(SubscriptionSuppressorDTO::fromEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        return dto;
    }

    public CustomerSubscriptions toEntity() {
        CustomerSubscriptions entity = new CustomerSubscriptions();
        entity.setSubscriptions(getSubscriptions().stream()
                .map(SubscriptionDTO::toEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        entity.setSuppressors(getSuppressors().stream()
                .map(SubscriptionSuppressorDTO::toEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        return entity;
    }
}
