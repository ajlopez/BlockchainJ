package com.ajlopez.blockchain.net.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ajlopez on 21/10/2018.
 */
public class PacketInputStream {
    private DataInputStream dataInputStream;

    public PacketInputStream(InputStream inputStream) {
        this.dataInputStream = new DataInputStream(inputStream);
    }

    public byte[] readPacket() {
        try {
            int signature = this.dataInputStream.readInt();

            if (signature != 0x01020304)
                return null;

            int length = this.dataInputStream.readInt();

            byte[] bytes = new byte[length];
            int btotalread = 0;

            while (btotalread < length) {
                int bread = this.dataInputStream.read(bytes, btotalread, length - btotalread);

                if (bread == -1)
                    return null;

                btotalread += bread;
            }

            return bytes;
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());

            return null;
        }
    }
}
