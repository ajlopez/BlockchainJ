package com.ajlopez.blockchain.vms.eth;

/**
 * Created by ajlopez on 09/12/2018.
 */

public class OpCodes {
    private OpCodes() {

    }

    public static final byte STOP = 0x00;
    public static final byte ADD = 0x01;
    public static final byte MUL = 0x02;
    public static final byte SUB = 0x03;
    public static final byte DIV = 0x04;
    public static final byte SDIV = 0x05;
    public static final byte MOD = 0x06;
    public static final byte SMOD = 0x07;
    public static final byte ADDMOD = 0x08;
    public static final byte MULMOD = 0x09;
    public static final byte EXP = 0x0a;
    public static final byte SIGNEXTEND = 0x0b;

    public static final byte POP = 0x50;
    public static final byte MLOAD = 0x51;
    public static final byte MSTORE = 0x52;
    public static final byte MSTORE8 = 0x53;
    public static final byte SLOAD = 0x54;
    public static final byte SSTORE = 0x55;

    public static final byte PUSH1 = 0x60;
    public static final byte PUSH2 = 0x61;
    public static final byte PUSH3 = 0x62;
    public static final byte PUSH4 = 0x63;
    public static final byte PUSH5 = 0x64;
    public static final byte PUSH6 = 0x65;
    public static final byte PUSH7 = 0x66;
    public static final byte PUSH8 = 0x67;
    public static final byte PUSH9 = 0x68;
    public static final byte PUSH10 = 0x69;
    public static final byte PUSH11 = 0x6a;
    public static final byte PUSH12 = 0x6b;
    public static final byte PUSH13 = 0x6c;
    public static final byte PUSH14 = 0x6d;
    public static final byte PUSH15 = 0x6e;
    public static final byte PUSH16 = 0x6f;

    public static final byte PUSH17 = 0x70;
    public static final byte PUSH18 = 0x71;
    public static final byte PUSH19 = 0x72;
    public static final byte PUSH20 = 0x73;
    public static final byte PUSH21 = 0x74;
    public static final byte PUSH22 = 0x75;
    public static final byte PUSH23 = 0x76;
    public static final byte PUSH24 = 0x77;
    public static final byte PUSH25 = 0x78;
    public static final byte PUSH26 = 0x79;
    public static final byte PUSH27 = 0x7a;
    public static final byte PUSH28 = 0x7b;
    public static final byte PUSH29 = 0x7c;
    public static final byte PUSH30 = 0x7d;
    public static final byte PUSH31 = 0x7e;
    public static final byte PUSH32 = 0x7f;

    public static final byte DUP1 = (byte)0x80;
    public static final byte DUP2 = (byte)0x81;
    public static final byte DUP3 = (byte)0x82;
    public static final byte DUP4 = (byte)0x83;
    public static final byte DUP5 = (byte)0x84;
    public static final byte DUP6 = (byte)0x85;
    public static final byte DUP7 = (byte)0x86;
    public static final byte DUP8 = (byte)0x87;
    public static final byte DUP9 = (byte)0x88;
    public static final byte DUP10 = (byte)0x89;
    public static final byte DUP11 = (byte)0x8a;
    public static final byte DUP12 = (byte)0x8b;
    public static final byte DUP13 = (byte)0x8c;
    public static final byte DUP14 = (byte)0x8d;
    public static final byte DUP15 = (byte)0x8e;
    public static final byte DUP16 = (byte)0x8f;

    public static final byte SWAP1 = (byte)0x90;
    public static final byte SWAP2 = (byte)0x91;
    public static final byte SWAP3 = (byte)0x92;
    public static final byte SWAP4 = (byte)0x93;
    public static final byte SWAP5 = (byte)0x94;
    public static final byte SWAP6 = (byte)0x95;
    public static final byte SWAP7 = (byte)0x96;
    public static final byte SWAP8 = (byte)0x97;
    public static final byte SWAP9 = (byte)0x98;
    public static final byte SWAP10 = (byte)0x99;
    public static final byte SWAP11 = (byte)0x9a;
    public static final byte SWAP12 = (byte)0x9b;
    public static final byte SWAP13 = (byte)0x9c;
    public static final byte SWAP14 = (byte)0x9d;
    public static final byte SWAP15 = (byte)0x9e;
    public static final byte SWAP16 = (byte)0x9f;
}
