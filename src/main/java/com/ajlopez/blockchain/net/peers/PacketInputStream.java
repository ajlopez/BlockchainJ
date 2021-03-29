package com.ajlopez.blockchain.net.peers;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ajlopez on 21/10/2018.
 */
public class PacketInputStream {
    private final DataInputStream dataInputStream;
    private boolean closed;

    public PacketInputStream(InputStream inputStream) {
        this.dataInputStream = new DataInputStream(inputStream);
    }

    public Packet readPacket() {
        if (this.closed)
            return null;

        try {
            int signature = this.dataInputStream.readInt();

            if (signature != 0x01020304) {
                this.close();
                return null;
            }

            // TODO check supported protocol
            short protocol = this.dataInputStream.readShort();
            short network = this.dataInputStream.readShort();

            int length = this.dataInputStream.readInt();

            System.out.println("length " + length);
            byte[] bytes = new byte[length];
            int btotalread = 0;

            while (btotalread < length) {
                int bread = this.dataInputStream.read(bytes, btotalread, length - btotalread);

                if (bread == -1) {
                    this.close();

                    return null;
                }

                btotalread += bread;
            }

            return new Packet(protocol, network, bytes);
        }
        catch (EOFException ex) {
            // TODO better exception process
            System.out.println(ex);
            this.close();

            return null;
        }
        catch (IOException ex) {
            // TODO better exception process
            System.out.println(ex);
            this.close();

            return null;
        }
    }

    public void close() {
        try {
            this.closed = true;
            this.dataInputStream.close();
        }
        catch (IOException ex) {
            // TODO process exception
        }
    }

    public boolean isClosed() {
        return this.closed;
    }
}
