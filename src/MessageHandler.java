import java.net.DatagramPacket;

public class MessageHandler {
    private DatagramPacket packet;

    MessageHandler(DatagramPacket packet) {
        this.packet = packet;
    }

    public void run() {
        String subProtocol = null;

        switch (subProtocol) {
            case "PUTCHUNK":
                break;
            case "STORED":
                break;
            case "DELETE":
                break;
            case "GETCHUNK":
                break;
            case "CHUNK":
                break;
            case "REMOVED":
                break;
            default:
                break;
        }
    }
}
