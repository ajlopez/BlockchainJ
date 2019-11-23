package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.CodeStore;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.vms.eth.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class TransactionExecutorTest {
    @Test
    public void executeTransaction() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore, 1000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(100), 0, null, 6000000, Coin.ZERO);

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, null, null));

        List<Transaction> result = executor.executeTransactions(Collections.singletonList(transaction), null);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());

        Transaction tresult = result.get(0);

        Assert.assertEquals(transaction, tresult);

        Coin senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(1000 - 100), senderBalance);

        Coin receiverBalance = accountStore.getAccount(receiverAddress).getBalance();
        Assert.assertNotNull(receiverAddress);
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiverBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(1, accountStore.getAccount(senderAddress).getNonce());
    }

    @Test
    public void executeTransactionWithGasPrice() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore, 100000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(100), 0, null, 60000, Coin.ONE);

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, null, null));

        Address coinbase = FactoryHelper.createRandomAddress();
        BlockData blockData = new BlockData(1,2,coinbase, Difficulty.ONE);
        List<Transaction> result = executor.executeTransactions(Collections.singletonList(transaction), blockData);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());

        Transaction tresult = result.get(0);

        Assert.assertEquals(transaction, tresult);

        Coin senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(100000 - FeeSchedule.TRANSFER.getValue() - 100), senderBalance);

        Coin receiverBalance = accountStore.getAccount(receiverAddress).getBalance();
        Assert.assertNotNull(receiverAddress);
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiverBalance);

        Coin coinbaseBalance = accountStore.getAccount(coinbase).getBalance();
        Assert.assertNotNull(coinbase);
        Assert.assertEquals(Coin.fromUnsignedLong(FeeSchedule.TRANSFER.getValue()), coinbaseBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(1, accountStore.getAccount(senderAddress).getNonce());
    }

    @Test
    public void executeTransactionWithGasPriceAndData() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore, 100000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        byte[] data = new byte[] { 0x00, 0x01, 0x02, 0x00, 0x03 };

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(100), 0, data, 60000, Coin.ONE);

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, null, null));

        Address coinbase = FactoryHelper.createRandomAddress();
        BlockData blockData = new BlockData(1,2,coinbase, Difficulty.ONE);
        List<Transaction> result = executor.executeTransactions(Collections.singletonList(transaction), blockData);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());

        Transaction tresult = result.get(0);

        Assert.assertEquals(transaction, tresult);

        Coin senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(100000 - FeeSchedule.TRANSFER.getValue() - 2 * FeeSchedule.DATAZERO.getValue() - 3 * FeeSchedule.DATANONZERO.getValue() - 100), senderBalance);

        Coin receiverBalance = accountStore.getAccount(receiverAddress).getBalance();
        Assert.assertNotNull(receiverAddress);
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiverBalance);

        Coin coinbaseBalance = accountStore.getAccount(coinbase).getBalance();
        Assert.assertNotNull(coinbase);
        Assert.assertEquals(Coin.fromUnsignedLong(FeeSchedule.TRANSFER.getValue() + 2 * FeeSchedule.DATAZERO.getValue() + 3 * FeeSchedule.DATANONZERO.getValue()), coinbaseBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(1, accountStore.getAccount(senderAddress).getNonce());
    }

    @Test
    public void executeTwoTransactions() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore, 1000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Transaction transaction1 = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(100), 0, null, 6000000, Coin.ZERO);
        Transaction transaction2 = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(50), 1, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, null, null));

        List<Transaction> result = executor.executeTransactions(transactions, null);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2, result.size());

        Transaction tresult1 = result.get(0);
        Assert.assertEquals(transaction1, tresult1);
        Transaction tresult2 = result.get(1);
        Assert.assertEquals(transaction2, tresult2);

        Coin senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(1000 - 150), senderBalance);

        Coin receiverBalance = accountStore.getAccount(receiverAddress).getBalance();
        Assert.assertNotNull(receiverAddress);
        Assert.assertEquals(Coin.fromUnsignedLong(150), receiverBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(2, accountStore.getAccount(senderAddress).getNonce());
    }

    @Test
    public void secondTransactionRejectedByNonce() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore, 1000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Transaction transaction1 = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(100), 0, null, 6000000, Coin.ZERO);
        Transaction transaction2 = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(50), 0, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();

        transactions.add(transaction1);
        transactions.add(transaction2);

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, null, null));

        List<Transaction> result = executor.executeTransactions(transactions, null);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());

        Transaction tresult1 = result.get(0);
        Assert.assertEquals(transaction1, tresult1);

        Coin senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(1000 - 100), senderBalance);

        Coin receiverBalance = accountStore.getAccount(receiverAddress).getBalance();
        Assert.assertNotNull(receiverAddress);
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiverBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(1, accountStore.getAccount(senderAddress).getNonce());
    }

    @Test
    public void secondTransactionRejectedByInsufficientBalance() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore, 1000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Transaction transaction1 = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(100), 0, null, 6000000, Coin.ZERO);
        Transaction transaction2 = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(5000), 1, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, null, null));

        List<Transaction> result = executor.executeTransactions(transactions, null);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());

        Transaction tresult1 = result.get(0);
        Assert.assertEquals(transaction1, tresult1);

        Coin senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(1000 - 100), senderBalance);

        Coin receiverBalance = accountStore.getAccount(receiverAddress).getBalance();
        Assert.assertNotNull(receiverAddress);
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiverBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(1, accountStore.getAccount(senderAddress).getNonce());
    }

    @Test
    public void secondTransactionRejectedByInsufficientBalanceToCoverGasLimit() throws IOException {
        AccountStore accountStore = new AccountStore(new Trie());

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore, 1000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Transaction transaction1 = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(100), 0, null, 6000000, Coin.ZERO);
        Transaction transaction2 = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(100), 1, null, 6000000, Coin.ONE);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, null, null));

        List<Transaction> result = executor.executeTransactions(transactions, null);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());

        Transaction tresult1 = result.get(0);
        Assert.assertEquals(transaction1, tresult1);

        Coin senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(1000 - 100), senderBalance);

        Coin receiverBalance = accountStore.getAccount(receiverAddress).getBalance();
        Assert.assertNotNull(receiverAddress);
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiverBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(1, accountStore.getAccount(senderAddress).getNonce());
    }

    @Test
    public void execute999Transactions() throws IOException {
        int ntxs = 1000;
        AccountStore accountStore = new AccountStore(new Trie());

        List<Address> addresses = new ArrayList<>();

        for (int k = 0; k < ntxs; k++)
            addresses.add(FactoryHelper.createRandomAddress());

        Account account = new Account(Coin.fromUnsignedLong(ntxs + 1), 0, null, null);

        accountStore.putAccount(addresses.get(0), account);

        List<Transaction> transactions = new ArrayList<>();

        for (int k = 0; k < ntxs - 1; k++) {
            Transaction transaction = new Transaction(addresses.get(k), addresses.get(k + 1), Coin.fromUnsignedLong(ntxs - k), 0, null, 6000000, Coin.ZERO);
            transactions.add(transaction);
        }

        TransactionExecutor transactionExecutor = new TransactionExecutor(new TopExecutionContext(accountStore, null, null));

        long millis = System.currentTimeMillis();
        List<Transaction> executed = transactionExecutor.executeTransactions(transactions, null);
        millis = System.currentTimeMillis() - millis;
        System.out.println(millis);

        millis = System.currentTimeMillis();
        accountStore.getRootHash();
        millis = System.currentTimeMillis() - millis;
        System.out.println(millis);

        Assert.assertNotNull(executed);
        Assert.assertEquals(transactions.size(), executed.size());
    }

    private static Storage executeTransactionInvokingCode(byte[] code) throws IOException {
        return executeTransactionInvokingCode(code, FactoryHelper.createRandomAddress(), FactoryHelper.createRandomAddress());
    }

    @Test
    public void executeTransactionInvokingContractCode() throws IOException {
        byte[] code = new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x00, OpCodes.SSTORE };

        Storage storage = executeTransactionInvokingCode(code);

        Assert.assertNotNull(storage);
        Assert.assertEquals(DataWord.ONE, storage.getValue(DataWord.ZERO));
    }

    @Test
    public void executeTransactionInvokingContractCodeUsingData() throws IOException {
        byte[] code = new byte[] { OpCodes.PUSH1, 0x00, OpCodes.CALLDATALOAD, OpCodes.PUSH1, 0x00, OpCodes.SSTORE };

        Storage storage = executeTransactionInvokingCode(code);

        Assert.assertNotNull(storage);
        Assert.assertEquals(DataWord.fromBytesToLeft(new byte[] { 0x01, 0x02, 0x03, 0x04 }, 0, 4), storage.getValue(DataWord.ZERO));
    }

    @Test
    public void executeTransactionInvokingContractCodeGettingMessageData() throws IOException {
        byte[] code = new byte[] {
                OpCodes.ORIGIN, OpCodes.PUSH1, 0x00, OpCodes.SSTORE,
                OpCodes.CALLER, OpCodes.PUSH1, 0x01, OpCodes.SSTORE,
                OpCodes.ADDRESS, OpCodes.PUSH1, 0x02, OpCodes.SSTORE,
                OpCodes.CALLVALUE, OpCodes.PUSH1, 0x03, OpCodes.SSTORE,
                OpCodes.GAS, OpCodes.PUSH1, 0x04, OpCodes.SSTORE,
                OpCodes.GASPRICE, OpCodes.PUSH1, 0x05, OpCodes.SSTORE
        };

        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Storage storage = executeTransactionInvokingCode(code, senderAddress, receiverAddress);

        Assert.assertNotNull(storage);
        Assert.assertEquals(DataWord.fromAddress(senderAddress), storage.getValue(DataWord.ZERO));
        Assert.assertEquals(DataWord.fromAddress(senderAddress), storage.getValue(DataWord.ONE));
        Assert.assertEquals(DataWord.fromAddress(receiverAddress), storage.getValue(DataWord.TWO));
        Assert.assertEquals(DataWord.fromUnsignedInteger(100), storage.getValue(DataWord.fromUnsignedInteger(3)));
        Assert.assertEquals(DataWord.fromUnsignedLong(200000L - FeeSchedule.BASE.getValue() - 4 * (FeeSchedule.BASE.getValue() + FeeSchedule.VERYLOW.getValue() + FeeSchedule.SSET.getValue())), storage.getValue(DataWord.fromUnsignedInteger(4)));
        Assert.assertEquals(DataWord.ONE, storage.getValue(DataWord.fromUnsignedInteger(5)));
    }

    @Test
    public void executeTransactionInvokingContractCodeGettingBlockData() throws IOException {
        byte[] code = new byte[] {
                OpCodes.NUMBER, OpCodes.PUSH1, 0x00, OpCodes.SSTORE,
                OpCodes.TIMESTAMP, OpCodes.PUSH1, 0x01, OpCodes.SSTORE,
                OpCodes.COINBASE, OpCodes.PUSH1, 0x02, OpCodes.SSTORE,
                OpCodes.DIFFICULTY, OpCodes.PUSH1, 0x03, OpCodes.SSTORE
        };

        Address coinbase = FactoryHelper.createRandomAddress();
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Storage storage = executeTransactionInvokingCode(code, coinbase, senderAddress, receiverAddress);

        Assert.assertNotNull(storage);
        Assert.assertEquals(DataWord.ONE, storage.getValue(DataWord.ZERO));
        Assert.assertEquals(DataWord.TWO, storage.getValue(DataWord.ONE));
        Assert.assertEquals(DataWord.fromAddress(coinbase), storage.getValue(DataWord.TWO));
        Assert.assertEquals(DataWord.ONE, storage.getValue(DataWord.fromUnsignedInteger(3)));
    }

    private static Storage executeTransactionInvokingCode(byte[] code, Address senderAddress, Address receiverAddress) throws IOException {
        Address coinbase = FactoryHelper.createRandomAddress();

        return executeTransactionInvokingCode(code, coinbase, senderAddress, receiverAddress);
    }

    private static Storage executeTransactionInvokingCode(byte[] code, Address coinbase, Address senderAddress, Address receiverAddress) throws IOException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStorageProvider trieStorageProvider = new TrieStorageProvider(new TrieStore(new HashMapStore()));
        AccountStore accountStore = new AccountStore(new Trie());

        FactoryHelper.createAccountWithBalance(accountStore, senderAddress, 1000000);
        FactoryHelper.createAccountWithCode(accountStore, codeStore, receiverAddress, code);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(100), 0, new byte[] { 0x01, 0x02, 0x03, 0x04 }, 200000, Coin.ONE);

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, trieStorageProvider, codeStore));

        BlockData blockData = new BlockData(1,2, coinbase, Difficulty.ONE);

        List<Transaction> result = executor.executeTransactions(Collections.singletonList(transaction), blockData);

        Account receiver = accountStore.getAccount(receiverAddress);

        Assert.assertNotNull(receiver);
        Assert.assertNotNull(receiver.getStorageHash());

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());

        Transaction tresult = result.get(0);

        Assert.assertEquals(transaction, tresult);

        Coin coinbaseBalance = accountStore.getAccount(coinbase).getBalance();
        Assert.assertNotNull(coinbaseBalance);
        Assert.assertNotEquals(Coin.ZERO, coinbaseBalance);

        Coin senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(Coin.fromUnsignedLong(1000000 - 100).subtract(coinbaseBalance), senderBalance);

        Assert.assertNotNull(receiverAddress);

        Coin receiverBalance = receiver.getBalance();
        Assert.assertEquals(Coin.fromUnsignedLong(100), receiverBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(1, accountStore.getAccount(senderAddress).getNonce());

        return trieStorageProvider.retrieve(receiver.getStorageHash());
    }
}
