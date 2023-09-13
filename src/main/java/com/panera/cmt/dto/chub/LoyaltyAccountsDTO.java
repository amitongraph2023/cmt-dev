package com.panera.cmt.dto.chub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class LoyaltyAccountsDTO {
    private LoyaltyDTO currentAccount;
    private LoyaltyDTO previousAccount;
}
