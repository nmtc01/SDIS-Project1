import java.io.BufferedReader;
import java.io.InputStreamReader;
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

        InetAddress host_name = PeerProtocol.getPeer().getMCChannel().getGroup();
        int port = PeerProtocol.getPeer().getMCChannel().getPort();

        try {
            //Create socket
            Socket echoSocket = new Socket(host_name, port);
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

            //Send
            out.println(message);
            //reply = in.readLine();
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private byte[] parsePacket(DatagramPacket packet) {
        return packet.getData();
    }
}
