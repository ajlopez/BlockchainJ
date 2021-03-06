package com.ajlopez.blockchain.vms.newvm;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Stack;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class VirtualMachineTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void executeEmptyOpCodes() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[0]);

        Stack<byte[]> stack = vm.getStack();

        Assert.assertTrue(stack.isEmpty());
    }

    @Test
    public void executePushThreeBytes() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x03, 0x01, 0x02, 0x03 });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, stack.get(0));
    }

    @Test
    public void executePushAndPop() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x03, 0x01, 0x02, 0x03, OpCodes.OP_POP });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertTrue(stack.isEmpty());
    }

    @Test
    public void executeAddTwoBytes() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_ADD });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x03 }, stack.get(0));
    }

    @Test
    public void executeAddTwoBytesWithOverflow() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, (byte)0xff, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_ADD });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x01, 0x00 }, stack.get(0));
    }

    @Test
    public void executeMultiplyTwoBytes() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, (byte)0xff, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_MULTIPLY });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x01, (byte)0xfe }, stack.get(0));
    }

    @Test
    public void executeDivideTwoBytes() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_PUSH, 0x01, 0x54, OpCodes.OP_DIVIDE });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x2a }, stack.get(0));
    }

    @Test
    public void executeSubtractTwoBytes() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_SUBTRACT });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0xff }, stack.get(0));
    }

    @Test
    public void executeSubtractTwoBytesWithUnsignedIntegers() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x02, 0x01, 0x00, OpCodes.OP_SUBTRACT });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x00, (byte)0xff }, stack.get(0));
    }

    @Test
    public void executeModTwoBytes() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x03, OpCodes.OP_PUSH, 0x01, 0x08, OpCodes.OP_MOD });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x02 }, stack.get(0));
    }

    @Test
    public void executeExpTwoBytes() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x03, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_EXP });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x08 }, stack.get(0));
    }

    @Test
    public void executeDupTopOfStack() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_DUP, 0x00 });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(2, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.get(0));
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.get(1));
    }

    @Test
    public void executeDupSecondElementOfStack() throws VirtualMachineException, IOException {
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
    public void executeSwapOfStack() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_SWAP, 0x01 });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(2, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
        Assert.assertArrayEquals(new byte[] { (byte)0x02 }, stack.pop());
    }

    @Test
    public void executeSwapOfThirdElementOfStack() throws VirtualMachineException, IOException {
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
    public void executeEqualTwoElementsOfStack() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_EQUAL});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
    }

    @Test
    public void executeEqualTwoDifferentElementsOfStack() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_EQUAL});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x00 }, stack.pop());
    }

    @Test
    public void executeLessThanTwoElementsOfStack() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x10, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_LT});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
    }

    @Test
    public void executeLessThanTwoElementsOfStackGivenFalse() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x10, OpCodes.OP_PUSH, 0x01, 0x20, OpCodes.OP_LT});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x00 }, stack.pop());
    }


    @Test
    public void executeGreaterThanTwoElementsOfStack() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x10, OpCodes.OP_GT});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x01 }, stack.pop());
    }

    @Test
    public void executeGreaterThanTwoElementsOfStackGivenFalse() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x20, OpCodes.OP_PUSH, 0x01, 0x10, OpCodes.OP_GT});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { (byte)0x00 }, stack.pop());
    }

    @Test
    public void executeStoreValueToStorage() throws VirtualMachineException, IOException {
        Storage storage = new Storage();
        VirtualMachine vm = new VirtualMachine(storage);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_SSTORE});

        Stack<byte[]> stack = vm.getStack();

        Assert.assertTrue(stack.isEmpty());
        Assert.assertArrayEquals(new byte[] { 0x01 }, storage.getValue(new byte[] { 0x02 }));
    }

    @Test
    public void executeLoadValueFromStorage() throws VirtualMachineException, IOException {
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
    public void executeStoreValueToMemory() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_MSTORE});

        Stack stack = vm.getStack();

        Assert.assertTrue(stack.isEmpty());
        Assert.assertEquals(1, vm.getMemory().getValue(2));
    }

    @Test
    public void executeStoreMultiByteValueToMemory() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x02, 0x01, 0x02, OpCodes.OP_PUSH, 0x01, 0x02, OpCodes.OP_MSTORE});

        Stack stack = vm.getStack();

        Assert.assertTrue(stack.isEmpty());
        Assert.assertEquals(1, vm.getMemory().getValue(2));
        Assert.assertEquals(2, vm.getMemory().getValue(3));
    }

    @Test
    public void executeLoadValueFromMemory() throws VirtualMachineException, IOException {
        Storage storage = new Storage();
        VirtualMachine vm = new VirtualMachine(storage);
        vm.getMemory().setValue(0x0102, (byte)0x03);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x02, 0x01, 0x02, OpCodes.OP_MLOAD });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x03 }, stack.pop());
    }

    @Test
    public void executeJumpToJumpDest() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_JUMP, 0x01, 0x06, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_JUMPDEST, OpCodes.OP_PUSH, 0x01, 0x02 });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x02 }, stack.pop());
    }

    @Test
    public void executeJumpWithoutJumpDestRaiseException() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        vm.execute(new byte[] { OpCodes.OP_JUMP, 0x01, 0x06, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_PUSH, 0x01, 0x02 });
    }

    @Test
    public void executeJumpToFalseJumpDestRaiseException() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        vm.execute(new byte[] { OpCodes.OP_JUMP, 0x01, 0x06, OpCodes.OP_PUSH, 0x02, 0x01, OpCodes.OP_JUMPDEST });
    }

    @Test
    public void executeJumpIfZeroToJumpDest() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x00, OpCodes.OP_JUMPI, 0x01, 0x09, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_JUMPDEST, OpCodes.OP_PUSH, 0x01, 0x02 });

        Stack<byte[]> stack = vm.getStack();

        Assert.assertFalse(stack.isEmpty());
        Assert.assertEquals(1, stack.size());
        Assert.assertArrayEquals(new byte[] { 0x02 }, stack.pop());
    }

    @Test
    public void executeJumpIfZeroWithoutJumpDestRaiseException() throws VirtualMachineException, IOException {
        VirtualMachine vm = new VirtualMachine(null);

        exception.expect(VirtualMachineException.class);
        exception.expectMessage("Invalid jump");
        vm.execute(new byte[] { OpCodes.OP_PUSH, 0x01, 0x00, OpCodes.OP_JUMPI, 0x01, 0x09, OpCodes.OP_PUSH, 0x01, 0x01, OpCodes.OP_ADD, OpCodes.OP_PUSH, 0x01, 0x02 });
    }
}
