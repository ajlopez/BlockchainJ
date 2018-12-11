package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 11/12/2018.
 */
public class MemoryTest {
    @Test
    public void initialSizeIsZero() {
        Memory memory = new Memory();

        Assert.assertEquals(0, memory.size());
    }

    @Test
    public void getUndefinedValueAsZero() {
        Memory memory = new Memory();

        Assert.assertEquals(DataWord.ZERO, memory.getValue(42));
    }

    @Test
    public void setAndGetValue() {
        Memory memory = new Memory();

        memory.setValue(144, DataWord.fromUnsignedInteger(42));

        Assert.assertEquals(144 + DataWord.DATAWORD_BYTES, memory.size());

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), memory.getValue(144));
    }

    @Test
    public void setAndGetTwoValues() {
        Memory memory = new Memory();

        memory.setValue(144, DataWord.fromUnsignedInteger(42));
        memory.setValue(200, DataWord.fromUnsignedInteger(100));

        Assert.assertEquals(200 + DataWord.DATAWORD_BYTES, memory.size());

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), memory.getValue(144));
        Assert.assertEquals(DataWord.fromUnsignedInteger(100), memory.getValue(200));
    }
}
