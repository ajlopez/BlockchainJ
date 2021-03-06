package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.bc.BlockUtils;
import com.ajlopez.blockchain.config.MinerConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.TransactionReceipt;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.BlockHeaderEncoder;
import com.ajlopez.blockchain.execution.*;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.utils.HashUtils;
import com.ajlopez.blockchain.vms.eth.BlockData;
import com.ajlopez.blockchain.vms.eth.ExecutionResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessor {
    private static final Random random = new Random();

    private final BlockChain blockChain;
    private final TransactionPool transactionPool;
    private final List<Consumer<Block>> minedBlockConsumers = new ArrayList<>();
    private final Stores stores;
    private final MinerConfiguration minerConfiguration;

    private boolean stopped = false;

    public MinerProcessor(BlockChain blockChain, TransactionPool transactionPool, Stores stores, MinerConfiguration minerConfiguration) {
        this.blockChain = blockChain;
        this.transactionPool = transactionPool;
        this.stores = stores;
        this.minerConfiguration = minerConfiguration;
    }

    public Block process() throws IOException {
        Block bestBlock = this.blockChain.getBestBlockInformation().getBlock();

        Block newBlock = this.mineBlock(bestBlock);

        emitMinedBlock(newBlock);

        return newBlock;
    }

    public void onMinedBlock(Consumer<Block> consumer) {
        this.minedBlockConsumers.add(consumer);
    }

    public Block mineBlock(Block parent) throws IOException {
        Block block = buildNewBlock(parent);

        return calculateProofOfWork(block);
    }

    private Block buildNewBlock(Block parent) throws IOException {
        Hash parentStateRootHash = parent.getHeader().getStateRootHash();
        AccountStore accountStore = this.stores.getAccountStoreProvider().retrieve(parentStateRootHash);
        ExecutionContext executionContext = new TopExecutionContext(accountStore, this.stores.getTrieStorageProvider(), this.stores.getCodeStore());

        // TODO evaluate to use BlockExecutor instead of TransactionExecutor
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);
        long timestamp = System.currentTimeMillis();

        // TODO adjust difficulty
        BlockData blockData = new BlockData(parent.getNumber() + 1, timestamp, this.minerConfiguration.getCoinbase(), parent.getDifficulty(), this.minerConfiguration.getGasLimit(), 0);

        List<Transaction> transactions = this.transactionPool.getTransactions();
        List<Transaction> executedTransactions = new ArrayList<>();
        List<TransactionReceipt> executedTransactionReceipts = new ArrayList<>();

        long gasUsed = 0L;

        for (Transaction transaction : transactions) {
            if (gasUsed + transaction.getGas() > this.minerConfiguration.getGasLimit())
                break;

            ExecutionResult executionResult = transactionExecutor.executeTransaction(transaction, blockData);

            if (executionResult == null)
                continue;

            executedTransactions.add(transaction);

            TransactionReceipt transactionReceipt = new TransactionReceipt(executionResult.getGasUsed(), executionResult.wasSuccesful(), executionResult.getLogs());

            executedTransactionReceipts.add(transactionReceipt);
            gasUsed += transactionReceipt.getGasUsed();
        }

        executionContext.commit();

        List<BlockHeader> uncles = BlockUtils.getCandidateUncles(parent.getHash(), 10, stores.getBlockStore(), stores.getBlocksInformationStore()).stream().collect(Collectors.toList());

        // TODO select best uncles
        if (uncles.size() > this.minerConfiguration.getNoUncles())
            uncles = uncles.subList(0, this.minerConfiguration.getNoUncles());

        // TODO any adjust in gas limit?
        // TODO use extraData
        return new Block(parent, uncles, executedTransactions, BlockExecutionResult.calculateTransactionReceiptsHash(executedTransactionReceipts), accountStore.getRootHash(), System.currentTimeMillis() / 1000, this.minerConfiguration.getCoinbase(), parent.getDifficulty(), this.minerConfiguration.getGasLimit(), gasUsed, null, 0);
    }

    private Block calculateProofOfWork(Block block) {
        if (block.getDifficulty().compareTo(Difficulty.ONE) <= 0)
            return block;

        BlockHeader blockHeader = block.getHeader();
        byte[] encodedHeader = BlockHeaderEncoder.encode(blockHeader);

        DataWord target = block.getDifficulty().toTarget();

        // TODO improve exit
        while (true) {
            int nbit = random.nextInt(Long.BYTES * 8);
            int nbyte = nbit / 8;
            encodedHeader[encodedHeader.length - Long.BYTES + nbyte] ^= 1 << (nbit % 8);

            Hash hash = HashUtils.calculateHash(encodedHeader);

            if (target.compareTo(hash) >= 0)
                break;
        }

        // TODO use uncles
        return new Block(BlockHeaderEncoder.decode(encodedHeader), null, block.getTransactions());
    }

    public void start() {
        new Thread(this::mineProcess).start();
    }

    public void stop() {
        this.stopped = true;
    }

    public void mineProcess() {
        while (!this.stopped) {
            try {
                this.process();
                // TODO sleep period by configuration
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void emitMinedBlock(Block block) {
        this.minedBlockConsumers.forEach(a -> a.accept(block));
    }
}
