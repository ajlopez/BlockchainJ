package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[0]);

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());
    }

    @Test
    public void executePushOneByte() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[]{OpCodes.PUSH1, 0x01});

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());

        DataWord result = stack.pop();

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ONE, result);

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue(), virtualMachine.getGasUsed());
    }

    @Test
    public void executePushBytes() throws VirtualMachineException {
        for (int k = 0; k < 32; k++) {
            byte[] value = FactoryHelper.createRandomBytes(k + 1);
            byte[] opcodes = new byte[k + 2];

            opcodes[0] = (byte) (OpCodes.PUSH1 + k);
            System.arraycopy(value, 0, opcodes, 1, k + 1);

            VirtualMachine virtualMachine = new VirtualMachine(null, null);

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
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

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
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

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
        executeBinaryOp(0, 0, OpCodes.ADD, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1, 2, OpCodes.ADD, 3, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(40, 2, OpCodes.ADD, 42, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.ADD, 1024 * 1024 * 1024 + 1, FeeSchedule.VERYLOW.getValue() * 3);
    }

    @Test
    public void executeAddWithOverflow() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);
        byte[] bytecodes = HexUtils.hexStringToBytes("7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff600101");
        virtualMachine.execute(bytecodes);

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
        executeBinaryOp(0, 0, OpCodes.MUL, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 2, OpCodes.MUL, 2, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(21, 2, OpCodes.MUL, 42, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.MUL, 1024 * 1024 * 1024, FeeSchedule.VERYLOW.getValue() * 2);

        executeBinaryOp("0100000000", "0100000000", OpCodes.MUL, "010000000000000000");
        executeBinaryOp("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.MUL, "01");
    }

    @Test
    public void executeMulModOperations() throws VirtualMachineException {
        // TODO test add without 2^256 modulus
        executeTernaryOp(0, 0, 0, OpCodes.MULMOD, 0, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());
        executeTernaryOp(1, 1, 1, OpCodes.MULMOD, 0, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());
        executeTernaryOp(2, 1, 3, OpCodes.MULMOD, 1, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());
        executeTernaryOp(2, 21, 2, OpCodes.MULMOD, 0, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());
        executeTernaryOp(5, 42, 2, OpCodes.MULMOD, 4, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());
        executeTernaryOp(1, 1, 1024 * 1024 * 1024, OpCodes.MULMOD, 0, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());

        executeTernaryOp("0100000000", "00", "010000000000000000", OpCodes.MULMOD, "00");
        executeTernaryOp("01", "00", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.MULMOD, "00");
    }

    @Test
    public void executeDivOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.DIV, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 2, OpCodes.DIV, 2, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(2, 84, OpCodes.DIV, 42, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 1024 * 1024 * 1024, OpCodes.DIV, 1024 * 1024 * 1024, FeeSchedule.VERYLOW.getValue() * 2);

        executeBinaryOp("0100000000", "010000000000000000", OpCodes.DIV, "0100000000");
        executeBinaryOp("01", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.DIV, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
    }

    @Test
    public void executeSignedDivOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.SDIV, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 2, OpCodes.SDIV, 2, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(2, 84, OpCodes.SDIV, 42, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 1024 * 1024 * 1024, OpCodes.SDIV, 1024 * 1024 * 1024, FeeSchedule.VERYLOW.getValue() * 2);

        executeBinaryOp(1, -2, OpCodes.SDIV, -2, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(-1, -2, OpCodes.SDIV, 2, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(-2, -4, OpCodes.SDIV, 2, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(-2, 4, OpCodes.SDIV, -2, FeeSchedule.VERYLOW.getValue() * 2);

        executeBinaryOp("0100000000", "010000000000000000", OpCodes.SDIV, "0100000000");
        executeBinaryOp(1, -1, OpCodes.SDIV, -1, FeeSchedule.VERYLOW.getValue() * 2);

        executeBinaryOp("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "8000000000000000000000000000000000000000000000000000000000000000", OpCodes.SDIV, "8000000000000000000000000000000000000000000000000000000000000000");
    }

    @Test
    public void executeModOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.MOD, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 2, OpCodes.MOD, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(2, 3, OpCodes.MOD, 1, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(2, 84, OpCodes.MOD, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(5, 84, OpCodes.MOD, 4, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 1024 * 1024 * 1024, OpCodes.MOD, 0, FeeSchedule.VERYLOW.getValue() * 2);

        executeBinaryOp("0100000000", "010000000000000000", OpCodes.MOD, "00");
        executeBinaryOp("01", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.MOD, "00");
    }

    @Test
    public void executeSignedModOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.SMOD, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 2, OpCodes.SMOD, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, -2, OpCodes.SMOD, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(2, 3, OpCodes.SMOD, 1, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(2, 84, OpCodes.SMOD, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(5, 84, OpCodes.SMOD, 4, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(5, -84, OpCodes.SMOD, -4, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(-5, 84, OpCodes.SMOD, 4, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 1024 * 1024 * 1024, OpCodes.SMOD, 0, FeeSchedule.VERYLOW.getValue() * 2);

        executeBinaryOp("0100000000", "010000000000000000", OpCodes.SMOD, "00");
        executeBinaryOp("01", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.SMOD, "00");
    }

    @Test
    public void executeAddModOperations() throws VirtualMachineException {
        // TODO test mul without 2^256 modulus
        executeTernaryOp(0, 0, 0, OpCodes.ADDMOD, 0, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());
        executeTernaryOp(1, 1, 1, OpCodes.ADDMOD, 0, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());
        executeTernaryOp(2, 1, 2, OpCodes.ADDMOD, 1, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());
        executeTernaryOp(2, 80, 4, OpCodes.ADDMOD, 0, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());
        executeTernaryOp(5, 80, 4, OpCodes.ADDMOD, 4, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());
        executeTernaryOp(1, 1, 1024 * 1024 * 1024, OpCodes.ADDMOD, 0, FeeSchedule.VERYLOW.getValue() * 3 + FeeSchedule.MID.getValue());

        executeTernaryOp("0100000000", "00", "010000000000000000", OpCodes.ADDMOD, "00");
        executeTernaryOp("01", "00", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.ADDMOD, "00");
    }

    @Test
    public void executeSub() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x02, OpCodes.SUB });

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
        executeBinaryOp(0, 0, OpCodes.SUB, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1, 3, OpCodes.SUB, 2, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(2, 44, OpCodes.SUB, 42, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1, 1024 * 1024 * 1024 + 1, OpCodes.SUB, 1024 * 1024 * 1024, FeeSchedule.VERYLOW.getValue() * 3);
    }

    @Test
    public void executeSubWithUnderflow() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);
        byte[] bytecodes = HexUtils.hexStringToBytes("6001600003");
        virtualMachine.execute(bytecodes);

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
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

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
            VirtualMachine virtualMachine = new VirtualMachine(null, null);

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
            VirtualMachine virtualMachine = new VirtualMachine(null, null);

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
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x02, OpCodes.POP });

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

        VirtualMachine virtualMachine = new VirtualMachine(null, storage);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.SLOAD });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(42), stack.pop());
    }

    @Test
    public void executeStorageStorage() throws VirtualMachineException {
        Storage storage = new MapStorage();

        VirtualMachine virtualMachine = new VirtualMachine(null, storage);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.SSTORE });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), storage.getValue(DataWord.ONE));
    }

    @Test
    public void executeMemoryStore() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.MSTORE });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), memory.getValue(1));
    }

    @Test
    public void executeMemoryStoreAndMemoryLoad() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.MSTORE, OpCodes.PUSH1, 0x01, OpCodes.MLOAD });

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
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.MSTORE, OpCodes.MSIZE });

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
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.MSTORE8 });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals("0x2a000000000000000000000000000000000000000000000000000000000000", memory.getValue(0).toNormalizedString());
    }

    @Test
    public void executeLessThanOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.LT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1, 2, OpCodes.LT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(40, 2, OpCodes.LT, 1, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.LT, 1, FeeSchedule.VERYLOW.getValue() * 3);
    }

    @Test
    public void executeGreaterThanOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.GT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1, 2, OpCodes.GT, 1, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(40, 2, OpCodes.GT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.GT, 0, FeeSchedule.VERYLOW.getValue() * 3);
    }

    @Test
    public void executeSignedLessThanOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.SLT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1, 2, OpCodes.SLT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(-1, 2, OpCodes.SLT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(2, -1, OpCodes.SLT, 1, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(-1, -2, OpCodes.SLT, 1, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(40, 2, OpCodes.SLT, 1, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.SLT, 1, FeeSchedule.VERYLOW.getValue() * 3);
    }

    @Test
    public void executeSignedGreaterThanOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.SGT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1, 2, OpCodes.SGT, 1, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(-1, 2, OpCodes.SGT, 1, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(2, -1, OpCodes.SGT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(-1, -2, OpCodes.SGT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(40, 2, OpCodes.SGT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.SGT, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1, 1024 * 1024 * 1024, OpCodes.SGT, 1, FeeSchedule.VERYLOW.getValue() * 3);
    }

    @Test
    public void executeEqualsOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.EQ, 1, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1, 2, OpCodes.EQ, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(42, 42, OpCodes.EQ, 1, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.EQ, 0, FeeSchedule.VERYLOW.getValue() * 3);
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.EQ, 1, FeeSchedule.VERYLOW.getValue() * 3);
    }

    @Test
    public void executeAndOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.AND, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 2, OpCodes.AND, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(3, 3, OpCodes.AND, 3, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(255, 42, OpCodes.AND, 42, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.AND, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.AND, 1024 * 1024 * 1024, FeeSchedule.VERYLOW.getValue() * 2);
    }

    @Test
    public void executeOrOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.OR, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 2, OpCodes.OR, 3, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(3, 3, OpCodes.OR, 3, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(255, 42, OpCodes.OR, 255, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.OR, 1024 * 1024 * 1024 + 1, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.OR, 1024 * 1024 * 1024, FeeSchedule.VERYLOW.getValue() * 2);
    }

    @Test
    public void executeXorOperations() throws VirtualMachineException {
        executeBinaryOp(0, 0, OpCodes.XOR, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1, 2, OpCodes.XOR, 3, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(3, 3, OpCodes.XOR, 0, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(255, 42, OpCodes.XOR, 255 ^ 42, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.XOR, 1024 * 1024 * 1024 + 1, FeeSchedule.VERYLOW.getValue() * 2);
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.XOR, 0, FeeSchedule.VERYLOW.getValue() * 2);
    }

    @Test
    public void executeByteOperations() throws VirtualMachineException {
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "00", OpCodes.BYTE, "01");
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "01", OpCodes.BYTE, "02");
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "02", OpCodes.BYTE, "03");
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "1f", OpCodes.BYTE, "20");
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "2a", OpCodes.BYTE, "00");
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1fff", "1f", OpCodes.BYTE, "ff");
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
    public void executeAddressOriginCallerCallValueOperations() throws VirtualMachineException {
        Address address = FactoryHelper.createRandomAddress();
        Address origin = FactoryHelper.createRandomAddress();
        Address caller = FactoryHelper.createRandomAddress();

        MessageData messageData = new MessageData(address, origin, caller, DataWord.ONE, 0, null);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null);

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
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 0, data);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null);

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
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 0, data);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null);

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
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 0, data);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null);

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
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 0, data);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null);

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
        MessageData messageData = new MessageData(null, null, null, DataWord.ONE, 0, data);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(messageData, null);

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

        // TODO check memory content
        byte[] expected = new byte[16];
        System.arraycopy(data, 2, expected, 0, 16);

        Assert.assertArrayEquals(expected, memory.getBytes(4, 16));
    }

    @Test
    public void executeCoinbaseTimestampNumberDifficultyOperations() throws VirtualMachineException {
        long number = 1;
        long timestamp = 2;
        Address coinbase = FactoryHelper.createRandomAddress();
        DataWord difficulty = DataWord.ONE;

        BlockData blockData = new BlockData(number, timestamp, coinbase, difficulty);

        ProgramEnvironment programEnvironment = new ProgramEnvironment(null, blockData);

        VirtualMachine virtualMachine = new VirtualMachine(programEnvironment, null);

        virtualMachine.execute(new byte[] { OpCodes.COINBASE, OpCodes.TIMESTAMP, OpCodes.NUMBER, OpCodes.DIFFICULTY });

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
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[] { OpCodes.CODESIZE, OpCodes.STOP });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.TWO, stack.pop());
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
    public void executeJumpOperation() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x04, OpCodes.JUMP, OpCodes.STOP, OpCodes.JUMPDEST, OpCodes.PUSH1, 0x2a });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(42), stack.pop());
    }

    @Test
    public void executeJumpWithoutJumpDestRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x04, OpCodes.JUMP, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeJumpWithNot32BitsIntegerTargetRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH5, 0x01, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, OpCodes.JUMP, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeJumpWithTwoLarge32BitsIntegerTargetRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH1, (byte)0xff, OpCodes.JUMP, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeConditionalJumpOperation() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x06, OpCodes.JUMPI, OpCodes.STOP, OpCodes.JUMPDEST, OpCodes.PUSH1, 0x2a });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(42), stack.pop());
    }

    @Test
    public void executeConditionalJumpWithoutJumpDestRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x06, OpCodes.JUMPI, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeConditionalJumpWithNot32BitsIntegerTargetRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH5, 0x01, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, OpCodes.JUMPI, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeConditionalJumpWithTwoLarge32BitsIntegerTargetRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 1, OpCodes.PUSH1, (byte)0xff, OpCodes.JUMPI, OpCodes.STOP, OpCodes.PUSH1, 0x2a });
    }

    @Test
    public void executeInvalidOpCodeRaiseException() throws VirtualMachineException {
        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid opcode");
        virtualMachine.execute(new byte[] { (byte)0xfe });
    }

    private static void executeUnaryOp(byte opcode, int expected, int operand, int expectedGasUsed) throws VirtualMachineException {
        byte[] boperand = ByteUtils.normalizedBytes(ByteUtils.unsignedIntegerToBytes(operand));

        byte[] bytecodes = new byte[2 + boperand.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand.length - 1);
        System.arraycopy(boperand, 0, bytecodes, 1, boperand.length);
        bytecodes[boperand.length + 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(null, null);

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

        VirtualMachine virtualMachine = new VirtualMachine(null, null);

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

        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(bytecodes);

        Assert.assertEquals(expectedGasUsed, virtualMachine.getGasUsed());

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromSignedLong(expected), stack.pop());
    }

    private static void executeBinaryOp(String operand1, String operand2, byte opcode, String expected) throws VirtualMachineException {
        byte[] boperand1 = DataWord.fromHexadecimalString(operand1).toNormalizedBytes();
        byte[] boperand2 = DataWord.fromHexadecimalString(operand2).toNormalizedBytes();

        byte[] bytecodes = new byte[3 + boperand1.length + boperand2.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand1.length - 1);
        System.arraycopy(boperand1, 0, bytecodes, 1, boperand1.length);
        bytecodes[boperand1.length + 1] = (byte)(OpCodes.PUSH1 + boperand2.length - 1);
        System.arraycopy(boperand2, 0, bytecodes, 2 + boperand1.length, boperand2.length);
        bytecodes[bytecodes.length - 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(bytecodes);

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

        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(bytecodes);

        Assert.assertEquals(expectedGasUsed, virtualMachine.getGasUsed());

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

        VirtualMachine virtualMachine = new VirtualMachine(null, null);

        virtualMachine.execute(bytecodes);

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromHexadecimalString(expected), stack.pop());
    }
}
