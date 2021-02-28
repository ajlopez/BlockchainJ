package com.ajlopez.blockchain.test.dsl.commands;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.dsl.DslCommand;
import com.ajlopez.blockchain.utils.HashUtils;
import com.ajlopez.blockchain.utils.HexUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by ajlopez on 28/02/2021.
 */
public class DslAccountCommand extends DslCommand {
    public DslAccountCommand(List<String> arguments) {
        super("account", arguments);
    }

    @Override
    public void execute(World world) throws IOException {
        String name = this.getName(0, "name");
        Coin balance = this.getCoin(1, "balance");
        long nonce = this.getLongInteger(2, "nonce");
        String bytecodes = this.getName(3, "code");
        Hash codeHash = null;
        byte[] code = null;

        if (bytecodes != null) {
            code = HexUtils.hexStringToBytes(bytecodes);
            codeHash = HashUtils.calculateHash(code);
            world.setCode(codeHash, code);
        }

        Account account = new Account(balance, nonce, code != null ? code.length : 0, codeHash, null);

        world.setAccount(name, account);
    }
}
