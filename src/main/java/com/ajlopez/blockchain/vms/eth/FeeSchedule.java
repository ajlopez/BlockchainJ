package com.ajlopez.blockchain.vms.eth;

/**
 * Created by ajlopez on 05/01/2019.
 */
public enum FeeSchedule {
    ZERO(0),
    BASE(2),
    VERYLOW(3),
    LOW(5),
    MID(8),
    HIGH(10),
    EXT(20),
    SLOAD(50);

    private int value;

    private FeeSchedule(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
