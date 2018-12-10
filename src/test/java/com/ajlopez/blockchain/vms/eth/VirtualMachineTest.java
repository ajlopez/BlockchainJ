package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Stack;

/**
 * Created by ajlopez on 10/12/2018.
 */
public class VirtualMachineTest {
    @Test
    public void executeEmptyCode() {
        VirtualMachine virtualMachine = new VirtualMachine();

        virtualMachine.execute(new byte[0]);

        Stack<DataWord> stack = virtualMachine.getStack();

        Assert.assertNotNull(stack);
        Assert.assertTrue(stack.isEmpty());
    }

    @Test
    public void executePushOneByte() {
        VirtualMachine virtualMachine = new VirtualMachine();

        virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x01 });

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

            opcodes[0] = (byte)(OpCodes.PUSH1 + k);
            System.arraycopy(value, 0, opcodes, 1, k + 1);

            VirtualMachine virtualMachine = new VirtualMachine();

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
}
