package com.ajlopez.blockchain.vm;

import com.ajlopez.blockchain.utils.ByteUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Stack;

import static com.ajlopez.blockchain.vm.OpCodes.*;

/**
 * Created by ajloepz on 12/08/2017.
 */
public class VirtualMachine {
    private Stack<byte[]> stack;
    private Storage storage;
    private Memory memory;

    private int pc;
    private byte[] opcodes;

    public VirtualMachine(Storage storage) {
        this.storage = storage;
        this.stack = new Stack();
        this.memory = new Memory();
    }

    public Stack<byte[]> getStack() {
        return this.stack;
    }

    public Memory getMemory() {
        return this.memory;
    }

    public void execute(byte[] opcodes) throws VirtualMachineException {
        this.pc = 0;
        this.opcodes = opcodes;

        int l = opcodes.length;

        while (pc < l)
            execute(opcodes[pc]);
    }

    private void execute(byte opcode) throws VirtualMachineException {
        BigInteger value1;
        BigInteger value2;

        switch (opcode) {
            case OP_PUSH:
                int nbytes = this.opcodes[pc + 1];
                this.stack.push(Arrays.copyOfRange(this.opcodes, this.pc + 2, this.pc + 2 + nbytes));
                this.pc += nbytes + 1;
                break;

            case OP_POP:
                stack.pop();
                break;

            case OP_ADD:
                value1 = this.popBigInteger();
                value2 = this.popBigInteger();
                this.pushBigInteger(value1.add(value2));
                break;

            case OP_SUBTRACT:
                value1 = this.popBigInteger();
                value2 = this.popBigInteger();
                this.pushBigInteger(value1.subtract(value2));
                break;

            case OP_MULTIPLY:
                value1 = this.popBigInteger();
                value2 = this.popBigInteger();
                this.pushBigInteger(value1.multiply(value2));
                break;

            case OP_DIVIDE:
                value1 = this.popBigInteger();
                value2 = this.popBigInteger();
                this.stack.push(value1.divide(value2).toByteArray());
                break;

            case OP_MOD:
                value1 = this.popBigInteger();
                value2 = this.popBigInteger();
                this.pushBigInteger(value1.mod(value2));
                break;

            case OP_DIVMOD:
                value1 = this.popBigInteger();
                value2 = this.popBigInteger();
                BigInteger[] divmod = value1.divideAndRemainder(value2);

                this.pushBigInteger(divmod[0]);
                this.pushBigInteger(divmod[1]);

                break;

            case OP_DUP:
                int offset = this.opcodes[++pc];
                this.stack.push(this.stack.get(this.stack.size() - 1 - offset));
                break;

            case OP_SWAP:
                offset = this.opcodes[++pc];
                byte[] bvalue1 = this.stack.peek();
                byte[] bvalue2 = this.stack.get(this.stack.size() - 1 - offset);
                this.stack.set(this.stack.size() - 1 - offset, bvalue1);
                this.stack.set(this.stack.size() - 1, bvalue2);
                break;

            case OP_EQUAL:
                bvalue1 = this.stack.pop();
                bvalue2 = this.stack.pop();

                if (Arrays.equals(bvalue1, bvalue2))
                    this.stack.push(new byte[] { 0x01 });
                else
                    this.stack.push(new byte[] { 0x00 });

                break;

            case OP_SSTORE:
                bvalue1 = this.stack.pop();
                bvalue2 = this.stack.pop();
                this.storage.setValue(bvalue1, bvalue2);

                break;

            case OP_SLOAD:
                bvalue1 = this.stack.pop();
                this.stack.push(this.storage.getValue(bvalue1));

                break;

            case OP_MSTORE:
                offset = this.popInteger();
                byte[] bytes = this.stack.pop();
                this.memory.setValues(offset, bytes);

                break;

            case OP_MLOAD:
                offset = this.popInteger();
                int length = this.popBigInteger().intValue();
                this.stack.push(this.memory.getValues(offset, length));

                break;

            case OP_JUMP:
                nbytes = this.opcodes[pc + 1];
                offset = ByteUtils.bytesToUnsignedInteger(Arrays.copyOfRange(this.opcodes, this.pc + 2, this.pc + 2 + nbytes));

                if (this.opcodes[offset] != OP_JUMPDEST)
                    throw new VirtualMachineException("Invalid jump");

                this.pc = offset;

                break;
        }

        this.pc++;
    }

    private BigInteger popBigInteger() {
        return new BigInteger(1, this.stack.pop());
    }

    private void pushBigInteger(BigInteger value) {
        this.stack.push(value.toByteArray());
    }

    private int popInteger() {
        byte[] bytes = this.stack.pop();

        return ByteUtils.bytesToUnsignedInteger(bytes);
    }
}
