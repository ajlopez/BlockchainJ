package com.ajlopez.blockchain.test.dsl.commands;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.dsl.DslCommand;

import java.io.IOException;
import java.util.List;

/**
 * Created by ajlopez on 02/03/2021.
 */
public class DslTransactionCommand extends DslCommand {
    public DslTransactionCommand(List<String> arguments) {
        super("transaction", arguments);
    }

    @Override
    public void execute(World world) throws IOException {
        String name = this.getName(0, "name");
        Address from = this.getAddress(world, 1, "from");
        Address to = this.getAddress(world, 2, "to");
        Coin value = this.getCoin(3, "value");
        long nonce = this.getLongInteger(4, "nonce");

        Transaction transaction = new Transaction(from, to, value, nonce, null, 6000000, Coin.ZERO);

        world.setTransaction(name, transaction);
    }
}
