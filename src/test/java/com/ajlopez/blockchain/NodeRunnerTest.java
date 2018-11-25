package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class NodeRunnerTest {
    @Test
    public void mineBlock() throws InterruptedException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        Semaphore semaphore = new Semaphore(0, true);

        blockChain.onBlock(blk -> {
            semaphore.release();
        });

        NodeRunner runner = new NodeRunner(blockChain, true);

        runner.start();

        semaphore.acquire();

        runner.stop();

        Block bestBlock = blockChain.getBestBlock();

        Assert.assertNotNull(bestBlock);
        Assert.assertTrue(bestBlock.getNumber() > 0);
    }
}
