import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;

public class SendRestoreEnh implements Runnable {
    private DatagramPacket packet;

    SendRestoreEnh(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void run() {
        byte[] message = parsePacket(this.packet);

        InetAddress host_name = PeerProtocol.getPeer().getMDRChannel().getGroup();
        int port = PeerProtocol.getPeer().getMCChannel().getPort();

        try {
            //Create socket
            Socket echoSocket = new Socket(host_name, port);
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
