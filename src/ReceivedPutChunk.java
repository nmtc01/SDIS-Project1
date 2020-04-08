import java.net.DatagramPacket;

public class ReceivedPutChunk implements Runnable {
    private String version;
    private String fileId;
    private int chunkNo;
    private int repDeg;
    private byte[] body;

    public ReceivedPutChunk(String version, String fileId, int chunkNo, int repDeg, byte[] body) {
        this.version = version;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.repDeg = repDeg;
        this.body = body;
    }

    @Override
    public void run() {
        MessageFactory messageFactory = new MessageFactory();
        byte msg[] = messageFactory.storedMsg(this.version, PeerProtocol.getPeer().getPeer_id(), this.fileId, this.chunkNo, this.repDeg);
        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length);
        System.out.printf("Sent message: %s\n", messageFactory.getMessageString());
        PeerProtocol.getThreadExecutor().execute(new SendMessagesManager(sendPacket));
    }
}
