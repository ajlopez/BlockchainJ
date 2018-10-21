package com.ajlopez.blockchain.net.messages;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ajlopez on 21/10/2018.
 */
public class MessageOutputStream {
    private DataOutputStream dataOutputStream;

    public MessageOutputStream(OutputStream outputStream) {
        this.dataOutputStream = new DataOutputStream(outputStream);
    }

    public boolean writeMessage(byte[] bytes) {
        try {
            this.dataOutputStream.writeInt(0x01020304);
            this.dataOutputStream.writeInt(bytes.length);
            this.dataOutputStream.write(bytes);
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }

        return true;
    }
}
