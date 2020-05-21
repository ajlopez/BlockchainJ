package com.ajlopez.blockchain.tools;

import com.ajlopez.blockchain.config.ArgumentsProcessor;
import com.ajlopez.blockchain.core.types.Bloom;
import com.ajlopez.blockchain.encoding.BloomEncoder;
import com.ajlopez.blockchain.state.Trie;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Angel on 26/04/2020.
 */
public class BloomEncodingMemory {
    private static final Random random = new Random();

    public static void main(String[] args) throws IOException {
        ArgumentsProcessor argumentsProcessor = new ArgumentsProcessor();

        argumentsProcessor.processArguments(args);

        int noBlooms = argumentsProcessor.getInteger(0);
        int fromTopics = argumentsProcessor.getInteger(1);
        int toTopics = argumentsProcessor.getInteger(2);
        int topicsStep = argumentsProcessor.getInteger(3);

        for (int k = fromTopics; k <= toTopics; k += topicsStep) {
            int totalBytes = 0;
            int totalTopics = 0;

            for (int j = 0; j < noBlooms; j++) {
                Bloom bloom = generateRandomBloom(k);
                totalTopics += bloom.size();
                totalBytes += BloomEncoder.encode(bloom).length;
            }

            System.out.println("Topics " + k);
            System.out.println("Average topics per bloom " + (double)totalTopics / noBlooms);
            System.out.println("Average bytes per bloom " + (double)totalBytes / noBlooms);
        }
    }

    private static Bloom generateRandomBloom(int ntopics) {
        Bloom bloom = new Bloom();

        for (int n = 0; n < ntopics; n++)
            bloom.add(random.nextInt(Bloom.BLOOM_BITS));

        return bloom;
    }
}
