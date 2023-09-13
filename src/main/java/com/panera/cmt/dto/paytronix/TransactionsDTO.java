package com.panera.cmt.dto.paytronix;

import lombok.Data;

import java.util.List;

@Data
public class TransactionsDTO {
    private List<TransactionDTO> transactions;
}
