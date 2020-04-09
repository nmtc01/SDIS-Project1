import java.net.DatagramPacket;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class Peer implements PeerInterface{

    private Integer peer_id;
    private Channel[] channels;
    private Storage storage;

    public Peer(Integer peer_id, Channel[] channels) {
        this.peer_id = peer_id;
        this.channels = channels;
        executeChannels();
    }

    public void executeChannels() {
        //Initiate channels' threads
        for (int i = 0; i < 3; i++) {
            PeerProtocol.getThreadExecutor().execute(channels[i]);
        }
    }

    public void initiateStorage() {
        this.storage = new Storage(peer_id);
    }

    public Channel getMCChannel(){
        return this.channels[0];
    }

    public Channel getMDBChannel() {
        return this.channels[1];
    }

    public Channel getMDRChannel() {
        return this.channels[2];
    }

    public Storage getStorage() {
        return this.storage;
    }

    public Integer getPeer_id(){
        return this.peer_id;
    }

    @Override
    public String backup(String file_path, Integer replication_degree) {
        //File creation
        FileInfo file = new FileInfo(file_path);
        file.prepareChunks(replication_degree);

        //File store
        this.storage.storeFile(file, this.peer_id);

        //Send PUTCHUNK message for each file's chunk
        Iterator<Chunk> chunkIterator = file.getChunks().iterator();
        MessageFactory messageFactory = new MessageFactory();

        while(chunkIterator.hasNext()) {
            Chunk chunk = chunkIterator.next();
            byte msg[] = messageFactory.putChunkMsg(chunk, replication_degree, this.peer_id);
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length);
            new Thread(new SendMessagesManager(sendPacket)).start();
            String messageString = messageFactory.getMessageString();
            System.out.printf("Sent message: %s\n", messageString);
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            String chunkKey = chunk.getFile_id()+"-"+chunk.getChunk_no();
            PutChunkAttempts putChunkAttempts = new PutChunkAttempts(1, 5, sendPacket, chunkKey, replication_degree, messageString);
            PeerProtocol.getThreadExecutor().schedule(putChunkAttempts, 1, TimeUnit.SECONDS);
        }

        return "backup";
    }

    @Override
    public String restore(String file) {
        return "restore";
    }

    @Override
    public String delete(String file_path) {
        FileInfo file = new FileInfo(file_path);
        String fileId = "";

        try {
            fileId = file.generateFileID();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.storage.deleteChunk(fileId);

        return "delete";
    }

    @Override
    public String reclaim(Integer max_space) {
        return "reclaim";
    }

    @Override
    public String state() {
        return "state";
    }
}
