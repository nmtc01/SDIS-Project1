import java.net.DatagramPacket;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class ReceivedRemoved implements Runnable{
    private int replication_degree;
    private String filepath;
    private String fileId;
    private int chunkNo;

    public ReceivedRemoved(String fileId, String filepath, int replication_degree, int chunkNo) {
        this.replication_degree = replication_degree;
        this.filepath = filepath;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    @Override
    public void run() {
        FileInfo file = new FileInfo(this.filepath);
        file.prepareChunks(replication_degree);
        Chunk chunk;
        Iterator<Chunk> chunkIterator = file.getChunks().iterator();
        while(chunkIterator.hasNext()) {
            chunk = chunkIterator.next();
            if (chunk.getFile_id().equals(fileId) && chunk.getChunk_no() == chunkNo) {
                MessageFactory messageFactory = new MessageFactory();
                byte msg[] = messageFactory.putChunkMsg(chunk, this.replication_degree, PeerProtocol.getPeer().getPeer_id());
                DatagramPacket sendPacket = new DatagramPacket(msg, msg.length);
                new Thread(new SendMessagesManager(sendPacket)).start();
                String messageString = messageFactory.getMessageString();
                System.out.printf("Sent message: %s\n", messageString);

                String chunkKey = chunk.getFile_id() + "-" + chunk.getChunk_no();
                PutChunkAttempts putChunkAttempts = new PutChunkAttempts(1, 5, sendPacket, chunkKey, this.replication_degree, messageString);
                PeerProtocol.getThreadExecutor().schedule(putChunkAttempts, 1, TimeUnit.SECONDS);
                return;
            }
        }
    }
}
