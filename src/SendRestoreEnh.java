import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendRestoreEnh implements Runnable {
    private DatagramPacket packet;
    private int port;

    SendRestoreEnh(DatagramPacket packet, int port) {
        this.packet = packet;
        this.port = port;
    }

    @Override
    public void run() {
        byte[] message = parsePacket(this.packet);
        try {
            InetAddress host_name = InetAddress.getLocalHost();
            //Create socket
            Socket echoSocket = new Socket(host_name, this.port);
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);

            //Send
            out.println(message);
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private byte[] parsePacket(DatagramPacket packet) {
        return packet.getData();
    }
}
