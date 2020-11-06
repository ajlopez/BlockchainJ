package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
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
    public void executeCallReturningSender() throws IOException {
        Stores stores = new Stores(new MemoryKeyValueStores());
        AccountStore accountStore = stores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);
        CodeStore codeStore = stores.getCodeStore();

        byte[] calleeCode = new byte[]{
                OpCodes.CALLER,
                OpCodes.PUSH1, 0,
                OpCodes.MSTORE,
                OpCodes.PUSH1, 32,
                OpCodes.PUSH1, 0,
                OpCodes.RETURN
        };

        Address callee = createAccountWithCode(accountStore, codeStore, calleeCode);

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

        Address caller = createAccountWithCode(accountStore, codeStore, callerCode);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);

        MessageData messageData = new MessageData(caller, null, null, Coin.ZERO, 5000000, Coin.ZERO, null, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, executionContext, 0), null);

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

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
    public void executeCallThatReverts() throws IOException {
        Stores stores = new Stores(new MemoryKeyValueStores());
        AccountStore accountStore = stores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);
        CodeStore codeStore = stores.getCodeStore();

        byte[] calleeCode = new byte[]{
                OpCodes.PUSH1, 0,
                OpCodes.PUSH1, 0,
                OpCodes.REVERT
        };

        Address callee = createAccountWithCode(accountStore, codeStore, calleeCode);

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

        Address caller = createAccountWithCode(accountStore, codeStore, callerCode);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);

        MessageData messageData = new MessageData(caller, null, null, Coin.ZERO, 5000000, Coin.ZERO, null, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, executionContext, 0), null);

        System.arraycopy(callee.getBytes(), 0, callerCode, callerCode.length - 4 - Address.ADDRESS_BYTES, Address.ADDRESS_BYTES);

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
        Stores stores = new Stores(new MemoryKeyValueStores());
        AccountStore accountStore = stores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);
        CodeStore codeStore = stores.getCodeStore();

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

        Address callee = createAccountWithCode(accountStore, codeStore, calleeCode);

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

        Address caller = createAccountWithCode(accountStore, codeStore, callerCode);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);

        MessageData messageData = new MessageData(caller, null, null, Coin.ZERO, 5000000, Coin.ZERO, null, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, executionContext, 0), null);

        ExecutionResult executionResult = virtualMachine.execute(callerCode);

        // TODO check gas used

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), virtualMachine.getMemory().getValue(0));

        Assert.assertEquals(1, virtualMachine.getDataStack().size());
        Assert.assertEquals(DataWord.ONE, virtualMachine.getDataStack().pop());
    }

    private static Address createAccountWithCode(AccountStore accountStore, CodeStore codeStore, byte[] code) throws IOException {
        Hash codeHash = HashUtils.calculateHash(code);
        codeStore.putCode(codeHash, code);

        Account account = new Account(Coin.ZERO, 0, code.length, codeHash, null);
        Address address = FactoryHelper.createRandomAddress();

        accountStore.putAccount(address, account);

        return address;
    }
}
