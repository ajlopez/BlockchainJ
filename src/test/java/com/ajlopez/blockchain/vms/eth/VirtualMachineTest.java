package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.execution.CodeProvider;
import com.ajlopez.blockchain.execution.TopExecutionContext;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.CodeStore;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;
import java.util.List;
import java.util.Stack;

/**
 * Created by ajlopez on 10/12/2018.
 */
public class VirtualMachineTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void executeEmptyCode() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[0]);

        Assert.assertEquals(0, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        List<Log> logs = virtualMachine.getLogs();

        Assert.assertNotNull(logs);
        Assert.assertTrue(logs.isEmpty());
    }

    @Test
    public void executePushOneByte() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[]{OpCodes.PUSH1, 0x01});

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());

        DataWord result = stack.pop();

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ONE, result);
    }

    @Test
    public void executePushBytes() throws VirtualMachineException {
        for (int k = 0; k < 32; k++) {
            byte[] value = FactoryHelper.createRandomBytes(k + 1);
            byte[] opcodes = new byte[k + 2];

            opcodes[0] = (byte) (OpCodes.PUSH1 + k);
            System.arraycopy(value, 0, opcodes, 1, k + 1);

            VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

            virtualMachine.execute(opcodes);

            Assert.assertEquals(FeeSchedule.VERYLOW.getValue(), virtualMachine.getGasUsed());

            Stack<DataWord> stack = virtualMachine.getStack();

            Assert.assertNotNull(stack);
            Assert.assertFalse(stack.isEmpty());
            Assert.assertEquals(1, stack.size());

            DataWord result = stack.pop();

            Assert.assertNotNull(result);
            Assert.assertEquals(DataWord.fromBytes(value, 0, value.length), result);
        }
    }

    @Test
    public void executeProgramCounter() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[]{OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x02, OpCodes.PC });

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 2 + FeeSchedule.BASE.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(3, stack.size());

        Assert.assertEquals(DataWord.fromUnsignedInteger(4), stack.pop());
        Assert.assertEquals(DataWord.fromUnsignedInteger(2), stack.pop());
        Assert.assertEquals(DataWord.fromUnsignedInteger(1), stack.pop());
    }

    @Test
    public void executeAdd() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x02, OpCodes.ADD });

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 3, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());

        DataWord result = stack.pop();
        Assert.assertNotNull(result);
        Assert.assertEquals("0x03", result.toNormalizedString());
    }

    @Test
    public void executeAddOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.ADD, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 2, OpCodes.ADD, 3, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(40, 2, OpCodes.ADD, 42, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.ADD, 1024 * 1024 * 1024 + 1, FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeAddWithOverflow() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);
        byte[] bytecodes = HexUtils.hexStringToBytes("7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff600101");
        virtualMachine.execute(bytecodes);

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 3, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());

        DataWord result = stack.pop();

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ZERO, result);
    }

    @Test
    public void executeMulOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.MUL, 0, FeeSchedule.LOW.getValue());
        executeBinaryOp(1, 2, OpCodes.MUL, 2, FeeSchedule.LOW.getValue());
        executeBinaryOp(21, 2, OpCodes.MUL, 42, FeeSchedule.LOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.MUL, 1024 * 1024 * 1024, FeeSchedule.LOW.getValue());

        executeBinaryOp("0100000000", "0100000000", OpCodes.MUL, "010000000000000000", FeeSchedule.LOW.getValue());
        executeBinaryOp("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.MUL, "01", FeeSchedule.LOW.getValue());
    }

    @Test
    public void executeMulModOperations() throws VirtualMachineException {
        // TODO test add without 2^256 modulus
        executeTernaryOp(0, 0, 0, OpCodes.MULMOD, 0, FeeSchedule.MID.getValue());
        executeTernaryOp(1, 1, 1, OpCodes.MULMOD, 0, FeeSchedule.MID.getValue());
        executeTernaryOp(2, 1, 3, OpCodes.MULMOD, 1, FeeSchedule.MID.getValue());
        executeTernaryOp(2, 21, 2, OpCodes.MULMOD, 0, FeeSchedule.MID.getValue());
        executeTernaryOp(5, 42, 2, OpCodes.MULMOD, 4, FeeSchedule.MID.getValue());
        executeTernaryOp(1, 1, 1024 * 1024 * 1024, OpCodes.MULMOD, 0, FeeSchedule.MID.getValue());

        executeTernaryOp("0100000000", "00", "010000000000000000", OpCodes.MULMOD, "00");
        executeTernaryOp("01", "00", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.MULMOD, "00");
    }

    @Test
    public void executeDivOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.DIV, 0, FeeSchedule.LOW.getValue());
        executeBinaryOp(1, 2, OpCodes.DIV, 2, FeeSchedule.LOW.getValue());
        executeBinaryOp(2, 84, OpCodes.DIV, 42, FeeSchedule.LOW.getValue());
        executeBinaryOp(1, 1024 * 1024 * 1024, OpCodes.DIV, 1024 * 1024 * 1024, FeeSchedule.LOW.getValue());

        executeBinaryOp("0100000000", "010000000000000000", OpCodes.DIV, "0100000000", FeeSchedule.LOW.getValue());
        executeBinaryOp("01", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.DIV, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", FeeSchedule.LOW.getValue());
    }

    @Test
    public void executeSignedDivOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.SDIV, 0, FeeSchedule.LOW.getValue());
        executeBinaryOp(1, 2, OpCodes.SDIV, 2, FeeSchedule.LOW.getValue());
        executeBinaryOp(2, 84, OpCodes.SDIV, 42, FeeSchedule.LOW.getValue());
        executeBinaryOp(1, 1024 * 1024 * 1024, OpCodes.SDIV, 1024 * 1024 * 1024, FeeSchedule.LOW.getValue());

        executeBinaryOp(1, -2, OpCodes.SDIV, -2, FeeSchedule.LOW.getValue());
        executeBinaryOp(-1, -2, OpCodes.SDIV, 2, FeeSchedule.LOW.getValue());
        executeBinaryOp(-2, -4, OpCodes.SDIV, 2, FeeSchedule.LOW.getValue());
        executeBinaryOp(-2, 4, OpCodes.SDIV, -2, FeeSchedule.LOW.getValue());

        executeBinaryOp("0100000000", "010000000000000000", OpCodes.SDIV, "0100000000", FeeSchedule.LOW.getValue());
        executeBinaryOp(1, -1, OpCodes.SDIV, -1, FeeSchedule.LOW.getValue());

        executeBinaryOp("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "8000000000000000000000000000000000000000000000000000000000000000", OpCodes.SDIV, "8000000000000000000000000000000000000000000000000000000000000000", FeeSchedule.LOW.getValue());
    }

    @Test
    public void executeModOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.MOD, 0, FeeSchedule.LOW.getValue());
        executeBinaryOp(1, 2, OpCodes.MOD, 0, FeeSchedule.LOW.getValue());
        executeBinaryOp(2, 3, OpCodes.MOD, 1, FeeSchedule.LOW.getValue());
        executeBinaryOp(2, 84, OpCodes.MOD, 0, FeeSchedule.LOW.getValue());
        executeBinaryOp(5, 84, OpCodes.MOD, 4, FeeSchedule.LOW.getValue());
        executeBinaryOp(1, 1024 * 1024 * 1024, OpCodes.MOD, 0, FeeSchedule.LOW.getValue());

        executeBinaryOp("0100000000", "010000000000000000", OpCodes.MOD, "00", FeeSchedule.LOW.getValue());
        executeBinaryOp("01", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.MOD, "00", FeeSchedule.LOW.getValue());
    }

    @Test
    public void executeSignedModOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.SMOD, 0, FeeSchedule.LOW.getValue());
        executeBinaryOp(1, 2, OpCodes.SMOD, 0, FeeSchedule.LOW.getValue());
        executeBinaryOp(1, -2, OpCodes.SMOD, 0, FeeSchedule.LOW.getValue());
        executeBinaryOp(2, 3, OpCodes.SMOD, 1, FeeSchedule.LOW.getValue());
        executeBinaryOp(2, 84, OpCodes.SMOD, 0, FeeSchedule.LOW.getValue());
        executeBinaryOp(5, 84, OpCodes.SMOD, 4, FeeSchedule.LOW.getValue());
        executeBinaryOp(5, -84, OpCodes.SMOD, -4, FeeSchedule.LOW.getValue());
        executeBinaryOp(-5, 84, OpCodes.SMOD, 4, FeeSchedule.LOW.getValue());
        executeBinaryOp(1, 1024 * 1024 * 1024, OpCodes.SMOD, 0, FeeSchedule.LOW.getValue());

        executeBinaryOp("0100000000", "010000000000000000", OpCodes.SMOD, "00", FeeSchedule.LOW.getValue());
        executeBinaryOp("01", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.SMOD, "00", FeeSchedule.LOW.getValue());
    }

    @Test
    public void executeAddModOperations() throws VirtualMachineException {
        // TODO test mul without 2^256 modulus
        executeTernaryOp(0, 0, 0, OpCodes.ADDMOD, 0, FeeSchedule.MID.getValue());
        executeTernaryOp(1, 1, 1, OpCodes.ADDMOD, 0, FeeSchedule.MID.getValue());
        executeTernaryOp(2, 1, 2, OpCodes.ADDMOD, 1, FeeSchedule.MID.getValue());
        executeTernaryOp(2, 80, 4, OpCodes.ADDMOD, 0, FeeSchedule.MID.getValue());
        executeTernaryOp(5, 80, 4, OpCodes.ADDMOD, 4, FeeSchedule.MID.getValue());
        executeTernaryOp(1, 1, 1024 * 1024 * 1024, OpCodes.ADDMOD, 0, FeeSchedule.MID.getValue());

        executeTernaryOp("0100000000", "00", "010000000000000000", OpCodes.ADDMOD, "00");
        executeTernaryOp("01", "00", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.ADDMOD, "00");
    }

    @Test
    public void executeSub() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x02, OpCodes.SUB });

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 3, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());

        DataWord result = stack.pop();

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ONE, result);
    }

    @Test
    public void executeSubOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.SUB, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 3, OpCodes.SUB, 2, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(2, 44, OpCodes.SUB, 42, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 1024 * 1024 * 1024 + 1, OpCodes.SUB, 1024 * 1024 * 1024, FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeSubWithUnderflow() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);
        byte[] bytecodes = HexUtils.hexStringToBytes("6001600003");
        virtualMachine.execute(bytecodes);

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 3, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());

        DataWord result = stack.pop();

        Assert.assertNotNull(result);
        Assert.assertEquals("0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", result.toNormalizedString());
    }

    @Test
    public void executeDupTopOfStack() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.DUP1 });

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 2, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(2, stack.size());

        Assert.assertEquals(DataWord.ONE, stack.pop());
        Assert.assertEquals(DataWord.ONE, stack.pop());
    }

    @Test
    public void executeDups() throws VirtualMachineException {
        for (int k = 0; k < 16; k++) {
            VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

            byte[][] values = new byte[k + 1][];

            for (int j = 0; j < values.length; j++)
                values[j] = FactoryHelper.createRandomBytes(DataWord.DATAWORD_BYTES);

            byte[] bytecodes = new byte[(k + 1) * (DataWord.DATAWORD_BYTES + 1) + 1];

            for (int j = 0; j < values.length; j++) {
                int offset = j * (DataWord.DATAWORD_BYTES + 1);

                bytecodes[offset] = OpCodes.PUSH32;
                System.arraycopy(values[j], 0, bytecodes, offset + 1, values[j].length);
            }

            bytecodes[bytecodes.length - 1] = (byte)(OpCodes.DUP1 + k);

            virtualMachine.execute(bytecodes);

            Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * (values.length + 1), virtualMachine.getGasUsed());

            Stack<DataWord> stack = virtualMachine.getStack();

            Assert.assertNotNull(stack);
            Assert.assertFalse(stack.isEmpty());
            Assert.assertEquals(k + 2, stack.size());

            Assert.assertEquals(DataWord.fromBytes(values[0], 0, values[0].length), stack.pop());
        }
    }

    @Test
    public void executeSwaps() throws VirtualMachineException {
        for (int k = 0; k < 16; k++) {
            VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

            byte[][] values = new byte[k + 2][];

            for (int j = 0; j < values.length; j++)
                values[j] = FactoryHelper.createRandomBytes(DataWord.DATAWORD_BYTES);

            byte[] bytecodes = new byte[(k + 2) * (DataWord.DATAWORD_BYTES + 1) + 1];

            for (int j = 0; j < values.length; j++) {
                int offset = j * (DataWord.DATAWORD_BYTES + 1);

                bytecodes[offset] = OpCodes.PUSH32;
                System.arraycopy(values[j], 0, bytecodes, offset + 1, values[j].length);
            }

            bytecodes[bytecodes.length - 1] = (byte)(OpCodes.SWAP1 + k);

            virtualMachine.execute(bytecodes);

            Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * (values.length + 1), virtualMachine.getGasUsed());

            Stack<DataWord> stack = virtualMachine.getStack();

            Assert.assertNotNull(stack);
            Assert.assertFalse(stack.isEmpty());
            Assert.assertEquals(k + 2, stack.size());

            Assert.assertEquals(DataWord.fromBytes(values[0], 0, values[0].length), stack.peek());
            Assert.assertEquals(DataWord.fromBytes(values[values.length - 1], 0, values[values.length - 1].length), stack.get(0));
        }
    }

    @Test
    public void executePop() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x02, OpCodes.POP });

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 2 + FeeSchedule.BASE.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());

        DataWord result = stack.pop();

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ONE, result);
    }

    @Test
    public void executeStorageLoad() throws VirtualMachineException {
        Storage storage = new MapStorage();

        storage.setValue(DataWord.ONE, DataWord.fromUnsignedInteger(42));

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), storage);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.SLOAD });

        Assert.assertEquals(FeeSchedule.SLOAD.getValue() + FeeSchedule.VERYLOW.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(42), stack.pop());
    }

    @Test
    public void executeStorageStore() throws VirtualMachineException {
        Storage storage = new MapStorage();

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), storage);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.SSTORE });

        // TODO other SSTORE cases for gas cost
        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 2 + FeeSchedule.SSET.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), storage.getValue(DataWord.ONE));
    }

    @Test
    public void cannotExecuteStorageStoreIfMessageIsReadOnly() throws VirtualMachineException {
        Storage storage = new MapStorage();

        VirtualMachine virtualMachine = new VirtualMachine(createReadOnlyProgramEnvironment(), storage);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Read-only message");
        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.SSTORE });
    }

    @Test
    public void executeMemoryStore() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.MSTORE });

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 3, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), memory.getValue(1));
    }

    @Test
    public void executeMemoryStoreAndMemoryLoad() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.MSTORE, OpCodes.PUSH1, 0x01, OpCodes.MLOAD });

        Assert.assertEquals(5 * FeeSchedule.VERYLOW.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(42), stack.pop());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), memory.getValue(1));
    }

    @Test
    public void executeMemoryStoreAndMemorySize() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.MSTORE, OpCodes.MSIZE });

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.BASE.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(1 + DataWord.DATAWORD_BYTES), stack.pop());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), memory.getValue(1));
        Assert.assertEquals(1 + DataWord.DATAWORD_BYTES, memory.size());
    }

    @Test
    public void executeMemoryStoreByte() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.MSTORE8 });

        Assert.assertEquals(3 * FeeSchedule.VERYLOW.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals("0x2a000000000000000000000000000000000000000000000000000000000000", memory.getValue(0).toNormalizedString());
    }

    @Test
    public void executeLog0() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH6, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, OpCodes.PUSH1, 0x0, OpCodes.MSTORE, OpCodes.PUSH1, 0x02, OpCodes.PUSH1, 0x1c, OpCodes.LOG0 });

        // TODO check gas uses

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals(32, memory.size());

        List<Log> logs = virtualMachine.getLogs();

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
        Assert.assertEquals(1, logs.size());
        Assert.assertArrayEquals(memory.getBytes(28, 2), logs.get(0).getData());
        Assert.assertTrue(logs.get(0).getTopics().isEmpty());
    }

    @Test
    public void executeLog1() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH6, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, OpCodes.PUSH1, 0x0, OpCodes.MSTORE, OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x02, OpCodes.PUSH1, 0x1c, OpCodes.LOG1 });

        // TODO check gas used

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals(32, memory.size());

        List<Log> logs = virtualMachine.getLogs();

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
        Assert.assertEquals(1, logs.size());
        Assert.assertArrayEquals(memory.getBytes(28, 2), logs.get(0).getData());
        Assert.assertFalse(logs.get(0).getTopics().isEmpty());
        Assert.assertEquals(1, logs.get(0).getTopics().size());
        Assert.assertEquals(DataWord.fromSignedLong(42), logs.get(0).getTopics().get(0));
    }

    @Test
    public void executeLog2() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH6, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, OpCodes.PUSH1, 0x0, OpCodes.MSTORE, OpCodes.PUSH1, 0x03, OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x02, OpCodes.PUSH1, 0x1c, OpCodes.LOG2 });

        // TODO check gas used

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals(32, memory.size());

        List<Log> logs = virtualMachine.getLogs();

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
        Assert.assertEquals(1, logs.size());
        Assert.assertArrayEquals(memory.getBytes(28, 2), logs.get(0).getData());
        Assert.assertFalse(logs.get(0).getTopics().isEmpty());
        Assert.assertEquals(2, logs.get(0).getTopics().size());
        Assert.assertEquals(DataWord.fromSignedLong(42), logs.get(0).getTopics().get(0));
        Assert.assertEquals(DataWord.fromSignedLong(3), logs.get(0).getTopics().get(1));
    }

    @Test
    public void executeLog3() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH6, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, OpCodes.PUSH1, 0x0, OpCodes.MSTORE, OpCodes.PUSH1, 0x02, OpCodes.PUSH1, 0x03, OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x02, OpCodes.PUSH1, 0x1c, OpCodes.LOG3 });

        // TODO check gas used

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals(32, memory.size());

        List<Log> logs = virtualMachine.getLogs();

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
        Assert.assertEquals(1, logs.size());
        Assert.assertArrayEquals(memory.getBytes(28, 2), logs.get(0).getData());
        Assert.assertFalse(logs.get(0).getTopics().isEmpty());
        Assert.assertEquals(3, logs.get(0).getTopics().size());
        Assert.assertEquals(DataWord.fromSignedLong(42), logs.get(0).getTopics().get(0));
        Assert.assertEquals(DataWord.fromSignedLong(3), logs.get(0).getTopics().get(1));
        Assert.assertEquals(DataWord.TWO, logs.get(0).getTopics().get(2));
    }

    @Test
    public void executeLog4() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH6, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, OpCodes.PUSH1, 0x0, OpCodes.MSTORE, OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x02, OpCodes.PUSH1, 0x03, OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x02, OpCodes.PUSH1, 0x1c, OpCodes.LOG4 });

        // TODO check gas used

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals(32, memory.size());

        List<Log> logs = virtualMachine.getLogs();

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
        Assert.assertEquals(1, logs.size());
        Assert.assertArrayEquals(memory.getBytes(28, 2), logs.get(0).getData());
        Assert.assertFalse(logs.get(0).getTopics().isEmpty());
        Assert.assertEquals(4, logs.get(0).getTopics().size());
        Assert.assertEquals(DataWord.fromSignedLong(42), logs.get(0).getTopics().get(0));
        Assert.assertEquals(DataWord.fromSignedLong(3), logs.get(0).getTopics().get(1));
        Assert.assertEquals(DataWord.TWO, logs.get(0).getTopics().get(2));
        Assert.assertEquals(DataWord.ONE, logs.get(0).getTopics().get(3));
    }

    @Test
    public void executeLessThanOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.LT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 2, OpCodes.LT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(40, 2, OpCodes.LT, 1, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.LT, 1, FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeGreaterThanOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.GT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 2, OpCodes.GT, 1, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(40, 2, OpCodes.GT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.GT, 0, FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeSignedLessThanOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.SLT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 2, OpCodes.SLT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(-1, 2, OpCodes.SLT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(2, -1, OpCodes.SLT, 1, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(-1, -2, OpCodes.SLT, 1, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(40, 2, OpCodes.SLT, 1, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.SLT, 1, FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeSignedGreaterThanOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.SGT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 2, OpCodes.SGT, 1, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(-1, 2, OpCodes.SGT, 1, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(2, -1, OpCodes.SGT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(-1, -2, OpCodes.SGT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(40, 2, OpCodes.SGT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.SGT, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 1024 * 1024 * 1024, OpCodes.SGT, 1, FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeEqualsOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.EQ, 1, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 2, OpCodes.EQ, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(42, 42, OpCodes.EQ, 1, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.EQ, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.EQ, 1, FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeAndOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.AND, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 2, OpCodes.AND, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(3, 3, OpCodes.AND, 3, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(255, 42, OpCodes.AND, 42, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.AND, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.AND, 1024 * 1024 * 1024, FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeOrOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.OR, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 2, OpCodes.OR, 3, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(3, 3, OpCodes.OR, 3, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(255, 42, OpCodes.OR, 255, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.OR, 1024 * 1024 * 1024 + 1, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.OR, 1024 * 1024 * 1024, FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeXorOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.XOR, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1, 2, OpCodes.XOR, 3, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(3, 3, OpCodes.XOR, 0, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(255, 42, OpCodes.XOR, 255 ^ 42, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.XOR, 1024 * 1024 * 1024 + 1, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.XOR, 0, FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeByteOperations() throws VirtualMachineException {
        executeBinaryOp(0x20, 0x1f, OpCodes.BYTE, 0x20, FeeSchedule.VERYLOW.getValue());

        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "00", OpCodes.BYTE, "01", FeeSchedule.VERYLOW.getValue());
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "01", OpCodes.BYTE, "02", FeeSchedule.VERYLOW.getValue());
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "02", OpCodes.BYTE, "03", FeeSchedule.VERYLOW.getValue());
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "1f", OpCodes.BYTE, "20", FeeSchedule.VERYLOW.getValue());
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "2a", OpCodes.BYTE, "00", FeeSchedule.VERYLOW.getValue());
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1fff", "1f", OpCodes.BYTE, "ff", FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeShiftLeftOperations() throws VirtualMachineException {
        executeBinaryOp(0x20, 0x01, OpCodes.SHL, 0x40, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x2020, 0x01, OpCodes.SHL, 0x4040, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x2020, 0x08, OpCodes.SHL, 0x202000, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x20, 0x0100, OpCodes.SHL, 0x00, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x20, 0x00, OpCodes.SHL, 0x20, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x20, 0xffffffff, OpCodes.SHL, 0x00, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp("20", "ffffffffffffffffffffffffffffffff", OpCodes.SHL, "00", FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeShiftRightOperations() throws VirtualMachineException {
        executeBinaryOp(0x20, 0x05, OpCodes.SHR, 0x01, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x2020, 0x01, OpCodes.SHR, 0x1010, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x20200000, 0x08, OpCodes.SHR, 0x202000, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x20, 0x0100, OpCodes.SHR, 0x00, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x20, 0x00, OpCodes.SHR, 0x20, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x20, 0xffffffff, OpCodes.SHR, 0x00, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp("20", "ffffffffffffffffffffffffffffffff", OpCodes.SHR, "00", FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeShiftArithmeticRightOperations() throws VirtualMachineException {
        executeBinaryOp(0x20, 0x05, OpCodes.SAR, 0x01, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x2020, 0x01, OpCodes.SAR, 0x1010, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x20200000, 0x08, OpCodes.SAR, 0x202000, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x20, 0x0100, OpCodes.SAR, 0x00, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x20, 0x00, OpCodes.SAR, 0x20, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp(0x20, 0xffffffff, OpCodes.SAR, 0x00, FeeSchedule.VERYLOW.getValue());
        executeBinaryOp("20", "ffffffffffffffffffffffffffffffff", OpCodes.SAR, "00", FeeSchedule.VERYLOW.getValue());
        executeBinaryOp("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "09", OpCodes.SAR, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", FeeSchedule.VERYLOW.getValue());
        executeBinaryOp("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "1000", OpCodes.SAR, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", FeeSchedule.VERYLOW.getValue());
    }

    @Test
    public void executeExpOperations() throws VirtualMachineException {
        // TODO calculate and check gas
        executeBinaryOp(0x05, 0x10, OpCodes.EXP, 0x100000, 0);
        executeBinaryOp(0x01, 0x2020, OpCodes.EXP, 0x2020, 0);
        executeBinaryOp(0x02, 0x1000, OpCodes.EXP, 0x1000000, 0);
        executeBinaryOp(0x20, 0x0100, OpCodes.EXP, 0x00, 0);
        executeBinaryOp(0x00, 0x20, OpCodes.EXP, 0x01, 0);
        executeBinaryOp(0xffffffff, 0x20, OpCodes.EXP, 0x00, 0);
        executeBinaryOp("09", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.EXP, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 0);
        executeBinaryOp("1001", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.EXP, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 0);
    }

    @Test
    public void executeIsZeroOperations() throws VirtualMachineException {
        executeUnaryOp(OpCodes.ISZERO, 1, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeUnaryOp(OpCodes.ISZERO, 0, 42, FeeSchedule.VERYLOW.getValue() * 2);
        executeUnaryOp(OpCodes.ISZERO, 0, 1024 * 1024 * 1024, FeeSchedule.VERYLOW.getValue() * 2);
    }

    @Test
    public void executeNotOperations() throws VirtualMachineException {
        executeUnaryOp("00", OpCodes.NOT, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", FeeSchedule.VERYLOW.getValue() * 2);
        executeUnaryOp("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.NOT, "00", FeeSchedule.VERYLOW.getValue() * 2);
        executeUnaryOp("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff00", OpCodes.NOT, "ff", FeeSchedule.VERYLOW.getValue() * 2);
    }

    @Test
    public void executeGasPriceOperations() throws VirtualMachineException {
        DataWord gasPrice = DataWord.fromUnsignedInteger(42);
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 100000, gasPrice, null, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(new byte[] { OpCodes.GASPRICE });

        Assert.assertEquals(FeeSchedule.BASE.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(gasPrice, stack.pop());
    }

    @Test
    public void executeAddressOriginCallerCallValueOperations() throws VirtualMachineException {
        Address address = FactoryHelper.createRandomAddress();
        Address origin = FactoryHelper.createRandomAddress();
        Address caller = FactoryHelper.createRandomAddress();

        MessageData messageData = new MessageData(address, origin, caller, DataWord.ONE, 100000, null, null, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(new byte[] { OpCodes.ADDRESS, OpCodes.ORIGIN, OpCodes.CALLER, OpCodes.CALLVALUE });

        Assert.assertEquals(FeeSchedule.BASE.getValue() * 4, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(4, stack.size());
        Assert.assertEquals(DataWord.ONE, stack.pop());
        Assert.assertEquals(DataWord.fromAddress(caller), stack.pop());
        Assert.assertEquals(DataWord.fromAddress(origin), stack.pop());
        Assert.assertEquals(DataWord.fromAddress(address), stack.pop());
    }

    @Test
    public void executeCallDataSize() throws VirtualMachineException {
        byte[] data = FactoryHelper.createRandomBytes(42);
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 100000, null, data, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(new byte[] { OpCodes.CALLDATASIZE });

        Assert.assertEquals(FeeSchedule.BASE.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(42), stack.pop());
    }

    @Test
    public void executeCallDataLoad() throws VirtualMachineException {
        byte[] data = FactoryHelper.createRandomBytes(42);
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 100000, null, data, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x02, OpCodes.CALLDATALOAD });

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 2, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromBytes(data, 2, 32), stack.pop());
    }

    @Test
    public void executeCallDataLoadWithAdditionalBytes() throws VirtualMachineException {
        byte[] data = FactoryHelper.createRandomBytes(42);
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 100000, null, data, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x20, OpCodes.CALLDATALOAD });

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 2, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());

        byte[] expected = new byte[DataWord.DATAWORD_BYTES];
        System.arraycopy(data, 32, expected, 0, 10);
        Assert.assertEquals(DataWord.fromBytes(expected, 0, expected.length), stack.pop());
    }

    @Test
    public void executeCallDataLoadBeyondData() throws VirtualMachineException {
        byte[] data = FactoryHelper.createRandomBytes(42);
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 100000, null, data, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x70, OpCodes.CALLDATALOAD });

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 2, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.ZERO, stack.pop());
    }

    @Test
    public void executeCallDataCopy() throws VirtualMachineException {
        byte[] data = FactoryHelper.createRandomBytes(42);
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 100000, null, data, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(new byte[] {
                OpCodes.PUSH1, 0x10,
                OpCodes.PUSH1, 0x02,
                OpCodes.PUSH1, 0x04,
                OpCodes.CALLDATACOPY });

        // TODO check gas uses

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertNotNull(memory);
        Assert.assertEquals(20, memory.size());

        byte[] expected = new byte[16];
        System.arraycopy(data, 2, expected, 0, 16);

        Assert.assertArrayEquals(expected, memory.getBytes(4, 16));
    }

    @Test
    public void executeCallDataCopyWithPartialData() throws VirtualMachineException {
        byte[] data = FactoryHelper.createRandomBytes(42);
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 100000, null, data, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(new byte[] {
                OpCodes.PUSH1, 0x10,
                OpCodes.PUSH1, 0x20,
                OpCodes.PUSH1, 0x04,
                OpCodes.CALLDATACOPY });

        // TODO check gas uses

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertNotNull(memory);
        Assert.assertEquals(20, memory.size());

        byte[] expected = new byte[16];
        System.arraycopy(data, 32, expected, 0, 10);

        Assert.assertArrayEquals(expected, memory.getBytes(4, 16));
    }

    @Test
    public void executeCallDataCopyBeyondData() throws VirtualMachineException {
        byte[] data = FactoryHelper.createRandomBytes(42);
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 100000, null, data, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(new byte[] {
                OpCodes.PUSH1, 0x10,
                OpCodes.PUSH1, 0x70,
                OpCodes.PUSH1, 0x04,
                OpCodes.CALLDATACOPY });

        // TODO check gas uses

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertNotNull(memory);
        Assert.assertEquals(20, memory.size());

        byte[] expected = new byte[16];

        Assert.assertArrayEquals(expected, memory.getBytes(4, 16));
    }

    @Test
    public void executeCodeCopy() throws VirtualMachineException {
        byte[] code = FactoryHelper.createRandomBytes(42);
        code[0] = OpCodes.PUSH1;
        code[1] = 0x10;
        code[2] = OpCodes.PUSH1;
        code[3] = 0x08;
        code[4] = OpCodes.PUSH1;
        code[5] = 0x04;
        code[6] = OpCodes.CODECOPY;
        code[7] = OpCodes.STOP;

        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 100000, null, null, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(code);

        // TODO check gas uses

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertNotNull(memory);
        Assert.assertEquals(20, memory.size());

        byte[] expected = new byte[16];
        System.arraycopy(code, 8, expected, 0, 16);

        Assert.assertArrayEquals(expected, memory.getBytes(4, 16));
    }

    @Test
    public void executeCodeCopyWithPartialCode() throws VirtualMachineException {
        byte[] code = FactoryHelper.createRandomBytes(42);
        code[0] = OpCodes.PUSH1;
        code[1] = 0x10;
        code[2] = OpCodes.PUSH1;
        code[3] = 0x20;
        code[4] = OpCodes.PUSH1;
        code[5] = 0x04;
        code[6] = OpCodes.CODECOPY;
        code[7] = OpCodes.STOP;

        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 100000, null, null, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(code);

        // TODO check gas uses

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertNotNull(memory);
        Assert.assertEquals(20, memory.size());

        byte[] expected = new byte[16];
        System.arraycopy(code, 32, expected, 0, 10);

        Assert.assertArrayEquals(expected, memory.getBytes(4, 16));
    }

    @Test
    public void executeCodeCopyBeyondCode() throws VirtualMachineException {
        byte[] code = FactoryHelper.createRandomBytes(42);
        code[0] = OpCodes.PUSH1;
        code[1] = 0x10;
        code[2] = OpCodes.PUSH1;
        code[3] = 0x70;
        code[4] = OpCodes.PUSH1;
        code[5] = 0x04;
        code[6] = OpCodes.CODECOPY;
        code[7] = OpCodes.STOP;

        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 100000, null, null, false);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null, null);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(code);

        // TODO check gas uses

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertNotNull(memory);
        Assert.assertEquals(20, memory.size());

        byte[] expected = new byte[16];

        Assert.assertArrayEquals(expected, memory.getBytes(4, 16));
    }

    @Test
    public void executeCoinbaseTimestampNumberDifficultyOperations() throws VirtualMachineException {
        long number = 1;
        long timestamp = 2;
        Address coinbase = FactoryHelper.createRandomAddress();
        DataWord difficulty = DataWord.ONE;

        BlockData blockData = new BlockData(number, timestamp, coinbase, difficulty);

        ProgramEnvironment programEnvironment = createProgramEnvironment(blockData);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(new byte[] { OpCodes.COINBASE, OpCodes.TIMESTAMP, OpCodes.NUMBER, OpCodes.DIFFICULTY });

        Assert.assertEquals(FeeSchedule.BASE.getValue() * 4, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(4, stack.size());
        Assert.assertEquals(difficulty, stack.pop());
        Assert.assertEquals(DataWord.fromUnsignedLong(number), stack.pop());
        Assert.assertEquals(DataWord.fromUnsignedLong(timestamp), stack.pop());
        Assert.assertEquals(DataWord.fromAddress(coinbase), stack.pop());
    }

    @Test
    public void executeCodeSizeOperation() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.CODESIZE, OpCodes.STOP });

        Assert.assertEquals(FeeSchedule.BASE.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.TWO, stack.pop());
    }

    @Test
    public void executeExtCodeSizeOperationForUnknownAccount() throws VirtualMachineException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        AccountStore accountStore = new AccountStore(new Trie());
        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(executionContext), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x28, OpCodes.EXTCODESIZE, OpCodes.STOP });

        // TODO Check gas cost

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.ZERO, stack.pop());
    }

    @Test
    public void executeExtCodeSizeOperationForAccountWithCode() throws VirtualMachineException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        Hash codeHash = FactoryHelper.createRandomHash();
        byte[] code = FactoryHelper.createRandomBytes(100);
        codeStore.putCode(codeHash, code);
        Account account = new Account(BigInteger.ZERO, 0, codeHash, null);
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();
        accountStore.putAccount(address, account);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(executionContext), null);

        byte bytecode[] = new byte[1 + 20 + 2];
        bytecode[0] = OpCodes.PUSH20;
        System.arraycopy(address.getBytes(), 0, bytecode, 1, Address.ADDRESS_BYTES);
        bytecode[21] = OpCodes.EXTCODESIZE;
        bytecode[22] = OpCodes.STOP;

        virtualMachine.execute(bytecode);

        // TODO Check gas cost

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(100), stack.pop());
    }

    @Test
    public void executeExtCodeCopyOperationForUnknownAccount() throws VirtualMachineException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        AccountStore accountStore = new AccountStore(new Trie());
        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(executionContext), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x20, OpCodes.PUSH1, 0x02, OpCodes.PUSH1, 0x10, OpCodes.PUSH1, 0x28, OpCodes.EXTCODECOPY, OpCodes.STOP });

        // TODO Check gas cost

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.empty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertNotNull(memory);
        Assert.assertEquals(34, memory.size());

        byte[] expected = new byte[34];
        Assert.assertArrayEquals(expected, memory.getBytes(0, 34));
    }


    @Test
    public void executeExtCodeCopyOperationForAccountWithCode() throws VirtualMachineException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        Hash codeHash = FactoryHelper.createRandomHash();
        byte[] code = FactoryHelper.createRandomBytes(100);
        codeStore.putCode(codeHash, code);
        Account account = new Account(BigInteger.ZERO, 0, codeHash, null);
        AccountStore accountStore = new AccountStore(new Trie());
        Address address = FactoryHelper.createRandomAddress();
        accountStore.putAccount(address, account);

        TopExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(executionContext), null);

        byte bytecode[] = new byte[6 + 1 + 20 + 2];
        bytecode[0] = OpCodes.PUSH1;
        bytecode[1] = 0x20;
        bytecode[2] = OpCodes.PUSH1;
        bytecode[3] = 0x02;
        bytecode[4] = OpCodes.PUSH1;
        bytecode[5] = 0x10;
        bytecode[6] = OpCodes.PUSH20;
        System.arraycopy(address.getBytes(), 0, bytecode, 7, Address.ADDRESS_BYTES);
        bytecode[27] = OpCodes.EXTCODESIZE;
        bytecode[28] = OpCodes.STOP;

        virtualMachine.execute(bytecode);

        // TODO Check gas cost

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.empty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertNotNull(memory);
        Assert.assertEquals(34, memory.size());

        byte[] expected = new byte[34];
        Assert.assertArrayEquals(expected, memory.getBytes(0, 34));
    }

    @Test
    public void executeStopOperation() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[] { OpCodes.STOP, OpCodes.PUSH1, 0x01 });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());
        Assert.assertEquals(0, virtualMachine.getGasUsed());
    }

    @Test
    public void executeGasOperation() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.GAS });

        Assert.assertEquals(FeeSchedule.BASE.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(100000 - FeeSchedule.BASE.getValue(), stack.pop().asUnsignedInteger());
    }

    @Test
    public void executeJumpOperation() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x04, OpCodes.JUMP, OpCodes.STOP, OpCodes.JUMPDEST, OpCodes.PUSH1, 0x2a });

        Assert.assertEquals(FeeSchedule.MID.getValue() + 2 * FeeSchedule.VERYLOW.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(42), stack.pop());
    }

    @Test
    public void executeJumpWithoutJumpDestRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x04, OpCodes.JUMP, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeJumpWithNot32BitsIntegerTargetRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH5, 0x01, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, OpCodes.JUMP, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeJumpWithTwoLarge32BitsIntegerTargetRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH1, (byte)0xff, OpCodes.JUMP, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeConditionalJumpOperation() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x06, OpCodes.JUMPI, OpCodes.STOP, OpCodes.JUMPDEST, OpCodes.PUSH1, 0x2a });

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.HIGH.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(42), stack.pop());
    }

    @Test
    public void executeConditionalJumpWithoutJumpDestRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x06, OpCodes.JUMPI, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeConditionalJumpWithNot32BitsIntegerTargetRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH5, 0x01, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, OpCodes.JUMPI, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeConditionalJumpWithTwoLarge32BitsIntegerTargetRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 1, OpCodes.PUSH1, (byte)0xff, OpCodes.JUMPI, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeInvalidOpCodeRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid opcode");
        virtualMachine.execute(new byte[] { (byte)0xfe });
    }

    @Test
    public void executeWithInsufficientGasRaiseException() throws VirtualMachineException {
        MessageData messageData = new MessageData(null, null, null, DataWord.ZERO, 0, DataWord.ZERO, null, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, null), null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Insufficient gas");
        virtualMachine.execute(new byte[] { OpCodes.ADDRESS });
    }

    private static void executeUnaryOp(byte opcode, int expected, int operand, int expectedGasUsed) throws VirtualMachineException {
        byte[] boperand = ByteUtils.normalizedBytes(ByteUtils.unsignedIntegerToBytes(operand));

        byte[] bytecodes = new byte[2 + boperand.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand.length - 1);
        System.arraycopy(boperand, 0, bytecodes, 1, boperand.length);
        bytecodes[boperand.length + 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(bytecodes);

        Assert.assertEquals(expectedGasUsed, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(expected), stack.pop());
    }

    private static void executeUnaryOp(String operand, byte opcode, String expected, int expectedGasUsed) throws VirtualMachineException {
        byte[] boperand = DataWord.fromHexadecimalString(operand).toNormalizedBytes();

        byte[] bytecodes = new byte[2 + boperand.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand.length - 1);
        System.arraycopy(boperand, 0, bytecodes, 1, boperand.length);
        bytecodes[boperand.length + 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(bytecodes);

        Assert.assertEquals(expectedGasUsed, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromHexadecimalString(expected), stack.pop());
    }

    private static void executeBinaryOp(int operand1, int operand2, byte opcode, int expected, int expectedGasUsed) throws VirtualMachineException {
        byte[] boperand1 = DataWord.fromSignedLong(operand1).toNormalizedBytes();
        byte[] boperand2 = DataWord.fromSignedLong(operand2).toNormalizedBytes();

        byte[] bytecodes = new byte[3 + boperand1.length + boperand2.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand1.length - 1);
        System.arraycopy(boperand1, 0, bytecodes, 1, boperand1.length);
        bytecodes[boperand1.length + 1] = (byte)(OpCodes.PUSH1 + boperand2.length - 1);
        System.arraycopy(boperand2, 0, bytecodes, 2 + boperand1.length, boperand2.length);
        bytecodes[bytecodes.length - 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(bytecodes);

        Assert.assertEquals(expectedGasUsed + 2 * FeeSchedule.VERYLOW.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromSignedLong(expected), stack.pop());
    }

    private static void executeBinaryOp(String operand1, String operand2, byte opcode, String expected, int expectedGasUsed) throws VirtualMachineException {
        byte[] boperand1 = DataWord.fromHexadecimalString(operand1).toNormalizedBytes();
        byte[] boperand2 = DataWord.fromHexadecimalString(operand2).toNormalizedBytes();

        byte[] bytecodes = new byte[3 + boperand1.length + boperand2.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand1.length - 1);
        System.arraycopy(boperand1, 0, bytecodes, 1, boperand1.length);
        bytecodes[boperand1.length + 1] = (byte)(OpCodes.PUSH1 + boperand2.length - 1);
        System.arraycopy(boperand2, 0, bytecodes, 2 + boperand1.length, boperand2.length);
        bytecodes[bytecodes.length - 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(bytecodes);

        Assert.assertEquals(expectedGasUsed + 2 * FeeSchedule.VERYLOW.getValue(), virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromHexadecimalString(expected), stack.pop());
    }

    private static void executeTernaryOp(int operand1, int operand2, int operand3, byte opcode, int expected, int expectedGasUsed) throws VirtualMachineException {
        byte[] boperand1 = DataWord.fromSignedLong(operand1).toNormalizedBytes();
        byte[] boperand2 = DataWord.fromSignedLong(operand2).toNormalizedBytes();
        byte[] boperand3 = DataWord.fromSignedLong(operand3).toNormalizedBytes();

        byte[] bytecodes = new byte[4 + boperand1.length + boperand2.length + boperand3.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand1.length - 1);
        System.arraycopy(boperand1, 0, bytecodes, 1, boperand1.length);
        bytecodes[boperand1.length + 1] = (byte)(OpCodes.PUSH1 + boperand2.length - 1);
        System.arraycopy(boperand2, 0, bytecodes, 2 + boperand1.length, boperand2.length);
        bytecodes[boperand1.length + 1 + boperand2.length + 1] = (byte)(OpCodes.PUSH1 + boperand3.length - 1);
        System.arraycopy(boperand3, 0, bytecodes, 3 + boperand1.length + boperand2.length, boperand3.length);
        bytecodes[bytecodes.length - 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(bytecodes);

        Assert.assertEquals(expectedGasUsed + FeeSchedule.VERYLOW.getValue() * 3, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromSignedLong(expected), stack.pop());
    }

    private static void executeTernaryOp(String operand1, String operand2, String operand3, byte opcode, String expected) throws VirtualMachineException {
        byte[] boperand1 = DataWord.fromHexadecimalString(operand1).toNormalizedBytes();
        byte[] boperand2 = DataWord.fromHexadecimalString(operand2).toNormalizedBytes();
        byte[] boperand3 = DataWord.fromHexadecimalString(operand3).toNormalizedBytes();

        byte[] bytecodes = new byte[4 + boperand1.length + boperand2.length + boperand3.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand1.length - 1);
        System.arraycopy(boperand1, 0, bytecodes, 1, boperand1.length);
        bytecodes[boperand1.length + 1] = (byte)(OpCodes.PUSH1 + boperand2.length - 1);
        System.arraycopy(boperand2, 0, bytecodes, 2 + boperand1.length, boperand2.length);
        bytecodes[boperand1.length + 1 + boperand2.length + 1] = (byte)(OpCodes.PUSH1 + boperand3.length - 1);
        System.arraycopy(boperand3, 0, bytecodes, 3 + boperand1.length + boperand2.length, boperand3.length);
        bytecodes[bytecodes.length - 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(createProgramEnvironment(), null);

        virtualMachine.execute(bytecodes);

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromHexadecimalString(expected), stack.pop());
    }

    private static ProgramEnvironment createProgramEnvironment() {
        return createProgramEnvironment((CodeProvider)null);
    }

    private static ProgramEnvironment createProgramEnvironment(CodeProvider codeProvider) {
        MessageData messageData = new MessageData(FactoryHelper.createRandomAddress(), null, null, DataWord.ZERO, 100000, DataWord.ZERO, null, false);

        return new ProgramEnvironment(messageData, null, codeProvider);
    }

    private static ProgramEnvironment createProgramEnvironment(BlockData blockData) {
        MessageData messageData = new MessageData(null, null, null, DataWord.ZERO, 100000, DataWord.ZERO, null, false);

        return new ProgramEnvironment(messageData, blockData, null);
    }

    private static ProgramEnvironment createReadOnlyProgramEnvironment() {
        MessageData messageData = new MessageData(null, null, null, DataWord.ZERO, 100000, DataWord.ZERO, null, true);

        return new ProgramEnvironment(messageData, null, null);
    }
}
