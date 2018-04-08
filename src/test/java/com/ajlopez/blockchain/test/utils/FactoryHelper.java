package com.ajlopez.blockchain.test.utils;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.processors.*;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by ajlopez on 26/01/2018.
 */
public class FactoryHelper {
    public static Transaction createTransaction(int value) {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger bivalue = BigInteger.valueOf(value);
        Random random = new Random();
        int nonce = Math.abs(random.nextInt());

        return new Transaction(sender, receiver, bivalue, nonce);
    }

    public static BlockProcessor createBlockProcessor() {
        return new BlockProcessor(new BlockChain(), new OrphanBlocks());
    }

    public static MessageProcessor createMessageProcessor(BlockProcessor blockProcessor) {
        return new MessageProcessor(blockProcessor, null, null, null);
    }

    public static MessageProcessor createMessageProcessor(BlockProcessor blockProcessor, PeerProcessor peerProcessor) {
        return new MessageProcessor(blockProcessor, null, peerProcessor, null);
    }

    public static MessageProcessor createMessageProcessor(BlockProcessor blockProcessor, PeerProcessor peerProcessor, OutputProcessor outputProcessor) {
        return new MessageProcessor(blockProcessor, null, peerProcessor, outputProcessor);
    }

    public static MessageProcessor createMessageProcessor(BlockProcessor blockProcessor, OutputProcessor outputProcessor) {
        return new MessageProcessor(blockProcessor, null, null, outputProcessor);
    }

    public static MessageProcessor createMessageProcessor(TransactionProcessor   transactionProcessor) {
        return new MessageProcessor(null, transactionProcessor, null, null);
    }
}
