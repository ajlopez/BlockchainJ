package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.execution.TopExecutionContext;
import com.ajlopez.blockchain.execution.TransactionExecutor;
import com.ajlopez.blockchain.json.JsonStringValue;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.json.JsonValueType;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HexUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 02/12/2018.
 */
public class AccountsProcessorTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void unknownMethod() throws JsonRpcException, IOException {
        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_foo", params);

        AccountsProcessor processor = new AccountsProcessor(null);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Unknown method 'eth_foo'");
        processor.processRequest(request);
    }

    @Test
    public void getBalanceWithNoParameter() throws JsonRpcException, IOException {
        List<JsonValue> params = Collections.emptyList();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBalance", params);

        AccountsProcessor processor = new AccountsProcessor(null);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Invalid number of parameters: expected 1 thru 2 found 0");
        processor.processRequest(request);
    }

    @Test
    public void getBalanceWithThreeParameters() throws JsonRpcException, IOException {
        List<JsonValue> params = new ArrayList();
        params.add(new JsonStringValue("foo"));
        params.add(new JsonStringValue("bar"));
        params.add(new JsonStringValue("foobar"));

        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBalance", params);

        AccountsProcessor processor = new AccountsProcessor(null);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Invalid number of parameters: expected 1 thru 2 found 3");
        processor.processRequest(request);
    }

    @Test
    public void getBalances() throws JsonRpcException, IOException {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();

        long initialBalance = 1000000;
        long transferAmount = 1000;
        int nblocks = 10;

        AccountsProcessor processor = createProcessor(sender, receiver, initialBalance, transferAmount, nblocks);

        checkBalance(processor, sender, "earliest", initialBalance);
        checkBalance(processor, receiver, "earliest", 0);

        checkBalance(processor, sender, "latest", initialBalance - transferAmount * nblocks);
        checkBalance(processor, receiver, "latest", transferAmount * nblocks);

        checkBalance(processor, sender,initialBalance - transferAmount * nblocks);
        checkBalance(processor, receiver,transferAmount * nblocks);

        for (int k = 0; k <= nblocks; k++) {
            String decimalBlockId = Integer.toString(k);
            String hexadecimalBlockId = "0x" + Integer.toString(k, 16);

            checkBalance(processor, sender, decimalBlockId, initialBalance - k * transferAmount);
            checkBalance(processor, receiver, decimalBlockId, k * transferAmount);

            checkBalance(processor, sender, hexadecimalBlockId, initialBalance - k * transferAmount);
            checkBalance(processor, receiver, hexadecimalBlockId, k * transferAmount);
        }
    }

    @Test
    public void getTransactionCountWithNoParameter() throws JsonRpcException, IOException {
        List<JsonValue> params = Collections.emptyList();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getTransactionCount", params);

        AccountsProcessor processor = new AccountsProcessor(null);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Invalid number of parameters: expected 1 thru 2 found 0");
        processor.processRequest(request);
    }

    @Test
    public void getTransactionCountWithThreeParameters() throws JsonRpcException, IOException {
        List<JsonValue> params = new ArrayList();
        params.add(new JsonStringValue("foo"));
        params.add(new JsonStringValue("bar"));
        params.add(new JsonStringValue("foobar"));

        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getTransactionCount", params);

        AccountsProcessor processor = new AccountsProcessor(null);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Invalid number of parameters: expected 1 thru 2 found 3");
        processor.processRequest(request);
    }

    @Test
    public void getTransactionCounts() throws JsonRpcException, IOException {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();

        long initialBalance = 1000000;
        long transferAmount = 1000;
        int nblocks = 10;

        AccountsProcessor processor = createProcessor(sender, receiver, initialBalance, transferAmount, nblocks);

        checkTransactionCount(processor, sender, "earliest", 0);
        checkTransactionCount(processor, receiver, "earliest", 0);

        checkTransactionCount(processor, sender, "latest", nblocks);
        checkTransactionCount(processor, receiver, "latest", 0);

        checkTransactionCount(processor, sender,nblocks);
        checkTransactionCount(processor, receiver,0);

        for (int k = 0; k <= nblocks; k++) {
            String decimalBlockId = Integer.toString(k);
            String hexadecimalBlockId = "0x" + Integer.toString(k, 16);

            checkTransactionCount(processor, sender, decimalBlockId, k);
            checkTransactionCount(processor, receiver, decimalBlockId, 0);

            checkTransactionCount(processor, sender, hexadecimalBlockId, k);
            checkTransactionCount(processor, receiver, hexadecimalBlockId, 0);
        }
    }

    private static void checkBalance(AccountsProcessor processor, Address address, String blockId, long expectedBalance) throws JsonRpcException, IOException {
        JsonStringValue addressValue = new JsonStringValue(address.toString());
        JsonStringValue blockIdValue = new JsonStringValue(blockId);

        List<JsonValue> params = new ArrayList<>();
        params.add(addressValue);
        params.add(blockIdValue);

        JsonRpcRequest request = new JsonRpcRequest("1", "2.0", "eth_getBalance", params);

        checkBalanceResponse(processor.processRequest(request), expectedBalance);
    }

    private static void checkBalance(AccountsProcessor processor, Address address, long expectedBalance) throws JsonRpcException, IOException {
        JsonStringValue addressValue = new JsonStringValue(address.toString());

        List<JsonValue> params = new ArrayList<>();
        params.add(addressValue);

        JsonRpcRequest request = new JsonRpcRequest("1", "2.0", "eth_getBalance", params);

        checkBalanceResponse(processor.processRequest(request), expectedBalance);
    }

    private static void checkBalanceResponse(JsonRpcResponse response, long expectedBalance) {
        Assert.assertNotNull(response);

        JsonValue result = response.getResult();
        Assert.assertNotNull(result);

        Assert.assertEquals(JsonValueType.STRING, result.getType());

        String value = (String)result.getValue();

        Assert.assertTrue(value.startsWith("0x"));

        byte[] bytes = HexUtils.hexStringToBytes(value);

        Assert.assertEquals(expectedBalance, (new BigInteger(1, bytes)).longValue());
    }

    private static void checkTransactionCount(AccountsProcessor processor, Address address, String blockId, long expectedNonce) throws JsonRpcException, IOException {
        JsonStringValue addressValue = new JsonStringValue(address.toString());
        JsonStringValue blockIdValue = new JsonStringValue(blockId);

        List<JsonValue> params = new ArrayList<>();
        params.add(addressValue);
        params.add(blockIdValue);

        JsonRpcRequest request = new JsonRpcRequest("1", "2.0", "eth_getTransactionCount", params);

        checkTransactionCountResponse(processor.processRequest(request), expectedNonce);
    }

    private static void checkTransactionCount(AccountsProcessor processor, Address address, long expectedNonce) throws JsonRpcException, IOException {
        JsonStringValue addressValue = new JsonStringValue(address.toString());

        List<JsonValue> params = new ArrayList<>();
        params.add(addressValue);

        JsonRpcRequest request = new JsonRpcRequest("1", "2.0", "eth_getTransactionCount", params);

        checkTransactionCountResponse(processor.processRequest(request), expectedNonce);
    }

    private static void checkTransactionCountResponse(JsonRpcResponse response, long expectedNonce) {
        Assert.assertNotNull(response);

        JsonValue result = response.getResult();
        Assert.assertNotNull(result);

        Assert.assertEquals(JsonValueType.STRING, result.getType());

        String value = (String)result.getValue();

        Assert.assertTrue(value.startsWith("0x"));

        byte[] bytes = HexUtils.hexStringToBytes(value);

        Assert.assertEquals(expectedNonce, (new BigInteger(1, bytes)).longValue());
    }

    private static AccountsProcessor createProcessor(Address sender, Address receiver, long initialBalance, long transferAmount, int nblocks) throws IOException {
        Stores stores = new MemoryStores();
        AccountStore accountStore = new AccountStore(stores.getAccountTrieStore().retrieve(Trie.EMPTY_TRIE_HASH));

        Account senderAccount = new Account(Coin.fromUnsignedLong(initialBalance), 0, 0, null, null);

        accountStore.putAccount(sender, senderAccount);
        accountStore.save();

        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores, accountStore);

        for (int k = 0; k < nblocks; k++) {
            Transaction transaction = new Transaction(sender, receiver, Coin.fromUnsignedLong(transferAmount), k, null, 6000000, Coin.ZERO);
            TransactionExecutor transactionExecutor = new TransactionExecutor(new TopExecutionContext(accountStore, null, null));
            List<Transaction> transactions = Collections.singletonList(transaction);

            transactionExecutor.executeTransactions(transactions, null);

            Block parent = blockChain.getBestBlock();
            Address coinbase = FactoryHelper.createRandomAddress();

            accountStore.save();

            Block block = new Block(parent.getNumber() + 1, parent.getHash(), null, transactions, null, accountStore.getRootHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

            blockChain.connectBlock(block);
        }

        BlocksProvider blocksProvider = new BlocksProvider(blockChain);
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(stores.getAccountTrieStore());
        AccountsProvider accountsProvider = new AccountsProvider(blocksProvider, accountStoreProvider);

        return new AccountsProcessor(accountsProvider);
    }
}
