package com.ajlopez.blockchain.net.peers;

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
        dataOutputStream.writeShort(1);
        dataOutputStream.writeShort(42);
        dataOutputStream.writeInt(bytes.length);
        dataOutputStream.write(bytes);

        dataOutputStream.close();

        byte[] message = bytesOutputStream.toByteArray();

        InputStream inputStream = new ByteArrayInputStream(message);
        PacketInputStream messageInputStream = new PacketInputStream(inputStream);

        Packet packet = messageInputStream.readPacket();

        Assert.assertNotNull(packet);
        Assert.assertEquals(Protocols.BLOCKCHAIN, packet.getProtocol());
        Assert.assertEquals(42, packet.getNetwork());

        byte[] result = packet.getBytes();

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

        Packet result = messageInputStream.readPacket();

        Assert.assertNull(result);
        Assert.assertTrue(messageInputStream.isClosed());
    }

    @Test
    public void readNullIfInvalidLength() throws IOException {
        byte[] bytes = new byte[] { 0x10, 0x11, 0x12 };
        ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(bytesOutputStream);

        dataOutputStream.writeInt(0x01020304);
        dataOutputStream.writeShort(Protocols.BLOCKCHAIN);
        dataOutputStream.writeShort(42);
        dataOutputStream.writeInt(bytes.length + 10);
        dataOutputStream.write(bytes);

        dataOutputStream.close();

        byte[] message = bytesOutputStream.toByteArray();

        InputStream inputStream = new ByteArrayInputStream(message);
        PacketInputStream messageInputStream = new PacketInputStream(inputStream);

        Packet result = messageInputStream.readPacket();

        Assert.assertNull(result);
        Assert.assertTrue(messageInputStream.isClosed());
    }
}
