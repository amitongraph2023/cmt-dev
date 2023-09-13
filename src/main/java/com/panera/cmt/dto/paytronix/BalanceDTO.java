package com.panera.cmt.dto.paytronix;

import lombok.Data;

@Data
public class BalanceDTO {
    private PointBalanceDTO[] pointBalances;
    private RewardBalanceDTO[] rewardBalances;
}
