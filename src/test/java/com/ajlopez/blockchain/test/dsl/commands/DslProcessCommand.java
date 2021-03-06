package com.ajlopez.blockchain.test.dsl.commands;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.dsl.DslCommand;

import java.io.IOException;
import java.util.List;

/**
 * Created by ajlopez on 06/03/2021.
 */
public class DslProcessCommand extends DslCommand {
    public DslProcessCommand(List<String> arguments) {
        super("block", arguments);
    }

    @Override
    public void execute(World world) throws IOException {
        String name = this.getName(0, "name");
        Block block = world.getBlock(name);
        world.getBlockProcessor().processBlock(block);
    }
}
