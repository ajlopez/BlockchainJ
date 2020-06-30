package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.utils.ByteUtils;

/**
 * Created by ajlopez on 31/08/2017.
 */
public class Address extends AbstractBytesValue {
    public static final int ADDRESS_BYTES = 20;
    public static final Address ADDRESS_RICH_TRANSACTION = new Address(new byte[] { 0x01, 0x00, 0x00, 0x00, 0x01 });

    public static final Address ZERO = new Address(new byte[0]);

    public Address(byte[] bytes) {
        super(bytes, ADDRESS_BYTES);
    }

    public boolean isZero() {
        return ByteUtils.areZero(this.getBytes());
    }

    public static Address normalizeToNull(Address address) {
        if (address != null && address.isZero())
            return null;

        return address;
    }
}
