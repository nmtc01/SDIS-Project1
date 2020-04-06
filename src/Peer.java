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
            new Thread(channels[i]).start();
        }
    }

    public void initiateStorage(Double space) {
        this.storage = new Storage(space, peer_id);
    }

    public byte[] preparePutchunkMsg(Chunk chunk, Integer replication_degree) {
        //<Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
        Double version = PeerProtocol.getProtocol_version();
        String fileId = chunk.getFile_id();
        int chunkNo = chunk.getChunk_no();
        //TODO check this
        String headerTerms = "0xD 0xA";
        String headerString = version + " " + "PUTCHUNK" + " " + this.peer_id + " " + fileId + " " + chunkNo + " "
                + replication_degree + " " + headerTerms;
        byte[] header = headerString.getBytes();
        byte[] content = chunk.getContent();
        byte[] putchunkMsg = new byte[header.length + content.length];
        System.arraycopy(header, 0, putchunkMsg, 0, header.length);
        System.arraycopy(content, 0, putchunkMsg, header.length, content.length);

        return putchunkMsg;
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
        while(chunkIterator.hasNext()) {
            Chunk chunk = chunkIterator.next();
            byte msg[] = preparePutchunkMsg(chunk, replication_degree);
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length);
            MessageHandler msgHandler = new MessageHandler(sendPacket);
            new Thread(msgHandler).start();
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
