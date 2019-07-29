package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.messages.GetBlockByHashMessage;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.MessageEncoder;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by ajlopez on 18/11/2018.
 */
public class MessageOutputStreamTest {
    @Test
    public void writeSimpleMessage() throws IOException {
        Message message = new GetBlockByHashMessage(FactoryHelper.createRandomBlockHash());
        byte[] bytes = MessageEncoder.encode(message);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PacketOutputStream packetOutputStream = new PacketOutputStream(outputStream);
        MessageOutputStream messageOutputStream = new MessageOutputStream((short)1, packetOutputStream);

        messageOutputStream.writeMessage(null, message);

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
