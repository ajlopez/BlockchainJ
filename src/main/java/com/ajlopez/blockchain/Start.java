package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.bc.Wallet;
import com.ajlopez.blockchain.bc.WalletCreator;
import com.ajlopez.blockchain.config.ArgumentsProcessor;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.KeyValueStores;
import com.ajlopez.blockchain.store.MemoryKeyValueStores;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.utils.HexUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by ajlopez on 24/11/2018.
 */
public class Start {
    public static void main(String[] args) throws IOException {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();
        Stores stores = new Stores(keyValueStores);

        AccountStore accountStore = stores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);
        WalletCreator walletCreator = new WalletCreator(accountStore);
        DataWord oneMillion = DataWord.fromUnsignedLong(1_000_000L);
        Coin balance = Coin.fromBytes(oneMillion.mul(oneMillion).mul(oneMillion).mul(DataWord.fromUnsignedInteger(100)).getBytes());
        Wallet wallet = walletCreator.createWallet(10, balance);
        accountStore.save();

        BlockChain blockChain = new BlockChain(stores);
        TransactionPool transactionPool = new TransactionPool();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        blockChain.connectBlock(genesis);

        ArgumentsProcessor argsproc = processArguments(args);

        String coinbaseText = argsproc.getString("coinbase");
        Address coinbase = coinbaseText.isEmpty() ? Address.ZERO : new Address(HexUtils.hexStringToBytes(coinbaseText));
        boolean isMiner = argsproc.getBoolean("miner");
        int port = argsproc.getInteger("port");
        List<String> peers = argsproc.getStringList("peers");

        NetworkConfiguration networkConfiguration = new NetworkConfiguration((short)1);
        NodeRunner runner = new NodeRunner(isMiner, port, peers, coinbase, networkConfiguration, keyValueStores, transactionPool);
        runner.onNewBlock(Start::printBlock);

        runner.start();

        Runtime.getRuntime().addShutdownHook(new Thread(runner::stop));

        boolean rpc = argsproc.getBoolean("rpc");

        if (rpc) {
            int rpcport = argsproc.getInteger("rpcport");

            RpcRunner rpcrunner = new RpcRunner(blockChain, rpcport, stores.getAccountStoreProvider(), transactionPool, networkConfiguration, wallet);

            rpcrunner.start();

            Runtime.getRuntime().addShutdownHook(new Thread(rpcrunner::stop));
        }
    }

    public static ArgumentsProcessor processArguments(String[] args) {
        ArgumentsProcessor processor = new ArgumentsProcessor();

        processor.defineInteger("p", "port", 0);

        processor.defineBoolean("r", "rpc", false);
        processor.defineInteger("rp", "rpcport", 4445);

        processor.defineStringList("ps", "peers", "");

        processor.defineBoolean("m", "miner", false);
        processor.defineString("k", "coinbase", "");

        processor.processArguments(args);

        return processor;
    }

    public static void printBlock(Block block) {
        System.out.println(String.format("Connecting block %d %s", block.getNumber(), block.getHash()));
    }
}
