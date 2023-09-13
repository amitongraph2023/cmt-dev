package com.panera.cmt.dto.subscription_service;

import com.panera.cmt.dto.proxy.subscription_service.CoffeeSubscriptionUsage;
import com.panera.cmt.dto.proxy.subscription_service.SubscriptionServiceCoffeeUsageResults;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubscriptionServiceCoffeeUsageResultsDTO {
    private List<CoffeeSubscriptionUsage> results;

    public static SubscriptionServiceCoffeeUsageResultsDTO fromEntity(SubscriptionServiceCoffeeUsageResults entity) {
        return SubscriptionServiceCoffeeUsageResultsDTO.builder()
                .results(entity.getResults())
                .build();
    }

    public SubscriptionServiceCoffeeUsageResults toEntity() {
        return SubscriptionServiceCoffeeUsageResults.builder()
                .results(results)
                .build();
    }
}