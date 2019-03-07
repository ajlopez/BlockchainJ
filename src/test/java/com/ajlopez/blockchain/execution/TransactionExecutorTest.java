package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class TransactionExecutorTest {
    @Test
    public void executeTransaction() {
        AccountStore accountStore = new AccountStore(new Trie());

        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(BigInteger.valueOf(1000), 0, null, null);

        accountStore.putAccount(senderAddress, sender);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(100), 0 );

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, null));

        List<Transaction> result = executor.executeTransactions(Collections.singletonList(transaction));

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());

        Transaction tresult = result.get(0);

        Assert.assertEquals(transaction, tresult);

        BigInteger senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(BigInteger.valueOf(1000 - 100), senderBalance);

        BigInteger receiverBalance = accountStore.getAccount(receiverAddress).getBalance();
        Assert.assertNotNull(receiverAddress);
        Assert.assertEquals(BigInteger.valueOf(100), receiverBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(1, accountStore.getAccount(senderAddress).getNonce());
    }

    @Test
    public void executeTwoTransactions() {
        AccountStore accountStore = new AccountStore(new Trie());

        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(BigInteger.valueOf(1000), 0, null, null);

        accountStore.putAccount(senderAddress, sender);

        Transaction transaction1 = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(100), 0 );
        Transaction transaction2 = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(50), 1 );
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, null));

        List<Transaction> result = executor.executeTransactions(transactions);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2, result.size());

        Transaction tresult1 = result.get(0);
        Assert.assertEquals(transaction1, tresult1);
        Transaction tresult2 = result.get(1);
        Assert.assertEquals(transaction2, tresult2);

        BigInteger senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(BigInteger.valueOf(1000 - 150), senderBalance);

        BigInteger receiverBalance = accountStore.getAccount(receiverAddress).getBalance();
        Assert.assertNotNull(receiverAddress);
        Assert.assertEquals(BigInteger.valueOf(150), receiverBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(2, accountStore.getAccount(senderAddress).getNonce());
    }

    @Test
    public void secondTransactionRejectedByNonce() {
        AccountStore accountStore = new AccountStore(new Trie());

        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(BigInteger.valueOf(1000), 0, null, null);

        accountStore.putAccount(senderAddress, sender);

        Transaction transaction1 = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(100), 0 );
        Transaction transaction2 = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(50), 0 );
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, null));

        List<Transaction> result = executor.executeTransactions(transactions);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());

        Transaction tresult1 = result.get(0);
        Assert.assertEquals(transaction1, tresult1);

        BigInteger senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(BigInteger.valueOf(1000 - 100), senderBalance);

        BigInteger receiverBalance = accountStore.getAccount(receiverAddress).getBalance();
        Assert.assertNotNull(receiverAddress);
        Assert.assertEquals(BigInteger.valueOf(100), receiverBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(1, accountStore.getAccount(senderAddress).getNonce());
    }

    @Test
    public void secondTransactionRejectedByInsufficientBalance() {
        AccountStore accountStore = new AccountStore(new Trie());

        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Account sender = new Account(BigInteger.valueOf(1000), 0, null, null);

        accountStore.putAccount(senderAddress, sender);

        Transaction transaction1 = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(100), 0 );
        Transaction transaction2 = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(5000), 1 );
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        TransactionExecutor executor = new TransactionExecutor(new TopExecutionContext(accountStore, null));

        List<Transaction> result = executor.executeTransactions(transactions);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());

        Transaction tresult1 = result.get(0);
        Assert.assertEquals(transaction1, tresult1);

        BigInteger senderBalance = accountStore.getAccount(senderAddress).getBalance();
        Assert.assertNotNull(senderBalance);
        Assert.assertEquals(BigInteger.valueOf(1000 - 100), senderBalance);

        BigInteger receiverBalance = accountStore.getAccount(receiverAddress).getBalance();
        Assert.assertNotNull(receiverAddress);
        Assert.assertEquals(BigInteger.valueOf(100), receiverBalance);

        Assert.assertEquals(0, accountStore.getAccount(receiverAddress).getNonce());
        Assert.assertEquals(1, accountStore.getAccount(senderAddress).getNonce());
    }

    @Test
    public void execute999Transactions() {
        AccountStore accountStore = new AccountStore(new Trie());

        List<Address> addresses = new ArrayList<>();

        for (int k = 0; k < 1000; k++)
            addresses.add(FactoryHelper.createRandomAddress());

        Account account = new Account(BigInteger.valueOf(1001), 0, null, null);

        accountStore.putAccount(addresses.get(0), account);

        List<Transaction> transactions = new ArrayList<>();

        for (int k = 0; k < 1000 - 1; k++) {
            Transaction transaction = new Transaction(addresses.get(k), addresses.get(k + 1), BigInteger.valueOf(1000 - k), 0);
            transactions.add(transaction);
        }

        TransactionExecutor transactionExecutor = new TransactionExecutor(new TopExecutionContext(accountStore, null));

        long millis = System.currentTimeMillis();
        List<Transaction> executed = transactionExecutor.executeTransactions(transactions);
        millis = System.currentTimeMillis() - millis;
        System.out.println(millis);

        millis = System.currentTimeMillis();
        accountStore.getRootHash();
        millis = System.currentTimeMillis() - millis;
        System.out.println(millis);

        Assert.assertNotNull(executed);
        Assert.assertEquals(transactions.size(), executed.size());
    }
}
