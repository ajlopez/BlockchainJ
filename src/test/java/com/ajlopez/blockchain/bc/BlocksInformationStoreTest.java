package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 21/03/2020.
 */
public class BlocksInformationStoreTest {
    @Test
    public void getUnknownHeight() throws IOException {
        BlocksInformationStore blocksInformationStore = new BlocksInformationStore(new HashMapStore());

        Assert.assertNull(blocksInformationStore.get(42));
    }

    @Test
    public void getInitialBestHeight() throws IOException {
        BlocksInformationStore blocksInformationStore = new BlocksInformationStore(new HashMapStore());

        Assert.assertEquals(-1, blocksInformationStore.getBestHeight());
    }


    @Test
    public void putAndGetInitialBestHeight() throws IOException {
        BlocksInformationStore blocksInformationStore = new BlocksInformationStore(new HashMapStore());

        blocksInformationStore.putBestHeight(42);
        Assert.assertEquals(42, blocksInformationStore.getBestHeight());
    }
    
    @Test
    public void putAndGetHeight() throws IOException {
        BlocksInformationStore blocksInformationStore = new BlocksInformationStore(new HashMapStore());

        BlockHash blockHash = FactoryHelper.createRandomBlockHash();
        Difficulty totalDifficulty = Difficulty.fromUnsignedLong(42);
        BlockHash blockHash2 = FactoryHelper.createRandomBlockHash();
        Difficulty totalDifficulty2 = Difficulty.fromUnsignedLong(100);

        BlocksInformation blocksInformation = new BlocksInformation();

        blocksInformation.addBlockInformation(blockHash, totalDifficulty);
        blocksInformation.addBlockInformation(blockHash2, totalDifficulty2);

        blocksInformation.setBlockOnChain(blockHash);

        blocksInformationStore.put(42, blocksInformation);

        BlocksInformation result = blocksInformationStore.get(42);

        Assert.assertNotNull(result);
        Assert.assertEquals(blocksInformation.getBlockOnChainPosition(), result.getBlockOnChainPosition());
        Assert.assertEquals(blocksInformation.getBlockInformationList().size(), result.getBlockInformationList().size());
        Assert.assertEquals(blocksInformation.getBlockInformationList().get(0).getBlockHash(), result.getBlockInformationList().get(0).getBlockHash());
        Assert.assertEquals(blocksInformation.getBlockInformationList().get(0).getTotalDifficulty(), result.getBlockInformationList().get(0).getTotalDifficulty());
        Assert.assertEquals(blocksInformation.getBlockInformationList().get(1).getBlockHash(), result.getBlockInformationList().get(1).getBlockHash());
        Assert.assertEquals(blocksInformation.getBlockInformationList().get(1).getTotalDifficulty(), result.getBlockInformationList().get(1).getTotalDifficulty());
    }
}
