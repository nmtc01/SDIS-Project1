import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Channel implements Runnable {
    private String address;
    private Integer port;
    private InetAddress group;

    public Channel(String full) throws IOException {
        String[] MC = full.split(":", 2);
        this.address = MC[0];
        this.port = Integer.parseInt(MC[1]);
        this.group = InetAddress.getByName(this.address);
    }

    //TODO finish this

    public void send(byte[] msg) {
        try {
            DatagramSocket senderSocket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(msg, msg.length);
            senderSocket.send(packet);
            System.out.println("Sent packet");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            MulticastSocket receiverSocket = new MulticastSocket(port);
            receiverSocket.setTimeToLive(1);
            receiverSocket.joinGroup(group);

            while (true) {
                //REQUEST
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                receiverSocket.receive(packet);
                //MESSAGE
                System.out.println(packet.getData());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
