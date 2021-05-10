package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.*;
import com.ajlopez.blockchain.config.ArgumentsProcessor;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.config.NodeConfiguration;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.config.MinerConfiguration;
import com.ajlopez.blockchain.net.messages.BlockMessage;
import com.ajlopez.blockchain.processors.MinerProcessor;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.MemoryKeyValueStores;
import com.ajlopez.blockchain.utils.HexUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by ajlopez on 24/11/2018.
 */
public class Start {
    private static NodeRunner nodeRunner;

    public static void main(String[] args) throws IOException {
        ObjectContext objectContext = new ObjectContext(new MemoryKeyValueStores());

        Wallet wallet = createWallet(objectContext);

        ArgumentsProcessor argsproc = processArguments(args);

        NetworkConfiguration networkConfiguration = new NetworkConfiguration((short)1);

        int port = argsproc.getInteger("port");
        List<String> peers = argsproc.getStringList("peers");

        launchNodeRunner(objectContext, port, peers, networkConfiguration);

        boolean isMiner = argsproc.getBoolean("miner");

        if (isMiner) {
            String coinbaseText = argsproc.getString("coinbase");
            Address coinbase = coinbaseText.isEmpty() ? Address.ZERO : new Address(HexUtils.hexStringToBytes(coinbaseText));

            MinerConfiguration minerConfiguration = new MinerConfiguration(isMiner, coinbase, 12_000_000L, 10);

            launchMinerProcessor(objectContext, minerConfiguration);
        }

        boolean rpc = argsproc.getBoolean("rpc");

        if (rpc) {
            int rpcport = argsproc.getInteger("rpcport");
            launchRpcServer(objectContext, wallet, networkConfiguration, rpcport);
        }
    }

    private static Wallet createWallet(ObjectContext objectContext) throws IOException {
        AccountStore accountStore = objectContext.getStores().getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);
        WalletCreator walletCreator = new WalletCreator(accountStore);
        DataWord oneMillion = DataWord.fromUnsignedLong(1_000_000L);
        Coin balance = Coin.fromBytes(oneMillion.mul(oneMillion).mul(oneMillion).mul(DataWord.fromUnsignedInteger(100)).getBytes());
        Wallet wallet = walletCreator.createWallet(10, balance);
        accountStore.save();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        objectContext.getBlockChain().connectBlock(genesis);

        return wallet;
    }

    private static void launchRpcServer(ObjectContext objectContext, Wallet wallet, NetworkConfiguration networkConfiguration, int rpcport) {
        RpcRunner rpcrunner = new RpcRunner(objectContext.getBlockChain(), rpcport, objectContext.getStores().getAccountStoreProvider(), objectContext.getTransactionPool(), networkConfiguration, wallet);

        rpcrunner.start();

        Runtime.getRuntime().addShutdownHook(new Thread(rpcrunner::stop));
    }

    private static void launchNodeRunner(ObjectContext objectContext, int port, List<String> peers, NetworkConfiguration networkConfiguration) throws IOException {
        nodeRunner = new NodeRunner(new NodeConfiguration(port, peers), networkConfiguration, objectContext);
        nodeRunner.onNewBlock(Start::printBlock);

        nodeRunner.start();

        Runtime.getRuntime().addShutdownHook(new Thread(runner::stop));
    }

    private static void launchMinerProcessor(ObjectContext objectContext, MinerConfiguration minerConfiguration) throws IOException {
        MinerProcessor minerProcessor = new MinerProcessor(objectContext.getBlockChain(), objectContext.getTransactionPool(), objectContext.getStores(), minerConfiguration);
        minerProcessor.onMinedBlock(blk -> {
            nodeRunner.getNodeProcessor().postMessage(nodeRunner.getNodeProcessor().getPeer(), new BlockMessage(blk));
        });

        minerProcessor.start();

        Runtime.getRuntime().addShutdownHook(new Thread(minerProcessor::stop));
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

        for (Transaction transaction: block.getTransactions())
            System.out.println(String.format("With transaction%s", transaction.getHash()));
    }
}
