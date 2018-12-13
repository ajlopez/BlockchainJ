package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

import java.util.Stack;

/**
 * Created by ajlopez on 10/12/2018.
 */
public class VirtualMachine {
    private final Storage storage;
    private final Memory memory = new Memory();
    private final Stack<DataWord> stack = new Stack<>();

    public VirtualMachine(Storage storage) {
        this.storage = storage;
    }

    public void execute(byte[] bytecodes) {
        int l = bytecodes.length;

        for (int pc = 0; pc < l; pc++) {
            byte bytecode = bytecodes[pc];

            switch (bytecode) {
                case OpCodes.ADD:
                    DataWord word1 = this.stack.pop();
                    DataWord word2 = this.stack.pop();
                    this.stack.push(word1.add(word2));
                    break;

                case OpCodes.SUB:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    this.stack.push(word1.sub(word2));
                    break;

                case OpCodes.LT:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    this.stack.push(word1.compareTo(word2) < 0 ? DataWord.ONE : DataWord.ZERO);
                    break;

                case OpCodes.GT:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    this.stack.push(word1.compareTo(word2) > 0 ? DataWord.ONE : DataWord.ZERO);
                    break;

                case OpCodes.EQ:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    this.stack.push(word1.compareTo(word2) == 0 ? DataWord.ONE : DataWord.ZERO);
                    break;

                case OpCodes.AND:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    this.stack.push(word1.and(word2));
                    break;

                case OpCodes.OR:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    this.stack.push(word1.or(word2));
                    break;

                case OpCodes.XOR:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    this.stack.push(word1.xor(word2));
                    break;

                case OpCodes.POP:
                    this.stack.pop();
                    break;

                case OpCodes.MLOAD:
                    word1 = this.stack.pop();
                    this.stack.push(this.memory.getValue(word1.asUnsignedInteger()));
                    break;

                case OpCodes.MSTORE:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    this.memory.setValue(word1.asUnsignedInteger(), word2);
                    break;

                case OpCodes.MSTORE8:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    this.memory.setByte(word1.asUnsignedInteger(), word2.getBytes()[DataWord.DATAWORD_BYTES - 1]);
                    break;

                case OpCodes.SLOAD:
                    word1 = this.stack.pop();
                    this.stack.push(this.storage.getValue(word1));
                    break;

                case OpCodes.SSTORE:
                    word1 = this.stack.pop();
                    word2 = this.stack.pop();
                    this.storage.setValue(word1, word2);
                    break;

                case OpCodes.PC:
                    this.stack.push(DataWord.fromUnsignedInteger(pc));
                    break;

                case OpCodes.MSIZE:
                    this.stack.push(DataWord.fromUnsignedInteger(this.memory.size()));
                    break;

                case OpCodes.PUSH1:
                case OpCodes.PUSH2:
                case OpCodes.PUSH3:
                case OpCodes.PUSH4:
                case OpCodes.PUSH5:
                case OpCodes.PUSH6:
                case OpCodes.PUSH7:
                case OpCodes.PUSH8:
                case OpCodes.PUSH9:
                case OpCodes.PUSH10:
                case OpCodes.PUSH11:
                case OpCodes.PUSH12:
                case OpCodes.PUSH13:
                case OpCodes.PUSH14:
                case OpCodes.PUSH15:
                case OpCodes.PUSH16:
                case OpCodes.PUSH17:
                case OpCodes.PUSH18:
                case OpCodes.PUSH19:
                case OpCodes.PUSH20:
                case OpCodes.PUSH21:
                case OpCodes.PUSH22:
                case OpCodes.PUSH23:
                case OpCodes.PUSH24:
                case OpCodes.PUSH25:
                case OpCodes.PUSH26:
                case OpCodes.PUSH27:
                case OpCodes.PUSH28:
                case OpCodes.PUSH29:
                case OpCodes.PUSH30:
                case OpCodes.PUSH31:
                case OpCodes.PUSH32:
                    int lb = bytecode - OpCodes.PUSH1 + 1;
                    this.stack.push(DataWord.fromBytes(bytecodes, pc + 1, lb));
                    pc += lb;
                    break;

                case OpCodes.DUP1:
                case OpCodes.DUP2:
                case OpCodes.DUP3:
                case OpCodes.DUP4:
                case OpCodes.DUP5:
                case OpCodes.DUP6:
                case OpCodes.DUP7:
                case OpCodes.DUP8:
                case OpCodes.DUP9:
                case OpCodes.DUP10:
                case OpCodes.DUP11:
                case OpCodes.DUP12:
                case OpCodes.DUP13:
                case OpCodes.DUP14:
                case OpCodes.DUP15:
                case OpCodes.DUP16:
                    this.stack.push(this.stack.get(this.stack.size() - 1 - (bytecode - OpCodes.DUP1)));
                    break;

                case OpCodes.SWAP1:
                case OpCodes.SWAP2:
                case OpCodes.SWAP3:
                case OpCodes.SWAP4:
                case OpCodes.SWAP5:
                case OpCodes.SWAP6:
                case OpCodes.SWAP7:
                case OpCodes.SWAP8:
                case OpCodes.SWAP9:
                case OpCodes.SWAP10:
                case OpCodes.SWAP11:
                case OpCodes.SWAP12:
                case OpCodes.SWAP13:
                case OpCodes.SWAP14:
                case OpCodes.SWAP15:
                case OpCodes.SWAP16:
                    int size = this.stack.size();
                    int offset = bytecode - OpCodes.SWAP1 + 1;

                    word1 = this.stack.get(size - 1);
                    word2 = this.stack.get(size - 1 - offset);

                    this.stack.set(size - 1, word2);
                    this.stack.set(size - 1 - offset, word1);

                    break;
            }
        }
    }

    public Stack<DataWord> getStack() {
        return this.stack;
    }

    public Memory getMemory() {
        return this.memory;
    }
}
