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
    SLOAD(50),
    SSET(20000),
    SRESET(5000),
    SCLEAR(15000),
    TRANSFER(21000);

    private long value;

    private FeeSchedule(long value) {
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }
}
