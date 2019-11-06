package com.ajlopez.blockchain.test.utils;

import com.ajlopez.blockchain.bc.BlockValidator;
import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.*;
import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.execution.BlockExecutor;
import com.ajlopez.blockchain.execution.ExecutionContext;
import com.ajlopez.blockchain.execution.TopExecutionContext;
import com.ajlopez.blockchain.execution.TransactionExecutor;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.processors.*;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.utils.HashUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ajlopez on 26/01/2018.
 */
public class FactoryHelper {
    private static Random random = new Random();

    public static byte[] createRandomBytes(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);

        return bytes;
    }

    public static DataWord createRandomDataWord() {
        return new DataWord(createRandomBytes(DataWord.DATAWORD_BYTES));
    }

    public static Hash createRandomHash() {
        return new Hash(createRandomBytes(BlockHash.HASH_BYTES));
    }

    public static BlockHash createRandomBlockHash() {
        return new BlockHash(createRandomBytes(BlockHash.HASH_BYTES));
    }

    public static Address createRandomAddress() {
        return new Address(createRandomBytes(Address.ADDRESS_BYTES));
    }

    public static Address createAccountWithBalance(AccountStore accountStore, long balance) throws IOException {
        Address address = createRandomAddress();

        createAccountWithBalance(accountStore, address, balance);

        return address;
    }

    public static void createAccountWithBalance(AccountStore accountStore, Address address, long balance) throws IOException {
        Account account = new Account(Coin.fromUnsignedLong(balance), 0, null, null);

        accountStore.putAccount(address, account);
        accountStore.save();
    }

    public static void createAccountWithCode(AccountStore accountStore, CodeStore codeStore, Address address, byte[] code) throws IOException {
        Hash codeHash = HashUtils.calculateHash(code);
        codeStore.putCode(codeHash, code);
        Account account = new Account(Coin.ZERO, 0, codeHash, null);

        accountStore.putAccount(address, account);
        accountStore.save();
    }

    public static List<Address> createRandomAddresses(int n) {
        List<Address> addresses = new ArrayList<>(n);

        for (int k = 0; k < n; k++)
            addresses.add(createRandomAddress());

        return addresses;
    }

    public static Transaction createTransaction(int value) {
        Address sender = createRandomAddress();

        return createTransaction(value, sender, 0);
    }

    public static Transaction createTransaction(int value, long nonce, byte[] data) {
        Address sender = createRandomAddress();
        Address receiver = createRandomAddress();
        Coin cvalue = Coin.fromUnsignedLong(value);

        return new Transaction(sender, receiver, cvalue, nonce, data, 6000000, Coin.ZERO);
    }

    public static Transaction createTransaction(int value, long nonce, byte[] data, long gas, long gasPrice) {
        Address sender = createRandomAddress();
        Address receiver = createRandomAddress();
        Coin cvalue = Coin.fromUnsignedLong(value);

        return new Transaction(sender, receiver, cvalue, nonce, data, gas, Coin.fromUnsignedLong(gasPrice));
    }

    public static Transaction createTransaction(int value, Address sender, long nonce) {
        Address receiver = createRandomAddress();
        Coin cvalue = Coin.fromUnsignedLong(value);

        return new Transaction(sender, receiver, cvalue, nonce, null, 6000000, Coin.ZERO);
    }

    public static List<Transaction> createTransactions(int ntransactions) {
        List<Transaction> transactions = new ArrayList<>();

        for (int k = 0; k < ntransactions; k++)
            transactions.add(createTransaction(random.nextInt(10000)));

        return transactions;
    }

    public static List<Transaction> createTransactions(int ntransactions, Address sender, long nonce) {
        List<Transaction> transactions = new ArrayList<>();

        for (int k = 0; k < ntransactions; k++)
            transactions.add(createTransaction(random.nextInt(10000), sender, nonce++));

        return transactions;
    }

    public static void extendBlockChainWithBlocks(BlockChain blockChain, int nblocks) {
        extendBlockChainWithBlocks(blockChain, nblocks, 0);
    }

    public static void extendBlockChainWithBlocks(BlockChain blockChain, int nblocks, int ntransactions) {
        Block block = blockChain.getBestBlock();
        Address coinbase = FactoryHelper.createRandomAddress();

        for (int k = 0; k < nblocks; k++) {
            Block newBlock = createBlock(block, coinbase, ntransactions);
            blockChain.connectBlock(newBlock);
            block = newBlock;
        }
    }

    public static void extendBlockChainWithBlocks(AccountStoreProvider accountStoreProvider, BlockChain blockChain, int nblocks, int ntransactions, Address sender, long nonce) throws IOException {
        extendBlockChainWithBlocksFromBlock(accountStoreProvider, blockChain, blockChain.getBestBlock(), nblocks, ntransactions, sender, nonce);
    }

    public static void extendBlockChainWithBlocksFromBlock(AccountStoreProvider accountStoreProvider, BlockChain blockChain, Block fromBlock, int nblocks, int ntransactions, Address sender, long nonce) throws IOException {
        Block block = fromBlock;
        Address coinbase = FactoryHelper.createRandomAddress();

        for (int k = 0; k < nblocks; k++) {
            Block newBlock = createBlock(accountStoreProvider, block, coinbase, ntransactions, sender, nonce);
            blockChain.connectBlock(newBlock);
            block = newBlock;
            nonce += ntransactions;
        }
    }

    public static Block createBlock(Block parent, Address coinbase, int ntransactions) {
        List<Transaction> transactions = createTransactions(ntransactions);

        return createBlock(parent, coinbase, transactions);
    }

    public static Block createBlock(AccountStoreProvider accountStoreProvider, Block parent, Address coinbase, int ntransactions, Address sender, long nonce) throws IOException {
        List<Transaction> transactions = createTransactions(ntransactions, sender, nonce);

        return createBlock(accountStoreProvider, parent, coinbase, transactions);
    }

    public static Block createBlock(AccountStoreProvider accountStoreProvider, Block parent, Address coinbase, List<Transaction> transactions) throws IOException {
        AccountStore accountStore = accountStoreProvider.retrieve(parent.getStateRootHash());

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(transactions, null);

        return new Block(parent.getNumber() + 1, parent.getHash(), transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
    }

    public static Block createBlock(Block parent, Address coinbase, List<Transaction> transactions) {
        return new Block(parent.getNumber() + 1, parent.getHash(), transactions, parent.getStateRootHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
    }

    public static BlockChain createBlockChain(int size) throws IOException {
        return createBlockChain(size, 0);
    }

    public static BlockChain createBlockChain(int size, int ntransactions) throws IOException {
        TrieStore trieStore = new TrieStore(new HashMapStore());

        return createBlockChain(trieStore, size, ntransactions);
    }

    public static BlockChain createBlockChain(TrieStore trieStore, int size, int ntransactions) throws IOException {
        Address senderAddress = FactoryHelper.createRandomAddress();

        return createBlockChainWithAccount(senderAddress, 1000000, trieStore, size, ntransactions);
    }

    public static BlockChain createBlockChainWithAccount(Address senderAddress, long balance, TrieStore trieStore, int size, int ntransactions) throws IOException {
        Account sender = new Account(Coin.fromUnsignedLong(balance), 0, null, null);

        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = accountStoreProvider.retrieve(Trie.EMPTY_TRIE_HASH);

        accountStore.putAccount(senderAddress, sender);
        accountStore.save();

        BlockChain blockChain = createBlockChainWithGenesis(accountStore);
        extendBlockChainWithBlocks(accountStoreProvider, blockChain, size, ntransactions, senderAddress, 0);

        return blockChain;
    }

    public static BlockChain createBlockChainWithGenesis() {
        return createBlockChainWithGenesis(GenesisGenerator.generateGenesis());
    }

    public static BlockChain createBlockChainWithGenesis(AccountStore accountStore) {
        return createBlockChainWithGenesis(GenesisGenerator.generateGenesis(accountStore));
    }

    public static BlockChain createBlockChainWithGenesis(Block genesis) {
        BlockChain blockChain = new BlockChain();
        blockChain.connectBlock(genesis);

        return blockChain;
    }

    public static BlockProcessor createBlockProcessor() {
        return createBlockProcessor(new BlockChain());
    }

    public static BlockProcessor createBlockProcessor(BlockChain blockChain) {
        return createBlockProcessor(blockChain, new TransactionPool());
    }

    public static BlockProcessor createBlockProcessor(BlockChain blockChain, TransactionPool transactionPool) {
        return new BlockProcessor(blockChain, new OrphanBlocks(), createBlockValidator(new AccountStoreProvider(new TrieStore(new HashMapStore()))), transactionPool);
    }

    public static BlockValidator createBlockValidator(AccountStoreProvider accountStoreProvider) {
        return new BlockValidator(new BlockExecutor(accountStoreProvider, null, null));
    }

    public static MessageProcessor createMessageProcessor(BlockProcessor blockProcessor) {
        return new MessageProcessor(blockProcessor, null, null, null, null);
    }

    public static MessageProcessor createMessageProcessor(BlockProcessor blockProcessor, PeerProcessor peerProcessor, SendProcessor outputProcessor) {
        return new MessageProcessor(blockProcessor, null, peerProcessor, outputProcessor, null);
    }

    public static MessageProcessor createMessageProcessor(BlockProcessor blockProcessor, SendProcessor outputProcessor) {
        return new MessageProcessor(blockProcessor, null, null, outputProcessor, null);
    }

    public static MessageProcessor createMessageProcessor(TransactionProcessor transactionProcessor) {
        return new MessageProcessor(null, transactionProcessor, null, null, null);
    }

    public static MessageProcessor createMessageProcessor(TransactionProcessor transactionProcessor, SendProcessor outputProcessor) {
        return new MessageProcessor(null, transactionProcessor, null, outputProcessor, null);
    }

    public static Peer createRandomPeer() {
        return new Peer(createRandomPeerId());
    }

    public static PeerId createRandomPeerId() {
        return new PeerId(createRandomBytes(Hash.HASH_BYTES));
    }

    public static NodeProcessor createNodeProcessor() {
        return createNodeProcessor(new BlockChain());
    }

    public static NodeProcessor createNodeProcessor(BlockChain blockChain) {
        return new NodeProcessor(new NetworkConfiguration((short)42), createRandomPeer(), blockChain, new AccountStoreProvider(new TrieStore(new HashMapStore())), null, new CodeStore(new HashMapStore()), createRandomAddress());
    }

    public static List<Block> createBlocks(int nblocks) {
        Address coinbase = createRandomAddress();
        List<Block> blocks = new ArrayList<>();

        Block block = new Block(0, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        blocks.add(block);

        for (int k = 0; k < nblocks; k++) {
            block = new Block(block.getNumber() + 1, block.getHash(), Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
            blocks.add(block);
        }

        return blocks;
    }
}
