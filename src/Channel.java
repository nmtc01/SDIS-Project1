import java.io.IOException;
import java.net.InetAddress;

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

    public void run() {

    }
}
