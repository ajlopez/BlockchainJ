package com.ajlopez.blockchain.net.messages;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class MessageInputStreamTest {
    @Test
    public void readSimpleBytes() throws IOException {
        byte[] bytes = new byte[] { 0x10, 0x11, 0x12 };
        ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(bytesOutputStream);

        dataOutputStream.writeInt(0x01020304);
        dataOutputStream.writeInt(bytes.length);
        dataOutputStream.write(bytes);

        dataOutputStream.close();

        byte[] message = bytesOutputStream.toByteArray();

        InputStream inputStream = new ByteArrayInputStream(message);
        MessageInputStream messageInputStream = new MessageInputStream(inputStream);

        byte[] result = messageInputStream.readMessage();

        Assert.assertNotNull(result);
        Assert.assertEquals(bytes.length, result.length);
        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void readNullIfInvalidMessage() throws IOException {
        byte[] bytes = new byte[] { 0x10, 0x11, 0x12 };
        ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(bytesOutputStream);

        dataOutputStream.writeInt(0x01020304 + 1);
        dataOutputStream.writeInt(bytes.length);
        dataOutputStream.write(bytes);

        dataOutputStream.close();

        byte[] message = bytesOutputStream.toByteArray();

        InputStream inputStream = new ByteArrayInputStream(message);
        MessageInputStream messageInputStream = new MessageInputStream(inputStream);

        byte[] result = messageInputStream.readMessage();

        Assert.assertNull(result);
    }

    @Test
    public void readNullIfInvalidLength() throws IOException {
        byte[] bytes = new byte[] { 0x10, 0x11, 0x12 };
        ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(bytesOutputStream);

        dataOutputStream.writeInt(0x01020304);
        dataOutputStream.writeInt(bytes.length + 10);
        dataOutputStream.write(bytes);

        dataOutputStream.close();

        byte[] message = bytesOutputStream.toByteArray();

        InputStream inputStream = new ByteArrayInputStream(message);
        MessageInputStream messageInputStream = new MessageInputStream(inputStream);

        byte[] result = messageInputStream.readMessage();

        Assert.assertNull(result);
    }
}
