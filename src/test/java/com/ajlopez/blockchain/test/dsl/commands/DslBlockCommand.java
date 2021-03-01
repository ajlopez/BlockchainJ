package com.ajlopez.blockchain.test.dsl.commands;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.dsl.DslCommand;
import com.ajlopez.blockchain.test.utils.FactoryHelper;

import java.io.IOException;
import java.util.List;

/**
 * Created by ajlopez on 01/03/2021.
 */
public class DslBlockCommand extends DslCommand {
    public DslBlockCommand(List<String> arguments) {
        super("block", arguments);
    }

    @Override
    public void execute(World world) throws IOException {
        String name = this.getName(0, "name");
        String parentName = this.getName(1, "parent");

        if (parentName == null)
            parentName = "genesis";

        List<String> transactionNames = this.getNames(2, "transactions");
        List<String> uncleNames = this.getNames(3, "uncles");

        Block parent = world.getBlock(parentName);
        List<Transaction> transactions = world.getTransactions(transactionNames);
        List<BlockHeader> uncles = world.getBlockHeaders(uncleNames);

        Block block = FactoryHelper.createBlock(parent, FactoryHelper.createRandomAddress(), transactions, uncles);

        world.setBlock(name, block);
    }
}
