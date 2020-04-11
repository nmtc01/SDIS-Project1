import java.net.DatagramPacket;

public class ReceivedGetChunk implements Runnable {
    private String version;
    private String fileId;
    private int chunkNo;

    public ReceivedGetChunk(String version, String fileId, int chunkNo) {
        this.version = version;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    @Override
    public void run() {
        Storage storage = PeerProtocol.getPeer().getStorage();
        for (int i = 0; i < storage.getStoredChunks().size(); i++) {
            Chunk chunk = storage.getStoredChunks().get(i);
            if (chunk.getFile_id().equals(this.fileId) && chunk.getChunk_no() == this.chunkNo) {
                MessageFactory messageFactory = new MessageFactory();
                byte msg[] = messageFactory.chunkMsg(this.version, PeerProtocol.getPeer().getPeer_id(), this.fileId, this.chunkNo, chunk.getContent());
                DatagramPacket sendPacket = new DatagramPacket(msg, msg.length);
                new Thread(new SendMessagesManager(sendPacket)).start();
                System.out.printf("Sent message: %s\n", messageFactory.getMessageString());
            }
        }
    }
}
