package com.ajlopez.blockchain.net.messages;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * Created by ajlopez on 21/10/2018.
 */
public class MessageOutputStreamTest {
    @Test
    public void writeSimpleBytes() throws IOException {
        byte[] bytes = new byte[] { 0x10, 0x11, 0x12 };

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessageOutputStream messageOutputStream = new MessageOutputStream(outputStream);

        messageOutputStream.writeMessage(bytes);

        outputStream.close();

        byte[] result = outputStream.toByteArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(Integer.BYTES + Integer.BYTES + bytes.length, result.length);

        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(result));

        Assert.assertEquals(0x01020304, dataInputStream.readInt());
        Assert.assertEquals(bytes.length, dataInputStream.readInt());

        byte[] bresult = new byte[bytes.length];

        dataInputStream.read(bresult);

        Assert.assertArrayEquals(bytes, bresult);
    }
}
