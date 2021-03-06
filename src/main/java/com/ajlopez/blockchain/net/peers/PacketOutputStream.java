package com.ajlopez.blockchain.net.peers;

import java.io.DataOutputStream;
import java.io.EOFException;
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

    public boolean writePacket(Packet packet) {
        try {
            this.dataOutputStream.writeInt(0x01020304);

            this.dataOutputStream.writeShort(packet.getProtocol());
            this.dataOutputStream.writeShort(packet.getNetwork());

            byte[] bytes = packet.getBytes();
            this.dataOutputStream.writeInt(bytes.length);
            this.dataOutputStream.write(bytes);

            this.dataOutputStream.flush();
        }
        catch (EOFException ex) {
            return false;
        }
        catch (IOException ex) {
            System.out.println(ex);
            return false;
        }

        return true;
    }

    public void close() throws IOException {
        this.dataOutputStream.close();;
    }
}
