import java.io.*;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ReceiveRestoreEnh implements Runnable {
    private String fileId;
    private int chunkNo;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public ReceiveRestoreEnh(String fileId, int chunkNo) {;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        int port = 4444 + PeerProtocol.getPeer().getPeer_id();
        try {
            this.serverSocket = new ServerSocket(port);
            this.clientSocket = this.serverSocket.accept();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(this.clientSocket.getInputStream()));
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
