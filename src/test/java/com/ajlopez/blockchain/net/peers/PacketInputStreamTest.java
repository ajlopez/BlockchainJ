package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.peers.PacketInputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class PacketInputStreamTest {
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
        PacketInputStream messageInputStream = new PacketInputStream(inputStream);

        byte[] result = messageInputStream.readPacket();

        Assert.assertNotNull(result);
        Assert.assertEquals(bytes.length, result.length);
        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void readNullIfInvalidPacket() throws IOException {
        byte[] bytes = new byte[] { 0x10, 0x11, 0x12 };
        ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(bytesOutputStream);

        dataOutputStream.writeInt(0x01020304 + 1);
        dataOutputStream.writeInt(bytes.length);
        dataOutputStream.write(bytes);

        dataOutputStream.close();

        byte[] message = bytesOutputStream.toByteArray();

        InputStream inputStream = new ByteArrayInputStream(message);
        PacketInputStream messageInputStream = new PacketInputStream(inputStream);

        byte[] result = messageInputStream.readPacket();

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
        PacketInputStream messageInputStream = new PacketInputStream(inputStream);

        byte[] result = messageInputStream.readPacket();

        Assert.assertNull(result);
    }
}
