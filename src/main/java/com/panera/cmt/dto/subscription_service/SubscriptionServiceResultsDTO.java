package com.panera.cmt.dto.subscription_service;

import com.panera.cmt.dto.proxy.subscription_service.GiftCoffeeSubscription;
import com.panera.cmt.dto.proxy.subscription_service.SubscriptionServiceGiftCoffeeResults;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubscriptionServiceResultsDTO {
    private int resultsFound;
    private List<GiftCoffeeSubscription> results;

    public static SubscriptionServiceResultsDTO fromEntity(SubscriptionServiceGiftCoffeeResults entity) {
        return SubscriptionServiceResultsDTO.builder()
                .resultsFound(entity.getResultsFound())
                .results(entity.getResults())
                .build();
    }

    public SubscriptionServiceGiftCoffeeResults toEntity() {
        return SubscriptionServiceGiftCoffeeResults.builder()
                .resultsFound(resultsFound)
                .results(results)
                .build();
    }
}
