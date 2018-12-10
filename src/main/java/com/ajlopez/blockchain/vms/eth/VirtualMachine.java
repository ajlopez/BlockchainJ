package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

import java.util.Stack;

/**
 * Created by ajlopez on 10/12/2018.
 */
public class VirtualMachine {
    private final Stack<DataWord> stack = new Stack<>();

    public void execute(byte[] bytecodes) {
        int l = bytecodes.length;

        for (int k = 0; k < l; k++) {
            byte bytecode = bytecodes[k];

            switch (bytecode) {
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
                    this.stack.push(DataWord.fromBytes(bytecodes, k + 1, lb));
                    k += lb;
            }
        }
    }

    public Stack<DataWord> getStack() {
        return this.stack;
    }
}
