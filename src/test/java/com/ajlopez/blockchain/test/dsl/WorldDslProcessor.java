package com.ajlopez.blockchain.test.dsl;

import com.ajlopez.blockchain.test.World;

import java.io.IOException;

/**
 * Created by ajlopez on 19/12/2020.
 */
public class WorldDslProcessor {
    private final World world;

    public WorldDslProcessor(World world) {
        this.world = world;
    }

    public World getWorld() {
        return this.world;
    }

    public void processCommands(DslParser parser) throws IOException, DslException {
        for (DslCommand cmd = parser.parse(); cmd != null; cmd = parser.parse())
            cmd.execute(this.world);
    }
}
