package com.ajlopez.blockchain.vm;

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

    private int pc;
    private byte[] opcodes;

    public VirtualMachine(Stack<byte[]> stack, Storage storage) {
        this.stack = stack;
        this.storage = storage;
    }

    public void execute(byte[] opcodes) {
        this.pc = 0;
        this.opcodes = opcodes;

        int l = opcodes.length;

        while (pc < l)
            execute(opcodes[pc]);
    }

    private void execute(byte opcode) {
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
                value1 = new BigInteger(1, this.stack.pop());
                value2 = new BigInteger(1, this.stack.pop());
                this.stack.push(value1.add(value2).toByteArray());
                break;

            case OP_SUBTRACT:
                value1 = new BigInteger(1, this.stack.pop());
                value2 = new BigInteger(1, this.stack.pop());
                this.stack.push(value1.subtract(value2).toByteArray());
                break;

            case OP_MULTIPLY:
                value1 = new BigInteger(1, this.stack.pop());
                value2 = new BigInteger(1, this.stack.pop());
                this.stack.push(value1.multiply(value2).toByteArray());
                break;

            case OP_DIVIDE:
                value1 = new BigInteger(1, this.stack.pop());
                value2 = new BigInteger(1, this.stack.pop());
                this.stack.push(value1.divide(value2).toByteArray());
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
                storage.setValue(bvalue1, bvalue2);

                break;
            case OP_SLOAD:
                bvalue1 = this.stack.pop();
                stack.push(storage.getValue(bvalue1));

                break;
        }

        this.pc++;
    }
}
