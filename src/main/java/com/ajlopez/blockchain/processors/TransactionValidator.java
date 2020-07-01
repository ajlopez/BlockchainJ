package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;

/**
 * Created by ajlopez on 01/07/2020.
 */
public class TransactionValidator {
    public boolean isValid(Transaction transaction) {
        if (transaction.isRichTransaction()) {
            if (transaction.getData() == null)
                return false;

            if (!transaction.getValue().isZero())
                return false;
        }

        return true;
    }
}
