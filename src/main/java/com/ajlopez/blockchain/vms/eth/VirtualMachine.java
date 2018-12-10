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

            if (bytecode == OpCodes.PUSH1) {
                this.stack.push(DataWord.fromBytes(bytecodes, k + 1, 1));
                k += 1;
            }
        }
    }

    public Stack<DataWord> getStack() {
        return this.stack;
    }
}
