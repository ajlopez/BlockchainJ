package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.peers.PacketOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * Created by ajlopez on 21/10/2018.
 */
public class PacketOutputStreamTest {
    @Test
    public void writeSimpleBytes() throws IOException {
        byte[] bytes = new byte[] { 0x10, 0x11, 0x12 };

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PacketOutputStream messageOutputStream = new PacketOutputStream(outputStream);

        messageOutputStream.writePacket(new Packet(Protocols.BLOCKCHAIN, (short)1, bytes));

        outputStream.close();

        byte[] result = outputStream.toByteArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(Integer.BYTES + Integer.BYTES + 2 * Short.BYTES + bytes.length, result.length);

        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(result));

        Assert.assertEquals(0x01020304, dataInputStream.readInt());
        Assert.assertEquals(Protocols.BLOCKCHAIN, dataInputStream.readShort());
        Assert.assertEquals(1, dataInputStream.readShort());
        Assert.assertEquals(bytes.length, dataInputStream.readInt());

        byte[] bresult = new byte[bytes.length];

        dataInputStream.read(bresult);

        Assert.assertArrayEquals(bytes, bresult);
    }
}
