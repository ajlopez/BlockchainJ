package com.ajlopez.blockchain.vm;

import org.junit.Assert;
import org.junit.Test;

import java.util.Stack;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class VirtualMachineTest {
    @Test
    public void executeEmptyOpCodes() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[0]);

        Stack<byte[]> stack = vm.getStack();

        Assert.assertTrue(stack.isEmpty());
    }

    @Test
    public void executePushThreeBytes() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x03, 0x01, 0x02, 0x03 });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, stack.get(0));
    }

    @Test
    public void executePushAndPop() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x03, 0x01, 0x02, 0x03, OpCodes.OP_POP });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertTrue(stack.isEmpty());
    }

    @Test
    public void executeAddTwoBytes() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_ADD });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x03 }, stack.get(0));
    }


    @Test
    public void executeAddTwoBytesWithOverflow() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, (byte)0xff, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_ADD });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x01, 0x00 }, stack.get(0));
    }

    @Test
    public void executeMultiplyTwoBytes() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, (byte)0xff, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_MULTIPLY });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x01, (byte)0xfe }, stack.get(0));
    }

    @Test
    public void executeDivideTwoBytes() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_PUSH, 0x01, 0x54, OpCodes.OP_DIVIDE });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x2a }, stack.get(0));
    }

    @Test
    public void executeSubtractTwoBytes() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_SUBTRACT });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0xff }, stack.get(0));
    }

    @Test
    public void executeSubtractTwoBytesWithUnsignedIntegers() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x02, 0x01, 0x00, OpCodes.OP_SUBTRACT });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x00, (byte)0xff }, stack.get(0));
    }

    @Test
    public void executeDupTopOfStack() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_DUP, 0x00 });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(2, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.get(0));
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.get(1));
    }

    @Test
    public void executeDupSecondElementOfStack() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_DUP, 0x01 });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(3, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x02 }, stack.pop());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
        Assert.assertArrayEquals(new byte[] { (byte)0x02 }, stack.pop());
    }

    @Test
    public void executeSwapOfStack() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_SWAP, 0x01 });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(2, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
        Assert.assertArrayEquals(new byte[] { (byte)0x02 }, stack.pop());
    }

    @Test
    public void executeSwapOfThirdElementOfStack() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_PUSH, 0x01, 0x03, OpCodes.OP_SWAP, 0x02 });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(3, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
        Assert.assertArrayEquals(new byte[] { (byte)0x02 }, stack.pop());
        Assert.assertArrayEquals(new byte[] { (byte)0x03 }, stack.pop());
    }

    @Test
    public void executeEqualTwoElementsOfStack() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_EQUAL});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
    }

    @Test
    public void executeEqualTwoDifferentElementsOfStack() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_EQUAL});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x00 }, stack.pop());
    }

    @Test
    public void executeStoreValueToStorage() {
        Storage storage = new Storage();
        VirtualMachine vm = new VirtualMachine(storage);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_SSTORE});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertTrue(stack.isEmpty());
        Assert.assertArrayEquals(new byte[] { 0x01 }, storage.getValue(new byte[] { 0x02 }));
    }

    @Test
    public void executeLoadValueFromStorage() {
        Storage storage = new Storage();
        storage.setValue(new byte[] { 0x01, 0x02 }, new byte[] { 0x03, 0x04, 0x05 });
        VirtualMachine vm = new VirtualMachine(storage);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x02, 0x01, 0x02, OpCodes.OP_SLOAD});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x03, 0x04, 0x05 }, stack.pop());
    }

    @Test
    public void executeStoreValueToMemory() {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_MSTORE});

        Stack stack = vm.getStack();

        Assert.assertTrue(stack.isEmpty());
        Assert.assertArrayEquals(new byte[] { 0x01 }, vm.getMemory().getValue(new byte[] { 0x02 }));
    }

    @Test
    public void executeLoadValueFromMemory() {
        Storage storage = new Storage();
        VirtualMachine vm = new VirtualMachine(storage);
        vm.getMemory().setValue(new byte[] { 0x01, 0x02 }, new byte[] { 0x03, 0x04, 0x05 });

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x02, 0x01, 0x02, OpCodes.OP_MLOAD});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x03, 0x04, 0x05 }, stack.pop());
    }
}
