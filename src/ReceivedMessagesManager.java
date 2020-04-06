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

    public void parsePacket(DatagramPacket packet) {
        String data = new String(packet.getData());
        String[] dataArray = data.trim().split(" ");

        System.arraycopy(dataArray, 0, this.header, 0, dataArray.length-1);
        this.body = dataArray[dataArray.length-1].substring(8).getBytes();
    }

    private void managePutChunk(String version, int senderId, String fileId, int chunkNo, int repDeg, byte[] body) {
        Chunk chunk = new Chunk(fileId, chunkNo, body.length, repDeg, body);
        PeerProtocol.getCurrentPeer().getStorage().storeChunk(chunk, senderId);
        //Finish
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
}
