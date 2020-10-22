package com.ajlopez.blockchain.tools;

import com.ajlopez.blockchain.config.ArgumentsProcessor;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.state.TrieNodeCounterVisitor;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Angel on 19/04/2020.
 */
public class TrieMemoryPerformance {
    private static final Random random = new Random();

    public static void main(String[] args) throws IOException {
        ArgumentsProcessor argumentsProcessor = new ArgumentsProcessor();

        argumentsProcessor.defineBoolean("c", "csv", false);

        argumentsProcessor.processArguments(args);

        int noValues = argumentsProcessor.getInteger(0);
        int keySize = argumentsProcessor.getInteger(1);
        int valueSize = argumentsProcessor.getInteger(2);

        boolean dumpCSV = argumentsProcessor.getBoolean("csv");

        CollectedData collectedData = new CollectedData();

        collectedData.noValues = noValues;
        collectedData.keySize = keySize;
        collectedData.valueSize = valueSize;

        Trie trie = new Trie();

        collectedData.megaBytesBefore = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

        long millis = System.currentTimeMillis();

        for (int k = 0; k < noValues; k++) {
            byte[] key = createRandomBytes(keySize);
            byte[] value = createRandomBytes(valueSize);

            trie = trie.put(key, value);
        }

        collectedData.creationTime = System.currentTimeMillis() - millis;

        TrieNodeCounterVisitor visitor = new TrieNodeCounterVisitor();
        visitor.process(trie);

        collectedData.trieSize = visitor.getNodeCounter();

        collectedData.megaBytesAfter = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

        System.gc();

        collectedData.megaBytesAfterGC = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

        long millis2 = System.currentTimeMillis();

        trie.getHash();

        collectedData.hashTime = System.currentTimeMillis() - millis2;

        if (dumpCSV)
            dumpCollectedDataAsCsv(collectedData);
        else
            dumpCollectedData(collectedData);
    }

    private static void dumpCollectedData(CollectedData collectedData) {
        System.out.println("No values: " + collectedData.noValues);
        System.out.println("Key size: " + collectedData.keySize);
        System.out.println("Value size: " + collectedData.valueSize);

        System.out.println("MB before: " + collectedData.megaBytesBefore);
        System.out.println("MB after: " + collectedData.megaBytesAfter);
        System.out.println("MB after GC: " + collectedData.megaBytesAfterGC);

        System.out.println("Creation time (ms): " + collectedData.creationTime);
        System.out.println("Hash time (ms): " + collectedData.hashTime);

        System.out.println("Trie size (nodes): " + collectedData.trieSize);
    }

    private static void dumpCollectedDataAsCsv(CollectedData collectedData) {
        System.out.println("" + collectedData.noValues + "," +
                collectedData.keySize + "," +
                collectedData.valueSize + "," +
                collectedData.megaBytesBefore + "," +
                collectedData.megaBytesAfter + "," +
                collectedData.megaBytesAfterGC + "," +
                collectedData.creationTime + "," +
                collectedData.hashTime + "," +
                collectedData.trieSize);
    }

    private static byte[] createRandomBytes(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);

        return bytes;
    }

    private static class CollectedData {
        public int noValues;
        public int keySize;
        public int valueSize;

        public double megaBytesBefore;
        public double megaBytesAfter;
        public double megaBytesAfterGC;

        public long creationTime;
        public long hashTime;

        public int trieSize;
    }
}
