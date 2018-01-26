package com.ajlopez.blockchain.test.utils;

import com.ajlopez.blockchain.core.Address;
import com.ajlopez.blockchain.core.Transaction;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by ajlopez on 26/01/2018.
 */
public class TransactionHelper {
    public static Transaction createTransaction(int value) {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger bivalue = BigInteger.valueOf(value);
        Random random = new Random();
        int nonce = Math.abs(random.nextInt());

        return new Transaction(sender, receiver, bivalue, nonce);
    }
}
