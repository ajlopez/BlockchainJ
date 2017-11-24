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
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[0]);

        Assert.assertTrue(stack.isEmpty());
    }

    @Test
    public void executePushThreeBytes() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x03, 0x01, 0x02, 0x03 });

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, stack.get(0));
    }

    @Test
    public void executePushAndPop() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x03, 0x01, 0x02, 0x03, OpCodes.OP_POP });

        Assert.assertTrue(stack.isEmpty());
    }

    @Test
    public void executeAddTwoBytes() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_ADD });

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x03 }, stack.get(0));
    }

    @Test
    public void executeAddTwoBytesWithOverflow() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, (byte)0xff, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_ADD });

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x01, 0x00 }, stack.get(0));
    }

    @Test
    public void executeMultiplyTwoBytes() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, (byte)0xff, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_MULTIPLY });

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x01, (byte)0xfe }, stack.get(0));
    }

    @Test
    public void executeDivideTwoBytes() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_PUSH, 0x01, 0x54, OpCodes.OP_DIVIDE });

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x2a }, stack.get(0));
    }

    @Test
    public void executeSubtractTwoBytes() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_SUBTRACT });

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0xff }, stack.get(0));
    }

    @Test
    public void executeSubtractTwoBytesWithUnsignedIntegers() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x02, 0x01, 0x00, OpCodes.OP_SUBTRACT });

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x00, (byte)0xff }, stack.get(0));
    }

    @Test
    public void executeDupTopOfStack() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_DUP, 0x00 });

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(2, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.get(0));
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.get(1));
    }

    @Test
    public void executeDupSecondElementOfStack() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_DUP, 0x01 });

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(3, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x02 }, stack.pop());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
        Assert.assertArrayEquals(new byte[] { (byte)0x02 }, stack.pop());
    }

    @Test
    public void executeSwapOfStack() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_SWAP, 0x01 });

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(2, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
        Assert.assertArrayEquals(new byte[] { (byte)0x02 }, stack.pop());
    }

    @Test
    public void executeSwapOfThirdElementOfStack() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_PUSH, 0x01, 0x03, OpCodes.OP_SWAP, 0x02 });

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(3, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
        Assert.assertArrayEquals(new byte[] { (byte)0x02 }, stack.pop());
        Assert.assertArrayEquals(new byte[] { (byte)0x03 }, stack.pop());
    }

    @Test
    public void executeEqualTwoElementsOfStack() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_EQUAL});

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
    }

    @Test
    public void executeEqualTwoDifferentElementsOfStack() {
        Stack<byte[]> stack = new Stack<>();
        VirtualMachine vm = new VirtualMachine(stack, null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_EQUAL});

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x00 }, stack.pop());
    }

    @Test
    public void executeStoreValueToStorage() {
        Stack<byte[]> stack = new Stack<>();
        Storage storage = new Storage();
        VirtualMachine vm = new VirtualMachine(stack, storage);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_SSTORE});

        Assert.assertTrue(stack.isEmpty());
        Assert.assertArrayEquals(new byte[] { 0x01 }, storage.getValue(new byte[] { 0x02 }));
    }
}
