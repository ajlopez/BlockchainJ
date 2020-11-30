package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.execution.ExecutionContext;
import com.ajlopez.blockchain.execution.TopExecutionContext;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.CodeStore;
import com.ajlopez.blockchain.store.MemoryKeyValueStores;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 04/11/2020.
 */
public class VirtualMachineCallTest {
    @Test
    public void executeCallReturningCaller() throws IOException {
        byte[] calleeCode = new byte[]{
                OpCodes.CALLER,
                OpCodes.PUSH1, 0,
                OpCodes.MSTORE,
                OpCodes.PUSH1, 32,
                OpCodes.PUSH1, 0,
                OpCodes.RETURN
        };

        Address callee = FactoryHelper.createRandomAddress();

        byte[] callerCode = new byte[]{
                OpCodes.PUSH1, 0x20,    // Out Data Size
                OpCodes.PUSH1, 0x00,    // Out Data Offset
                OpCodes.PUSH1, 0x00,    // In Data Size
                OpCodes.PUSH1, 0x00,    // In Data Offset
                OpCodes.PUSH1, 0x00,    // Value
                // Callee Address
                OpCodes.PUSH20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                OpCodes.PUSH2, 0x40, 0x00,   // Gas
                OpCodes.CALL
        };

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

        Address caller = FactoryHelper.createRandomAddress();
        Address sender = FactoryHelper.createRandomAddress();

        VirtualMachine virtualMachine = createVirtualMachine(sender, caller, callerCode, callee, calleeCode);
        ExecutionResult executionResult = virtualMachine.execute(callerCode);

        // TODO check gas used

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());

        // TODO check if it is an address
        Assert.assertEquals(caller, virtualMachine.getMemory().getValue(0).toAddress());

