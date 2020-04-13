import java.io.*;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ReceiveRestoreEnh implements Runnable {
    private int senderId;
    private String fileId;
    private int chunkNo;

    public ReceiveRestoreEnh(int senderId, String fileId, int chunkNo) {
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PeerProtocol.getPeer().getMDRChannel().getPort());
            Socket clientSocket = serverSocket.accept();
            DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
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
