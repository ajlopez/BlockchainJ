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
}
