package com.panera.cmt.dto.paytronix;

import lombok.Data;

import java.util.List;

@Data
public class TransactionDTO {
    private String storeName;
    private long transactionIdLong;
    private String transactionType;
    private boolean hasVisibleComment;
    private List<TransactionDetailsDTO> details;
    private String posTransactionNumber;
    private String storeCode;
    private String cardNumber;

    private String datetime;
}
