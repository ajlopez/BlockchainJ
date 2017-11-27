package com.ajlopez.blockchain.vm;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class OpCodes {
    private OpCodes() { }
    
    public static final byte OP_PUSH = 1;
    public static final byte OP_POP = 2;
    public static final byte OP_ADD = 3;
    public static final byte OP_SUBTRACT = 4;
    public static final byte OP_MULTIPLY = 5;
    public static final byte OP_DIVIDE = 6;
    public static final byte OP_DUP = 7;
    public static final byte OP_SWAP = 8;
    public static final byte OP_EQUAL = 9;
    public static final byte OP_SSTORE = 10;
    public static final byte OP_SLOAD = 11;
    public static final byte OP_MSTORE = 12;
    public static final byte OP_MLOAD = 13;
}
