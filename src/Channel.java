import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Channel implements Runnable {
    private String address;
    private Integer port;
    private InetAddress group;
    private MulticastSocket receiverSocket;

    public Channel(String full) throws IOException {
        String[] MC = full.split(":", 2);
        this.address = MC[0];
        this.port = Integer.parseInt(MC[1]);
        this.group = InetAddress.getByName(this.address);
        this.receiverSocket = new MulticastSocket(this.port);
        this.receiverSocket.joinGroup(this.group);
    }

    public void send(byte[] msg) {
        try {
            DatagramPacket packet = new DatagramPacket(msg, msg.length, this.group, this.port);
            System.out.println("estou a enviar - group: " + this.group + ", port: " + this.port);
            receiverSocket.send(packet);
            System.out.println("enviei - group: " + this.group + ", port: " + this.port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            byte[] buf = new byte[65000];

            while (true) {
                //REQUEST
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                System.out.println("estou pronto para receber - group: " + this.group + ", port: " + this.port);
                receiverSocket.receive(packet);
                System.out.println("Received message - peer" + PeerProtocol.getPeer().getPeer_id());
                //MESSAGE
                ReceivedMessagesManager manager = new ReceivedMessagesManager(packet);
                PeerProtocol.getThreadExecutor().execute(manager);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
