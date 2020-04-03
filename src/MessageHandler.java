import java.net.DatagramPacket;

public class MessageHandler {
    private DatagramPacket packet;
    private String[] parsedHeader;
    private byte[] body;


    MessageHandler(DatagramPacket packet) {
        this.packet = packet;
    }

    public void run() {

        parsedHeader = parseHeader(packet);
        body = parseBody(packet);
        String subProtocol = parsedHeader[1];
        int senderId = Integer.parseInt(parsedHeader[2]);
        String fileId = parsedHeader[3];
        int chunkNo = Integer.parseInt(parsedHeader[4]);
        int repDeg = Integer.parseInt(parsedHeader[5]);

        switch (subProtocol) {
            case "PUTCHUNK":
                managePutChunk();
                break;
            case "STORED":
                manageStored();
                break;
            case "DELETE":
                manageDelete();
                break;
            case "GETCHUNK":
                manageGetChunk();
                break;
            case "CHUNK":
                manageChunk();
                break;
            case "REMOVED":
                manageRemoved();
                break;
            default:
                break;
        }
    }

    private void manageRemoved() {

    }

    private void manageChunk() {

    }

    private void manageGetChunk() {

    }

    private void manageDelete() {

    }

    private void manageStored() {
    }

    private void managePutChunk() {
    }

    private String[] parseHeader(DatagramPacket packet) {
        String header = new String(packet.getData());
        String[] headerArray = header.trim().split(" ");
        return headerArray;
    }

    private byte[] parseBody(DatagramPacket packet) {
        return body;
    }
}
