package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.messages.GetBlockByHashMessage;
import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.MessageEncoder;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class MessageInputStreamTest {
    @Test
    public void readMessage() throws IOException {
        Message message = new GetBlockByHashMessage(HashUtilsTest.generateRandomHash());
        byte[] bytes = MessageEncoder.encode(message);
        ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(bytesOutputStream);

        dataOutputStream.writeInt(0x01020304);
        dataOutputStream.writeInt(bytes.length);
        dataOutputStream.write(bytes);

        dataOutputStream.close();

        byte[] packet = bytesOutputStream.toByteArray();

        InputStream inputStream = new ByteArrayInputStream(packet);
        PacketInputStream packetInputStream = new PacketInputStream(inputStream);
        MessageInputStream messageInputStream = new MessageInputStream(packetInputStream);

        Message result = messageInputStream.readMessage();

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(bytes, MessageEncoder.encode(result));
    }
}
