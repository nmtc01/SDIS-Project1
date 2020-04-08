import java.net.DatagramPacket;

public class ReceivedMessagesManager implements Runnable {
    private String[] header;
    private byte[] body;

    public ReceivedMessagesManager(DatagramPacket packet) {
        parsePacket(packet);
    }

    @Override
    public void run() {
        String version = header[0];
        String subProtocol = header[1];
        int senderId = Integer.parseInt(header[2]);
        String fileId = header[3];
        int chunkNo = Integer.parseInt(header[4]);
        int repDeg = Integer.parseInt(header[5]);

        switch (subProtocol) {
            case "PUTCHUNK":
                managePutChunk(version, senderId, fileId, chunkNo, repDeg, body);
                break;
            case "STORED":
                System.out.println("Stored received");
                manageStored(version, senderId, fileId, chunkNo, repDeg);
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

    public void parsePacket(DatagramPacket packet) {
        String data = new String(packet.getData());
        String[] dataArray = data.trim().split(" ");
        this.header = new String[dataArray.length-1];
        System.arraycopy(dataArray, 0, this.header, 0, dataArray.length-1);
        if (dataArray[dataArray.length-1].substring(8).getBytes() != null)
            this.body = dataArray[dataArray.length-1].substring(8).getBytes();
    }

    private void managePutChunk(String version, int senderId, String fileId, int chunkNo, int repDeg, byte[] body) {
        //If the peer that sent is the same peer receiving
        if (senderId == PeerProtocol.getPeer().getPeer_id())
            return;
        System.out.printf("Received message: %s PUTCHUNK %d %s %d %d\n", version, senderId, fileId, chunkNo, repDeg);
        ReceivedPutChunk receivedPutChunk = new ReceivedPutChunk(version, fileId, chunkNo, repDeg, body);
        PeerProtocol.getThreadExecutor().execute(receivedPutChunk);
    }

    private void manageStored(String version, int senderId, String fileId, int chunkNo, int repDeg) {
        if (senderId == PeerProtocol.getPeer().getPeer_id())
            return;
        System.out.printf("Received message: %s STORED %d %s %d %d\n", version, senderId, fileId, chunkNo, repDeg);
    }

    private void manageRemoved() {

    }

    private void manageChunk() {

    }

    private void manageGetChunk() {

    }

    private void manageDelete() {

    }
}
