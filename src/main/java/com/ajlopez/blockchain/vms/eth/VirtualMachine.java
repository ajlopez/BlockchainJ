package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

import java.util.Stack;

/**
 * Created by ajlopez on 10/12/2018.
 */
public class VirtualMachine {
    private final Stack<DataWord> stack = new Stack<>();

    public void execute(byte[] bytecodes) {

    }

    public Stack<DataWord> getStack() {
        return this.stack;
    }
}
