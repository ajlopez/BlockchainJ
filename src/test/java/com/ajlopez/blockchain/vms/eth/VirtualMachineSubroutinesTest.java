package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 12/11/2020.
 */
public class VirtualMachineSubroutinesTest {
    @Test
    public void executeSimpleSubroutine() throws IOException {
        MessageData messageData = new MessageData(null, null, null, Coin.ZERO, 100_000L, Coin.ZERO, null, 0, 0, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, null), null);

        byte[] bytecodes = new byte[] {
                OpCodes.PUSH1, 0x04,
                OpCodes.JUMPSUB,
                OpCodes.STOP,
                OpCodes.BEGINSUB,
                OpCodes.RETURNSUB
        };

        ExecutionResult executionResult = virtualMachine.execute(bytecodes);

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() +  FeeSchedule.LOW.getValue() + FeeSchedule.HIGH.getValue(), executionResult.getGasUsed());

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());
        Assert.assertTrue(virtualMachine.getDataStack().isEmpty());
    }

    @Test
    public void executeSimpleSubroutineWithStackOperation() throws IOException {
        MessageData messageData = new MessageData(null, null, null, Coin.ZERO, 100_000L, Coin.ZERO, null, 0, 0, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, null), null);

        byte[] bytecodes = new byte[] {
                OpCodes.PUSH1, 0x04,
                OpCodes.JUMPSUB,
                OpCodes.STOP,
                OpCodes.BEGINSUB,
                OpCodes.PUSH1, 0x2a,
                OpCodes.RETURNSUB
        };

        ExecutionResult executionResult = virtualMachine.execute(bytecodes);

        Assert.assertEquals(FeeSchedule.VERYLOW.getValue() * 2 +  FeeSchedule.LOW.getValue() + FeeSchedule.HIGH.getValue(), executionResult.getGasUsed());

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());
        Assert.assertFalse(virtualMachine.getDataStack().isEmpty());
        Assert.assertEquals(1, virtualMachine.getDataStack().size());
        Assert.assertEquals(DataWord.fromUnsignedInteger(42), virtualMachine.getDataStack().pop());
    }

    @Test
    public void executeTwoLevelsOfSubroutines() throws IOException {
        MessageData messageData = new MessageData(null, null, null, Coin.ZERO, 100_000L, Coin.ZERO, null, 0, 0, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, null), null);

        byte[] bytecodes = new byte[] {
                OpCodes.PUSH9, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0c,
                OpCodes.JUMPSUB,
                OpCodes.STOP,
                OpCodes.BEGINSUB,
                OpCodes.PUSH1, 0x11,
                OpCodes.JUMPSUB,
                OpCodes.RETURNSUB,
                OpCodes.BEGINSUB,
                OpCodes.RETURNSUB
        };

        ExecutionResult executionResult = virtualMachine.execute(bytecodes);

        Assert.assertEquals(
                FeeSchedule.VERYLOW.getValue() * 2 +
                        FeeSchedule.LOW.getValue() * 2 +
                        FeeSchedule.HIGH.getValue() * 2,
                executionResult.getGasUsed());

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());
        Assert.assertTrue(virtualMachine.getDataStack().isEmpty());
    }

    @Test
    public void executeSubroutineAtTheEndOfCode() throws IOException {
        MessageData messageData = new MessageData(null, null, null, Coin.ZERO, 100_000L, Coin.ZERO, null, 0, 0, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, null), null);

        ExecutionResult executionResult = virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x05, OpCodes.JUMP, OpCodes.BEGINSUB, OpCodes.RETURNSUB, OpCodes.JUMPDEST, OpCodes.PUSH1, 0x03, OpCodes.JUMPSUB });

        Assert.assertEquals(FeeSchedule.JUMPDEST.getValue() +  FeeSchedule.VERYLOW.getValue() * 2 +  FeeSchedule.MID.getValue() + FeeSchedule.LOW.getValue() + FeeSchedule.HIGH.getValue(), executionResult.getGasUsed());

        Assert.assertNotNull(executionResult);
        Assert.assertTrue(executionResult.wasSuccesful());
        Assert.assertTrue(virtualMachine.getDataStack().isEmpty());
    }

    @Test
    public void errorOnWalkIntoSubroutine() throws IOException {
        MessageData messageData = new MessageData(null, null, null, Coin.ZERO, 100_000L, Coin.ZERO, null, 0, 0, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, null), null);

        ExecutionResult executionResult = virtualMachine.execute(new byte[] { OpCodes.BEGINSUB, OpCodes.RETURNSUB, OpCodes.STOP  });

        Assert.assertNotNull(executionResult);
        Assert.assertFalse(executionResult.wasSuccesful());
        Assert.assertTrue(executionResult.getException() instanceof  VirtualMachineException);
        Assert.assertEquals("Invalid subroutine entry", executionResult.getException().getMessage());
        Assert.assertEquals(100_000L, executionResult.getGasUsed());
    }

    @Test
    public void errorOnShallowReturnStack() throws IOException {
        MessageData messageData = new MessageData(null, null, null, Coin.ZERO, 100_000L, Coin.ZERO, null, 0, 0, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, null), null);

        ExecutionResult executionResult = virtualMachine.execute(new byte[] { OpCodes.RETURNSUB });

        Assert.assertNotNull(executionResult);
        Assert.assertFalse(executionResult.wasSuccesful());
        Assert.assertTrue(executionResult.getException() instanceof  VirtualMachineException);
        Assert.assertEquals("Invalid retsub", executionResult.getException().getMessage());
        Assert.assertEquals(100_000L, executionResult.getGasUsed());
    }

    @Test
    public void errorOnInvalidSubroutineJump() throws IOException {
        MessageData messageData = new MessageData(null, null, null, Coin.ZERO, 100_000L, Coin.ZERO, null, 0, 0, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, null), null);

        ExecutionResult executionResult = virtualMachine.execute(new byte[] { OpCodes.PUSH9, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0c, OpCodes.JUMPSUB });

        Assert.assertNotNull(executionResult);
        Assert.assertFalse(executionResult.wasSuccesful());
        Assert.assertTrue(executionResult.getException() instanceof  VirtualMachineException);
        Assert.assertEquals("Invalid subroutine jump", executionResult.getException().getMessage());
        Assert.assertEquals(100_000L, executionResult.getGasUsed());
    }

    @Test
    public void errorOnInvalidSubroutineJumpWithoutBeginSub() throws IOException {
        MessageData messageData = new MessageData(null, null, null, Coin.ZERO, 100_000L, Coin.ZERO, null, 0, 0, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, null), null);

        ExecutionResult executionResult = virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x04, OpCodes.JUMPSUB, OpCodes.STOP, OpCodes.RETURNSUB });

        Assert.assertNotNull(executionResult);
        Assert.assertFalse(executionResult.wasSuccesful());
        Assert.assertTrue(executionResult.getException() instanceof  VirtualMachineException);
        Assert.assertEquals("Invalid subroutine jump", executionResult.getException().getMessage());
        Assert.assertEquals(100_000L, executionResult.getGasUsed());
    }

    @Test
    public void errorOnInvalidSubroutineJumpBeyondCode() throws IOException {
        MessageData messageData = new MessageData(null, null, null, Coin.ZERO, 100_000L, Coin.ZERO, null, 0, 0, false);
        VirtualMachine virtualMachine = new VirtualMachine(new ProgramEnvironment(messageData, null, null), null);

        ExecutionResult executionResult = virtualMachine.execute(new byte[] { OpCodes.PUSH1, 0x10, OpCodes.JUMPSUB, OpCodes.STOP, OpCodes.RETURNSUB });

        Assert.assertNotNull(executionResult);
        Assert.assertFalse(executionResult.wasSuccesful());
        Assert.assertTrue(executionResult.getException() instanceof  VirtualMachineException);
        Assert.assertEquals("Invalid subroutine jump", executionResult.getException().getMessage());
        Assert.assertEquals(100_000L, executionResult.getGasUsed());
    }
}
