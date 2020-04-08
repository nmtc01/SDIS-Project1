import java.net.DatagramPacket;
import java.util.Iterator;

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

    public void initiateStorage(Double space) {
        this.storage = new Storage(space, peer_id);
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
        FileInfo file = new FileInfo(file_path, replication_degree);
        file.prepareChunks();

        //File store
        this.storage.storeFile(file, this.peer_id);

        //Send PUTCHUNK message for each file's chunk
        //TODO missing encode things
        Iterator<Chunk> chunkIterator = file.getChunks().iterator();
        MessageFactory messageFactory = new MessageFactory();
        while(chunkIterator.hasNext()) {
            Chunk chunk = chunkIterator.next();
            byte msg[] = messageFactory.putChunkMsg(chunk, replication_degree, this.peer_id);
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length);
            new Thread(new SendMessagesManager(sendPacket)).start();
            System.out.printf("Sent message: %s\n", messageFactory.getMessageString());
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //TODO FINISH

        return "backup";
    }

    @Override
    public String restore(String file) {
        return "restore";
    }

    @Override
    public String delete(String file) {
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
