package com.ajlopez.blockchain.test.dsl.commands;

import com.ajlopez.blockchain.bc.BlockBuilder;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.dsl.DslCommand;

import java.io.IOException;
import java.util.List;

/**
 * Created by ajlopez on 03/03/2021.
 */
public class DslBlockHeaderCommand extends DslCommand {
    public DslBlockHeaderCommand(List<String> arguments) {
        super("transaction", arguments);
    }

    @Override
    public void execute(World world) throws IOException {
        String name = this.getName(0, "name");
        String parentName = this.getName(1, "parent");

        if (parentName == null)
            parentName = "genesis";

        List<String> uncleNames = this.getNames(2, "uncles");

        List<BlockHeader> uncles = world.getBlockHeaders(uncleNames);

        BlockHeader blockHeader;

        // TODO parent could be a header
        Block parent = world.getBlock(parentName);

        if (parent != null)
            blockHeader = new BlockBuilder()
                    .parent(parent)
                    .uncles(uncles)
                    .buildHeader();
        else
            blockHeader = new BlockBuilder()
                    .parentHeader(world.getBlockHeader(parentName))
                    .uncles(uncles)
                    .buildHeader();

        world.setBlockHeader(name, blockHeader);
    }
}
