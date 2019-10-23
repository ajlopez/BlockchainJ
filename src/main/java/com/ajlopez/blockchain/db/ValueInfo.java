package com.ajlopez.blockchain.db;

/**
 * Created by ajlopez on 20/10/2019.
 */
public class ValueInfo {
    public final long position;
    public final int length;

    public ValueInfo(long position, int length) {
        this.position = position;
        this.length = length;
    }
}
