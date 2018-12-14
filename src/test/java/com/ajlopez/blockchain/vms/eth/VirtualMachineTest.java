package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Stack;

/**
 * Created by ajlopez on 10/12/2018.
 */
public class VirtualMachineTest {
    @Test
    public void executeEmptyCode() {
        VirtualMachine virtualMachine = new VirtualMachine(null);

        virtualMachine.execute(new byte[0]);

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());
    }

    @Test
    public void executePushOneByte() {
        VirtualMachine virtualMachine = new VirtualMachine(null);

        virtualMachine.execute(new byte[]{OpCodes.PUSH1, 0x01});

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());

        DataWord result = stack.pop();

        Assert.assertNotNull(result);
        Assert.assertEquals(DataWord.ONE, result);
    }

    @Test
    public void executePushBytes() {
        for (int k = 0; k < 32; k++) {
            byte[] value = FactoryHelper.createRandomBytes(k + 1);
            byte[] opcodes = new byte[k + 2];

            opcodes[0] = (byte) (OpCodes.PUSH1 + k);
            System.arraycopy(value, 0, opcodes, 1, k + 1);

            VirtualMachine virtualMachine = new VirtualMachine(null);

            virtualMachine.execute(opcodes);

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
    public void executeProgramCounter() {
        VirtualMachine virtualMachine = new VirtualMachine(null);

        virtualMachine.execute(new byte[]{OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x02, OpCodes.PC });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(3, stack.size());

        Assert.assertEquals(DataWord.fromUnsignedInteger(4), stack.pop());
        Assert.assertEquals(DataWord.fromUnsignedInteger(2), stack.pop());
        Assert.assertEquals(DataWord.fromUnsignedInteger(1), stack.pop());
    }

    @Test
    public void executeAdd() {
        VirtualMachine virtualMachine = new VirtualMachine(null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x02, OpCodes.ADD });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());

        DataWord result = stack.pop();

        Assert.assertNotNull(result);
        Assert.assertEquals("0x03", result.toNormalizedString());
    }

    @Test
    public void executeAddOperations() {
        executeBinaryOp(0, 0, OpCodes.ADD, 0);
        executeBinaryOp(1, 2, OpCodes.ADD, 3);
        executeBinaryOp(40, 2, OpCodes.ADD, 42);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.ADD, 1024 * 1024 * 1024 + 1);
    }

    @Test
    public void executeAddWithOverflow() {
        VirtualMachine virtualMachine = new VirtualMachine(null);
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
    public void executeSub() {
        VirtualMachine virtualMachine = new VirtualMachine(null);

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
    public void executeSubOperations() {
        executeBinaryOp(0, 0, OpCodes.SUB, 0);
        executeBinaryOp(1, 3, OpCodes.SUB, 2);
        executeBinaryOp(2, 44, OpCodes.SUB, 42);
        executeBinaryOp(1, 1024 * 1024 * 1024 + 1, OpCodes.SUB, 1024 * 1024 * 1024);
    }

    @Test
    public void executeSubWithUnderflow() {
        VirtualMachine virtualMachine = new VirtualMachine(null);
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
    public void executeDupTopOfStack() {
        VirtualMachine virtualMachine = new VirtualMachine(null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.DUP1 });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(2, stack.size());

        Assert.assertEquals(DataWord.ONE, stack.pop());
        Assert.assertEquals(DataWord.ONE, stack.pop());
    }

    @Test
    public void executeDups() {
        for (int k = 0; k < 16; k++) {
            VirtualMachine virtualMachine = new VirtualMachine(null);

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

            Stack<DataWord> stack = virtualMachine.getStack();

            Assert.assertNotNull(stack);
            Assert.assertFalse(stack.isEmpty());
            Assert.assertEquals(k + 2, stack.size());

            Assert.assertEquals(DataWord.fromBytes(values[0], 0, values[0].length), stack.pop());
        }
    }

    @Test
    public void executeSwaps() {
        for (int k = 0; k < 16; k++) {
            VirtualMachine virtualMachine = new VirtualMachine(null);

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

            Stack<DataWord> stack = virtualMachine.getStack();

            Assert.assertNotNull(stack);
            Assert.assertFalse(stack.isEmpty());
            Assert.assertEquals(k + 2, stack.size());

            Assert.assertEquals(DataWord.fromBytes(values[0], 0, values[0].length), stack.peek());
            Assert.assertEquals(DataWord.fromBytes(values[values.length - 1], 0, values[values.length - 1].length), stack.get(0));
        }
    }

    @Test
    public void executePop() {
        VirtualMachine virtualMachine = new VirtualMachine(null);

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
    public void executeStorageLoad() {
        Storage storage = new MapStorage();

        storage.setValue(DataWord.ONE, DataWord.fromUnsignedInteger(42));

        VirtualMachine virtualMachine = new VirtualMachine(storage);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01, OpCodes.SLOAD });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(42), stack.pop());
    }

    @Test
    public void executeStorageStorage() {
        Storage storage = new MapStorage();

        VirtualMachine virtualMachine = new VirtualMachine(storage);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.SSTORE });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), storage.getValue(DataWord.ONE));
    }

    @Test
    public void executeMemoryStore() {
        VirtualMachine virtualMachine = new VirtualMachine(null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.MSTORE });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), memory.getValue(1));
    }

    @Test
    public void executeMemoryStoreAndMemoryLoad() {
        VirtualMachine virtualMachine = new VirtualMachine(null);

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
    public void executeMemoryStoreAndMemorySize() {
        VirtualMachine virtualMachine = new VirtualMachine(null);

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
    public void executeMemoryStoreByte() {
        VirtualMachine virtualMachine = new VirtualMachine(null);

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x2a, OpCodes.PUSH1, 0x01, OpCodes.MSTORE8 });

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());

        Memory memory = virtualMachine.getMemory();

        Assert.assertEquals("0x2a000000000000000000000000000000000000000000000000000000000000", memory.getValue(0).toNormalizedString());
    }

    @Test
    public void executeLessThanOperations() {
        executeBinaryOp(0, 0, OpCodes.LT, 0);
        executeBinaryOp(1, 2, OpCodes.LT, 0);
        executeBinaryOp(40, 2, OpCodes.LT, 1);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.LT, 1);
    }

    @Test
    public void executeGreaterThanOperations() {
        executeBinaryOp(0, 0, OpCodes.GT, 0);
        executeBinaryOp(1, 2, OpCodes.GT, 1);
        executeBinaryOp(40, 2, OpCodes.GT, 0);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.GT, 0);
    }

    @Test
    public void executeEqualsOperations() {
        executeBinaryOp(0, 0, OpCodes.EQ, 1);
        executeBinaryOp(1, 2, OpCodes.EQ, 0);
        executeBinaryOp(42, 42, OpCodes.EQ, 1);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.EQ, 0);
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.EQ, 1);
    }

    @Test
    public void executeAndOperations() {
        executeBinaryOp(0, 0, OpCodes.AND, 0);
        executeBinaryOp(1, 2, OpCodes.AND, 0);
        executeBinaryOp(3, 3, OpCodes.AND, 3);
        executeBinaryOp(255, 42, OpCodes.AND, 42);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.AND, 0);
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.AND, 1024 * 1024 * 1024);
    }

    @Test
    public void executeOrOperations() {
        executeBinaryOp(0, 0, OpCodes.OR, 0);
        executeBinaryOp(1, 2, OpCodes.OR, 3);
        executeBinaryOp(3, 3, OpCodes.OR, 3);
        executeBinaryOp(255, 42, OpCodes.OR, 255);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.OR, 1024 * 1024 * 1024 + 1);
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.OR, 1024 * 1024 * 1024);
    }

    @Test
    public void executeXorOperations() {
        executeBinaryOp(0, 0, OpCodes.XOR, 0);
        executeBinaryOp(1, 2, OpCodes.XOR, 3);
        executeBinaryOp(3, 3, OpCodes.XOR, 0);
        executeBinaryOp(255, 42, OpCodes.XOR, 255 ^ 42);
        executeBinaryOp(1024 * 1024 * 1024, 1, OpCodes.XOR, 1024 * 1024 * 1024 + 1);
        executeBinaryOp(1024 * 1024 * 1024, 1024 * 1024 * 1024, OpCodes.XOR, 0);
    }

    @Test
    public void executeByteOperations() {
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "00", OpCodes.BYTE, "01");
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "01", OpCodes.BYTE, "02");
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "02", OpCodes.BYTE, "03");
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "1f", OpCodes.BYTE, "20");
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20", "2a", OpCodes.BYTE, "00");
        executeBinaryOp("0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1fff", "1f", OpCodes.BYTE, "ff");
    }

    @Test
    public void executeIsZeroOperations() {
        executeUnaryOp(0, OpCodes.ISZERO, 1);
        executeUnaryOp(42, OpCodes.ISZERO, 0);
        executeUnaryOp(1024 * 1024 * 1024, OpCodes.ISZERO, 0);
    }

    @Test
    public void executeNotOperations() {
        executeUnaryOp("00", OpCodes.NOT, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        executeUnaryOp("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", OpCodes.NOT, "00");
        executeUnaryOp("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff00", OpCodes.NOT, "ff");
    }

    private static void executeUnaryOp(int operand, byte opcode, int expected) {
        byte[] boperand = ByteUtils.normalizedBytes(ByteUtils.unsignedIntegerToBytes(operand));

        byte[] bytecodes = new byte[2 + boperand.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand.length - 1);
        System.arraycopy(boperand, 0, bytecodes, 1, boperand.length);
        bytecodes[boperand.length + 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(null);

        virtualMachine.execute(bytecodes);

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(expected), stack.pop());
    }

    private static void executeUnaryOp(String operand, byte opcode, String expected) {
        byte[] boperand = DataWord.fromHexadecimalString(operand).toNormalizedBytes();

        byte[] bytecodes = new byte[2 + boperand.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand.length - 1);
        System.arraycopy(boperand, 0, bytecodes, 1, boperand.length);
        bytecodes[boperand.length + 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(null);

        virtualMachine.execute(bytecodes);

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromHexadecimalString(expected), stack.pop());
    }

    private static void executeBinaryOp(int operand1, int operand2, byte opcode, int expected) {
        byte[] boperand1 = ByteUtils.normalizedBytes(ByteUtils.unsignedIntegerToBytes(operand1));
        byte[] boperand2 = ByteUtils.normalizedBytes(ByteUtils.unsignedIntegerToBytes(operand2));

        byte[] bytecodes = new byte[3 + boperand1.length + boperand2.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand1.length - 1);
        System.arraycopy(boperand1, 0, bytecodes, 1, boperand1.length);
        bytecodes[boperand1.length + 1] = (byte)(OpCodes.PUSH1 + boperand2.length - 1);
        System.arraycopy(boperand2, 0, bytecodes, 2 + boperand1.length, boperand2.length);
        bytecodes[bytecodes.length - 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(null);

        virtualMachine.execute(bytecodes);

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(expected), stack.pop());
    }

    private static void executeBinaryOp(String operand1, String operand2, byte opcode, String expected) {
        byte[] boperand1 = DataWord.fromHexadecimalString(operand1).toNormalizedBytes();
        byte[] boperand2 = DataWord.fromHexadecimalString(operand2).toNormalizedBytes();

        byte[] bytecodes = new byte[3 + boperand1.length + boperand2.length];

        bytecodes[0] = (byte)(OpCodes.PUSH1 + boperand1.length - 1);
        System.arraycopy(boperand1, 0, bytecodes, 1, boperand1.length);
        bytecodes[boperand1.length + 1] = (byte)(OpCodes.PUSH1 + boperand2.length - 1);
        System.arraycopy(boperand2, 0, bytecodes, 2 + boperand1.length, boperand2.length);
        bytecodes[bytecodes.length - 1] = opcode;

        VirtualMachine virtualMachine = new VirtualMachine(null);

        virtualMachine.execute(bytecodes);

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertEquals(1, stack.size());
        Assert.assertEquals(DataWord.fromHexadecimalString(expected), stack.pop());
    }
}
