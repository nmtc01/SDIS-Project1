import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

public class ReceiveRestoreEnh implements Runnable {
    private String fileId;
    private int chunkNo;
    private String address;
    private int port;
    private Socket echoSocket;

    public ReceiveRestoreEnh(String fileId, int chunkNo, String address, int sender_id) {;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.address = address;
        this.port = 4444+sender_id+chunkNo+Integer.valueOf(fileId.charAt(0));
        try {
            InetAddress host_name = InetAddress.getByName(this.address);
            this.echoSocket = new Socket(host_name, this.port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(this.echoSocket.getInputStream()));
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] body = new byte[64000]; // or 4096, or more
            int count;
            while ((count = in.read(body)) > 0)
            {
                out.write(body, 0, count);
            }
            PeerProtocol.getPeer().getStorage().getRestoreChunks().putIfAbsent(this.fileId+"-"+this.chunkNo, body);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
