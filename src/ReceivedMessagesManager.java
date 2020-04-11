import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ReceivedMessagesManager implements Runnable {
    private String[] header;
    private byte[] body;

    public ReceivedMessagesManager(byte[] msg) {
        parseMsg(msg);
    }

    @Override
    public void run() {
        String version = header[0];
        String subProtocol = header[1];
        int senderId = Integer.parseInt(header[2]);
        String fileId = header[3];
        int chunkNo = Integer.parseInt(header[4]);
        int repDeg = 0;
        if (header.length == 6)
            repDeg = Integer.parseInt(header[5]);

        switch (subProtocol) {
            case "PUTCHUNK":
                managePutChunk(version, senderId, fileId, chunkNo, repDeg, body);
                break;
            case "STORED":
                manageStored(version, senderId, fileId, chunkNo);
                break;
            case "DELETE":
                manageDelete(version, senderId, fileId);
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

    public void parseMsg(byte[] data) {
        int index;
        for (index = 0; index < data.length; index++) {
            if (data[index] == 0xD && data[index+1] == 0xA && data[index+2] == 0xD && data[index+3] == 0xA)
                break;
        }
        String headerStr = new String(Arrays.copyOfRange(data, 0, index));
        this.header = headerStr.split(" ");
        this.body = Arrays.copyOfRange(data, index+4, data.length);
    }

    private void managePutChunk(String version, int senderId, String fileId, int chunkNo, int repDeg, byte[] body) {
        //If the peer that sent is the same peer receiving
        if (senderId == PeerProtocol.getPeer().getPeer_id())
            return;
        System.out.printf("Received message: %s PUTCHUNK %d %s %d %d\n", version, senderId, fileId, chunkNo, repDeg);
        Random random = new Random();
        int random_value = random.nextInt(401);
        ReceivedPutChunk receivedPutChunk = new ReceivedPutChunk(version, fileId, chunkNo, repDeg, body);
        PeerProtocol.getThreadExecutor().schedule(receivedPutChunk, random_value, TimeUnit.MILLISECONDS);
    }

    private void manageStored(String version, int senderId, String fileId, int chunkNo) {
        if (senderId == PeerProtocol.getPeer().getPeer_id())
            return;
        Storage peerStorage = PeerProtocol.getPeer().getStorage();
        String chunkKey = fileId+"-"+chunkNo;
        peerStorage.incrementChunkOccurences(chunkKey);
        System.out.printf("Received message: %s STORED %d %s %d\n", version, senderId, fileId, chunkNo);
    }

    private void manageRemoved() {

    }

    private void manageChunk() {

    }

    private void manageGetChunk() {

    }

    private void manageDelete(String version, int senderId, String fileId) {
        if (senderId == PeerProtocol.getPeer().getPeer_id())
            return;
        System.out.printf("Received message: %s DELETE %d %s\n", version, senderId, fileId);
    }
}
