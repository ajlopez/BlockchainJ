package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.bc.BlockInformation;
import com.ajlopez.blockchain.bc.BlocksInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 18/03/2020.
 */
public class BlocksInformationEncoder {
    private BlocksInformationEncoder() {}

    public static byte[] encode(BlocksInformation blocksInformation) {
        byte[] rlpPosition = RLPEncoder.encodeLong(blocksInformation.getBlockOnChainPosition());

        List<BlockInformation> list = blocksInformation.getBlockInformationList();

        byte[][] rlpInfos = new byte[list.size()][];

        for (int k = 0; k < list.size(); k++)
            rlpInfos[k] = BlockInformationEncoder.encode(list.get(k));

        byte[] rlpList = RLP.encodeList(rlpInfos);

        return RLP.encodeList(rlpPosition, rlpList);
    }

    public static BlocksInformation decode(byte[] encoded) {
        byte[][] bytes = RLP.decodeList(encoded);

        long position = RLPEncoder.decodeLong(bytes[0]);

        byte[][] rlpInfos = RLP.decodeList(bytes[1]);

        List<BlockInformation> infos = new ArrayList<>();

        for (int k = 0; k < rlpInfos.length; k++)
            infos.add(BlockInformationEncoder.decode(rlpInfos[k]));

        BlocksInformation blocksInformation = new BlocksInformation();

        for (int k = 0; k < infos.size(); k++)
            blocksInformation.addBlockInformation(infos.get(k).getBlockHash(), infos.get(k).getTotalDifficulty());

        if (position >= 0)
            blocksInformation.setBlockOnChain(infos.get((int)position).getBlockHash());

        return blocksInformation;
    }
}
