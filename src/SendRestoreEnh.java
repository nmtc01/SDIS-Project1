import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

public class SendRestoreEnh implements Runnable {
    private DatagramPacket packet;
    private int port;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    SendRestoreEnh(DatagramPacket packet, int port) {
        this.packet = packet;
        this.port = port;
    }

    @Override
    public void run() {
        byte[] message = parsePacket(this.packet);
        try {
            this.serverSocket = new ServerSocket(this.port);
            this.clientSocket = this.serverSocket.accept();

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            //Send
            out.println(message);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] parsePacket(DatagramPacket packet) {
        return packet.getData();
    }
}
