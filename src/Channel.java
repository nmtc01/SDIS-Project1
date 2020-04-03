import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Channel {
    private String address;
    private Integer port;
    private InetAddress group;

    public Channel(String full) throws IOException{
        String[] MC = full.split(":", 2);
        this.address = MC[0];
        this.port = Integer.parseInt(MC[1]);
        this.group = InetAddress.getByName(this.address);
    }

    //TODO finish this

    public void send() {

    }

    public void run() throws IOException {
        MulticastSocket multicastSocket = new MulticastSocket(port);
        multicastSocket.setTimeToLive(1);
        multicastSocket.joinGroup(group);

        while (true) {

            //REQUEST
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            multicastSocket.receive(packet);
            //MESSAGE

        }

    }
}
