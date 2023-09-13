package com.panera.cmt.service.chub;

import com.panera.cmt.dto.chub.CardExchangeDTO;
import com.panera.cmt.dto.chub.LoyaltyAccountsDTO;
import com.panera.cmt.dto.chub.LoyaltyDTO;
import com.panera.cmt.dto.chub.LoyaltyRewardsEnabledDTO;
import com.panera.cmt.dto.proxy.chub.CustomerLoyalty;
import com.panera.cmt.entity.ResponseHolder;

import java.util.Optional;

public interface ICustomerLoyaltyService {

    Optional<ResponseHolder<String>> cardExchange(Long customerId, String existingLoyaltyCard, CardExchangeDTO dto, boolean excludePX);

    Optional<ResponseHolder<LoyaltyAccountsDTO>> updateLoyalty(Long customerId);

	Optional<CustomerLoyalty> getLoyaltyRewardsEnabled(Long customerId);

	Optional<ResponseHolder<LoyaltyRewardsEnabledDTO>> updateLoyaltyRewardsEnabled(Long customerId, LoyaltyRewardsEnabledDTO rewardsEnabledDto);
}
