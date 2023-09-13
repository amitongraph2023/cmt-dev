package com.panera.cmt.service.paytronix;

import com.panera.cmt.dto.paytronix.BalanceDTO;
import com.panera.cmt.dto.paytronix.TransactionsDTO;
import com.panera.cmt.dto.paytronix.WalletsDTO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

public interface IPaytronixEsbService {

    // public static final String ADD_REDEEM_URI           = "/transaction/addRedeem";
    Optional<ResponseEntity<String>>addRedeemReward(String loyaltyCard, String checkNumber, BigDecimal checkTotal);

    // public static final String TRANSACTION_HISTORY_URI  = "/guest/transactionHistory";
    Optional<ResponseEntity<TransactionsDTO>> getTransactionHistory(String loyaltyCard, String startDate);

    //    public static final String GET_WALLET_CODES_URI     = "/transaction/loadMap";
    Optional<ResponseEntity<WalletsDTO>> getWalletCodes();

    // public static final String GET_BALANCE_URI          = "/guest/accountInformation";
    Optional<ResponseEntity<BalanceDTO>> getBalance(String loyaltyCard);

    // public static final String VOID_ADD_REDEEM_URI      = "/transaction/voidAddRedeem";
    Optional<ResponseEntity<String>> voidAddRedeemReward(String loyaltyCard, String checkNumber, BigDecimal checkTotal);

}

