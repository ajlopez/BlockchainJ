package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.messages.MessageType;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.processors.SendProcessor;
import com.ajlopez.blockchain.test.simples.SimpleMessageChannel;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by ajlopez on 03/05/2020.
 */
public class RemoteKeyValueStoreTest {
    @Test
    public void getValue() throws IOException {
        SendProcessor sendProcessor = new SendProcessor(FactoryHelper.createRandomPeer());
        Peer receiver = FactoryHelper.createRandomPeer();
        SimpleMessageChannel channel = new SimpleMessageChannel();
        sendProcessor.connectToPeer(receiver, channel);

        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        KeyValueResolver keyValueResolver = new KeyValueResolver() {
            @Override
            public void resolve(KeyValueStoreType storeType, byte[] key, CompletableFuture<byte[]> future) {
                new Thread(() -> { future.complete(value); }).start();
            }
        };

        RemoteKeyValueStore delayedKeyValueStore = new RemoteKeyValueStore(KeyValueStoreType.BLOCKS, sendProcessor, keyValueResolver);

        byte[] result = delayedKeyValueStore.getValue(key);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(value, result);

        Message msg = channel.getPeerMessages().get(0).getValue();

        Assert.assertNotNull(msg);
        Assert.assertEquals(MessageType.GET_STORED_VALUE, msg.getMessageType());
    }
}
