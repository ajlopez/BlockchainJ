package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
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
}