        Assert.assertEquals(1, virtualMachine.getDataStack().size());
        Assert.assertEquals(DataWord.ONE, virtualMachine.getDataStack().pop());
    }

    @Test
    public void executeCallReturningOriginalSender() throws IOException {
        byte[] calleeCode = new byte[]{
                OpCodes.ORIGIN,
                OpCodes.PUSH1, 0,
                OpCodes.MSTORE,
                OpCodes.PUSH1, 32,
                OpCodes.PUSH1, 0,
                OpCodes.RETURN
        };

        Address callee = FactoryHelper.createRandomAddress();

        byte[] callerCode = new byte[]{
                OpCodes.PUSH1, 0x20,    // Out Data Size
                OpCodes.PUSH1, 0x00,    // Out Data Offset
                OpCodes.PUSH1, 0x00,    // In Data Size
                OpCodes.PUSH1, 0x00,    // In Data Offset
                OpCodes.PUSH1, 0x00,    // Value
                // Callee Address
                OpCodes.PUSH20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                OpCodes.PUSH2, 0x40, 0x00,   // Gas
                OpCodes.CALL
        };

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

        Address caller = FactoryHelper.createRandomAddress();
        Address sender = FactoryHelper.createRandomAddress();

        VirtualMachine virtualMachine = createVirtualMachine(sender, caller, callerCode, callee, calleeCode);
        ExecutionResult executionResult = virtualMachine.execute(callerCode);

        // TODO check gas used

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());

        // TODO check if it is an address
        Assert.assertEquals(sender, virtualMachine.getMemory().getValue(0).toAddress());

        Assert.assertEquals(1, virtualMachine.getDataStack().size());
        Assert.assertEquals(DataWord.ONE, virtualMachine.getDataStack().pop());
    }

    @Test
    public void executeCallReturningReceiver() throws IOException {
        byte[] calleeCode = new byte[]{
                OpCodes.ADDRESS,
                OpCodes.PUSH1, 0,
                OpCodes.MSTORE,
                OpCodes.PUSH1, 32,
                OpCodes.PUSH1, 0,
                OpCodes.RETURN
        };

        Address callee = FactoryHelper.createRandomAddress();

        byte[] callerCode = new byte[]{
                OpCodes.PUSH1, 0x20,    // Out Data Size
                OpCodes.PUSH1, 0x00,    // Out Data Offset
                OpCodes.PUSH1, 0x00,    // In Data Size
                OpCodes.PUSH1, 0x00,    // In Data Offset
                OpCodes.PUSH1, 0x00,    // Value
                // Callee Address
                OpCodes.PUSH20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                OpCodes.PUSH2, 0x40, 0x00,   // Gas
                OpCodes.CALL
        };

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

        Address caller = FactoryHelper.createRandomAddress();
        Address sender = FactoryHelper.createRandomAddress();

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

        VirtualMachine virtualMachine = createVirtualMachine(sender, caller, callerCode, callee, calleeCode);
        ExecutionResult executionResult = virtualMachine.execute(callerCode);

        // TODO check gas used

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());

        // TODO check if it is an address
        Assert.assertEquals(callee, virtualMachine.getMemory().getValue(0).toAddress());

        Assert.assertEquals(1, virtualMachine.getDataStack().size());
        Assert.assertEquals(DataWord.ONE, virtualMachine.getDataStack().pop());
    }

    @Test
    public void executeCallThatReverts() throws IOException {
        byte[] calleeCode = new byte[]{
                OpCodes.PUSH1, 0,
                OpCodes.PUSH1, 0,
                OpCodes.REVERT
        };

        Address callee = FactoryHelper.createRandomAddress();

        byte[] callerCode = new byte[]{
                OpCodes.PUSH1, 0x20,    // Out Data Size
                OpCodes.PUSH1, 0x00,    // Out Data Offset
                OpCodes.PUSH1, 0x00,    // In Data Size
                OpCodes.PUSH1, 0x00,    // In Data Offset
                OpCodes.PUSH1, 0x00,    // Value
                // Callee Address
                OpCodes.PUSH20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                OpCodes.PUSH2, 0x40, 0x00,   // Gas
                OpCodes.CALL
        };

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

        Address caller = FactoryHelper.createRandomAddress();
        Address sender = FactoryHelper.createRandomAddress();

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

        VirtualMachine virtualMachine = createVirtualMachine(sender, caller, callerCode, callee, calleeCode);
        ExecutionResult executionResult = virtualMachine.execute(callerCode);

        // TODO check gas used

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());

        // TODO check if it is an address

        Assert.assertEquals(1, virtualMachine.getDataStack().size());
        Assert.assertEquals(DataWord.ZERO, virtualMachine.getDataStack().pop());
    }

    @Test
    public void executeCallIncrementingInputData() throws IOException {
        byte[] calleeCode = new byte[]{
                OpCodes.PUSH1, 0,
                OpCodes.CALLDATALOAD,
                OpCodes.PUSH1, 1,
                OpCodes.ADD,
                OpCodes.PUSH1, 0,
                OpCodes.MSTORE,
                OpCodes.PUSH1, 32,
                OpCodes.PUSH1, 0,
                OpCodes.RETURN
        };

        Address callee = FactoryHelper.createRandomAddress();

        byte[] callerCode = new byte[]{
                OpCodes.PUSH1, 0x29,    // 41
                OpCodes.PUSH1, 0x00,    // Memory offset
                OpCodes.MSTORE,         // Save in  memory

                OpCodes.PUSH1, 0x20,    // Out Data Size
                OpCodes.PUSH1, 0x00,    // Out Data Offset
                OpCodes.PUSH1, 0x20,    // In Data Size
                OpCodes.PUSH1, 0x00,    // In Data Offset
                OpCodes.PUSH1, 0x00,    // Value
                // Callee Address
                OpCodes.PUSH20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                OpCodes.PUSH2, 0x40, 0x00,   // Gas
                OpCodes.CALL
        };

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

        Address caller = FactoryHelper.createRandomAddress();
        Address sender = FactoryHelper.createRandomAddress();

        VirtualMachine virtualMachine = createVirtualMachine(sender, caller, callerCode, callee, calleeCode);
        ExecutionResult executionResult = virtualMachine.execute(callerCode);

        // TODO check gas used

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), virtualMachine.getMemory().getValue(0));

        Assert.assertEquals(1, virtualMachine.getDataStack().size());
        Assert.assertEquals(DataWord.ONE, virtualMachine.getDataStack().pop());
    }

    @Test
    public void executeCallSavingInputDataIntoStorage() throws IOException {
        byte[] calleeCode = new byte[]{
                OpCodes.PUSH1, 0,
                OpCodes.CALLDATALOAD,
                OpCodes.PUSH1, 0,
                OpCodes.SSTORE,
                OpCodes.PUSH1, 0,
                OpCodes.PUSH1, 0,
                OpCodes.RETURN
        };

        Address callee = FactoryHelper.createRandomAddress();

        byte[] callerCode = new byte[]{
                OpCodes.PUSH1, 0x2a,    // 42
                OpCodes.PUSH1, 0x00,    // Memory offset
                OpCodes.MSTORE,         // Save in  memory

                OpCodes.PUSH1, 0x20,    // Out Data Size
                OpCodes.PUSH1, 0x00,    // Out Data Offset
                OpCodes.PUSH1, 0x20,    // In Data Size
                OpCodes.PUSH1, 0x00,    // In Data Offset
                OpCodes.PUSH1, 0x00,    // Value
                // Callee Address
                OpCodes.PUSH20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                OpCodes.PUSH2, 0x60, 0x00,   // Gas
                OpCodes.CALL
        };

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

        Address caller = FactoryHelper.createRandomAddress();

        ExecutionContext executionContext = createExecutionContext(caller, callerCode, callee, calleeCode);

        Address sender = FactoryHelper.createRandomAddress();

        MessageData messageData = new MessageData(caller, sender, sender, null, Coin.ZERO, 5000000, Coin.ZERO, null, 0, 0, false, false);
        VirtualMachine virtualMachine = new VirtualMachine(null, messageData, executionContext, null);

        ExecutionResult executionResult = virtualMachine.execute(callerCode);

        // TODO check gas used

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), executionContext.getAccountStorage(callee).getValue(DataWord.ZERO));
        Assert.assertEquals(DataWord.ZERO, executionContext.getAccountStorage(caller).getValue(DataWord.ZERO));

        Assert.assertEquals(1, virtualMachine.getDataStack().size());
        Assert.assertEquals(DataWord.ONE, virtualMachine.getDataStack().pop());
    }

    @Test
    public void executeCallSavingInputDataIntoStorageAndRevert() throws IOException {
        byte[] calleeCode = new byte[]{
                OpCodes.PUSH1, 0,
                OpCodes.CALLDATALOAD,
                OpCodes.PUSH1, 0,
                OpCodes.SSTORE,
                OpCodes.PUSH1, 0,
                OpCodes.PUSH1, 0,
                OpCodes.REVERT
        };

        Address callee = FactoryHelper.createRandomAddress();

        byte[] callerCode = new byte[]{
                OpCodes.PUSH1, 0x2a,    // 42
                OpCodes.PUSH1, 0x00,    // Memory offset
                OpCodes.MSTORE,         // Save in  memory

                OpCodes.PUSH1, 0x20,    // Out Data Size
                OpCodes.PUSH1, 0x00,    // Out Data Offset
                OpCodes.PUSH1, 0x20,    // In Data Size
                OpCodes.PUSH1, 0x00,    // In Data Offset
                OpCodes.PUSH1, 0x00,    // Value
                // Callee Address
                OpCodes.PUSH20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                OpCodes.PUSH2, 0x60, 0x00,   // Gas
                OpCodes.CALL
        };

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

        Address caller = FactoryHelper.createRandomAddress();

        ExecutionContext executionContext = createExecutionContext(caller, callerCode, callee, calleeCode);

        Address sender = FactoryHelper.createRandomAddress();

        MessageData messageData = new MessageData(caller, sender, sender, null, Coin.ZERO, 5000000, Coin.ZERO, null, 0, 0, false, false);
        VirtualMachine virtualMachine = new VirtualMachine(null, messageData, executionContext, null);

        ExecutionResult executionResult = virtualMachine.execute(callerCode);

        // TODO check gas used

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());

        Assert.assertEquals(DataWord.ZERO, executionContext.getAccountStorage(callee).getValue(DataWord.ZERO));
        Assert.assertEquals(DataWord.ZERO, executionContext.getAccountStorage(caller).getValue(DataWord.ZERO));

        Assert.assertEquals(1, virtualMachine.getDataStack().size());
        Assert.assertEquals(DataWord.ZERO, virtualMachine.getDataStack().pop());
    }

    @Test
    public void executeCallThatReturnsTooMuchData() throws IOException {
        byte[] calleeCode = new byte[]{
                OpCodes.PUSH1, 0,
                OpCodes.CALLDATALOAD,
                OpCodes.PUSH1, 1,
                OpCodes.ADD,
                OpCodes.PUSH1, 0,
                OpCodes.MSTORE,
                OpCodes.PUSH1, 2,
                OpCodes.PUSH1, 32,
                OpCodes.MSTORE,
                OpCodes.PUSH1, 64,
                OpCodes.PUSH1, 0,
                OpCodes.RETURN
        };

        Address callee = FactoryHelper.createRandomAddress();

        byte[] callerCode = new byte[]{
                OpCodes.PUSH1, 0x29,    // 41
                OpCodes.PUSH1, 0x00,    // Memory offset
                OpCodes.MSTORE,         // Save in  memory

                OpCodes.PUSH1, 0x20,    // Out Data Size
                OpCodes.PUSH1, 0x00,    // Out Data Offset
                OpCodes.PUSH1, 0x20,    // In Data Size
                OpCodes.PUSH1, 0x00,    // In Data Offset
                OpCodes.PUSH1, 0x00,    // Value
                // Callee Address
                OpCodes.PUSH20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                OpCodes.PUSH2, 0x40, 0x00,   // Gas
                OpCodes.CALL
        };

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

        Address caller = FactoryHelper.createRandomAddress();
        Address sender = FactoryHelper.createRandomAddress();

        VirtualMachine virtualMachine = createVirtualMachine(sender, caller, callerCode, callee, calleeCode);
        ExecutionResult executionResult = virtualMachine.execute(callerCode);

        // TODO check gas used

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), virtualMachine.getMemory().getValue(0));
        Assert.assertEquals(DataWord.ZERO, virtualMachine.getMemory().getValue(32));

        Assert.assertEquals(1, virtualMachine.getDataStack().size());
        Assert.assertEquals(DataWord.ONE, virtualMachine.getDataStack().pop());
    }

    @Test
    public void executeCallThatReturnsLessDataThanExpected() throws IOException {
        byte[] calleeCode = new byte[]{
                OpCodes.PUSH1, 0,
                OpCodes.CALLDATALOAD,
                OpCodes.PUSH1, 1,
                OpCodes.ADD,
                OpCodes.PUSH1, 0,
                OpCodes.MSTORE,
                OpCodes.PUSH1, 16,
                OpCodes.PUSH1, 0,
                OpCodes.RETURN
        };

        Address callee = FactoryHelper.createRandomAddress();

        byte[] callerCode = new byte[]{
                OpCodes.PUSH1, 1,
                OpCodes.PUSH1, 0,
                OpCodes.MSTORE,
                OpCodes.PUSH1, 0x29,    // 41
                OpCodes.PUSH1, 0x00,    // Memory offset
                OpCodes.MSTORE,         // Save in  memory

                OpCodes.PUSH1, 0x20,    // Out Data Size
                OpCodes.PUSH1, 0x00,    // Out Data Offset
                OpCodes.PUSH1, 0x20,    // In Data Size
                OpCodes.PUSH1, 0x00,    // In Data Offset
                OpCodes.PUSH1, 0x00,    // Value
                // Callee Address
                OpCodes.PUSH20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                OpCodes.PUSH2, 0x40, 0x00,   // Gas
                OpCodes.CALL
        };

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

        Address caller = FactoryHelper.createRandomAddress();
        Address sender = FactoryHelper.createRandomAddress();

        VirtualMachine virtualMachine = createVirtualMachine(sender, caller, callerCode, callee, calleeCode);
        ExecutionResult executionResult = virtualMachine.execute(callerCode);

        // TODO check gas used

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());

        Assert.assertEquals(DataWord.ZERO, virtualMachine.getMemory().getValue(0));

        Assert.assertEquals(1, virtualMachine.getDataStack().size());
        Assert.assertEquals(DataWord.ONE, virtualMachine.getDataStack().pop());
    }

    private static VirtualMachine createVirtualMachine(Address sender, Address caller, byte[] callerCode, Address callee, byte[] calleeCode) throws IOException {
        ExecutionContext executionContext = createExecutionContext(caller, callerCode, callee, calleeCode);
        MessageData messageData = new MessageData(caller, sender, sender, null, Coin.ZERO, 5000000, Coin.ZERO, null, 0, 0, false, false);

        return new VirtualMachine(null, messageData, executionContext, null);
    }

    private static ExecutionContext createExecutionContext(Address caller, byte[] callerCode, Address callee, byte[] calleeCode) throws IOException {
        Stores stores = new Stores(new MemoryKeyValueStores());
        AccountStore accountStore = stores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);
        CodeStore codeStore = stores.getCodeStore();

        createAccountWithCode(callee, calleeCode, accountStore, codeStore);
        createAccountWithCode(caller, callerCode, accountStore, codeStore);

        return new TopExecutionContext(accountStore, stores.getTrieStorageProvider(), codeStore);
    }

    private static void createAccountWithCode(Address address, byte[] code, AccountStore accountStore, CodeStore codeStore) throws IOException {
        Hash codeHash = HashUtils.calculateHash(code);
        codeStore.putCode(codeHash, code);

        Account account = new Account(Coin.ZERO, 0, code.length, codeHash, null);
        accountStore.putAccount(address, account);
    }
}
