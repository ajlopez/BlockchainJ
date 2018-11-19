package com.ajlopez.blockchain.net.peers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ajlopez on 21/10/2018.
 */
public class PacketOutputStream {
    private DataOutputStream dataOutputStream;

    public PacketOutputStream(OutputStream outputStream) {
        this.dataOutputStream = new DataOutputStream(outputStream);
    }

    public boolean writePacket(byte[] bytes) {
        try {
            this.dataOutputStream.writeInt(0x01020304);
            this.dataOutputStream.writeInt(bytes.length);
            this.dataOutputStream.write(bytes);
            this.dataOutputStream.flush();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }

        return true;
    }
}
