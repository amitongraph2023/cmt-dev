package com.panera.cmt.service.chub;

import com.panera.cmt.dto.chub.CardExchangeDTO;
import com.panera.cmt.dto.chub.LoyaltyAccountsDTO;
import com.panera.cmt.dto.chub.LoyaltyDTO;
import com.panera.cmt.dto.chub.LoyaltyRewardsEnabledDTO;
import com.panera.cmt.dto.proxy.chub.CustomerLoyalty;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_CARD_EXCHANGE;
import static com.panera.cmt.util.SharedUtils.isNull;

@Service
@Slf4j
public class CustomerLoyaltyService extends BaseCustomerHubService implements ICustomerLoyaltyService {

    @Override
    public Optional<ResponseHolder<String>> cardExchange(Long customerId, String existingLoyaltyCard, CardExchangeDTO dto, boolean excludePX) {
        if (isNull(customerId, existingLoyaltyCard, dto)) {
            return Optional.empty();
        }

        StopWatch stopwatch = new StopWatch(log, "cardExchange", String.format("Card exchange for customerId=%d from existingLoyatlyCard=%s to newLoyaltyCard=%s",
                customerId, existingLoyaltyCard, dto.getCardNumber()));

        return Optional.ofNullable(doPost(String.class, stopwatch, createAudit(ActionType.UPDATE, customerId, dto), dto, ChubEndpoints.CARD_EXCHANGE, customerId, existingLoyaltyCard, excludePX));
    }

    @Override
    public Optional<ResponseHolder<LoyaltyAccountsDTO>> updateLoyalty(Long customerId) {
        if (isNull(customerId)) {
            return Optional.empty();
        }

        StopWatch stopwatch = new StopWatch(log, "updateLoyalty", String.format("Update loyalty account for customerId=%d",
                customerId));

        return Optional.ofNullable(doPut(LoyaltyAccountsDTO.class, stopwatch, createAudit(ActionType.UPDATE, customerId, null), new HashMap<String, String>()  , ChubEndpoints.LOYALTY_UPDATE, customerId));
    }
    
	@Override
	public Optional<CustomerLoyalty> getLoyaltyRewardsEnabled(Long customerId) {
		if (isNull(customerId)) {
			return Optional.empty();
		}

		StopWatch stopwatch = new StopWatch(log, "getLoyaltyRewardsEnabled",
				String.format("Get loyalty account for customerId=%d", customerId));

		return Optional
				.ofNullable(doGet(CustomerLoyalty.class, stopwatch, ChubEndpoints.LOYALTY_UPDATE, customerId));
	}

	@Override
	public Optional<ResponseHolder<LoyaltyRewardsEnabledDTO>> updateLoyaltyRewardsEnabled(Long customerId,
			LoyaltyRewardsEnabledDTO rewardsEnabledDto) {
		if (isNull(customerId)) {
			return Optional.empty();
		}

		StopWatch stopwatch = new StopWatch(log, "updateLoyaltyRewards",
				String.format("Update loyalty rewards for customerId=%d", customerId));

		return Optional.ofNullable(doPut(LoyaltyRewardsEnabledDTO.class, stopwatch,
				createAudit(ActionType.UPDATE, customerId, rewardsEnabledDto), rewardsEnabledDto,
				ChubEndpoints.LOYALTY_REWARDS_UPDATE, customerId));
	}
	
    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_CARD_EXCHANGE;
    }
}
