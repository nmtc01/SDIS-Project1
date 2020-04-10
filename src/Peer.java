import java.net.DatagramPacket;
import java.util.ArrayList;
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

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Integer getPeer_id(){
        return this.peer_id;
    }

    @Override
    public synchronized String backup(String file_path, Integer replication_degree) {
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

        return "Backup sucessful";
    }

    @Override
    public synchronized String restore(String file) {
        return "Restore successful";
    }

    @Override
    public synchronized String delete(String file_path) {
        FileInfo file = new FileInfo(file_path);
        String fileId = "";

        try {
            fileId = file.generateFileID();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.storage.deleteChunk(fileId);

        return "Delete successful";
    }

    @Override
    public synchronized String reclaim(Integer max_space) {

        double spaceUsed = this.storage.getOccupiedSpace();
        double spaceClaimed = max_space * 1000; //The client shall specify the maximum disk space in KBytes (1KByte = 1000 bytes)

        if (spaceUsed <= spaceClaimed) {
            this.storage.reclaimSpace(spaceClaimed, spaceUsed);
        }

        ArrayList<Chunk> chunks = this.storage.getStoredChunks();

        if (chunks.isEmpty()) {
            return "Impossible to reclaim space.";
        }

        double tmpSpace = spaceUsed - spaceClaimed;

        Iterator<Chunk> chunkIterator = chunks.iterator();

        do {
            Chunk chunk = chunkIterator.next();
            //send message
            tmpSpace -= chunk.getChunk_size();
            chunkIterator.remove();
            this.storage.deleteChunk(chunk);
        } while (tmpSpace > 0);

        double totalSpaceOccupied = 0;

        for (Chunk chunk: chunks) {
            totalSpaceOccupied += chunk.getChunk_size();
        }

        if (totalSpaceOccupied == 0) {
            for (Chunk chunk : chunks) {
                this.storage.deleteChunk(chunk);
            }

            chunks.clear();
        }

        this.storage.reclaimSpace(spaceClaimed, totalSpaceOccupied);

        return "Reclaim successful";
    }

    @Override
    public synchronized Storage state() {
        //For each file whose backup it has initiated
        System.out.println("-> For each file whose backup it has initiated:");
        for (int i = 0; i < this.storage.getStoredFiles().size(); i++) {
            FileInfo fileInfo = this.storage.getStoredFiles().get(i);

            //File pathname
            String filename = fileInfo.getFile().getPath();
            System.out.println("File pathname: "+filename);

            //File id
            String fileId = fileInfo.getFileId();
            System.out.println("\tFile id: "+fileId);

            //Replication degree
            int repDeg = fileInfo.getReplicationDegree();
            System.out.println("\tFile desired replication degree: "+repDeg);

            System.out.println("\t-> For each chunk of the file:");
            Iterator<Chunk> chunkIterator = fileInfo.getChunks().iterator();
            while (chunkIterator.hasNext()) {
                Chunk chunk = chunkIterator.next();

                //Chunk id
                String chunk_id = chunk.getFile_id()+"-"+chunk.getChunk_no();
                System.out.println("\t  Chunk id: "+chunk_id);

                //Perceived replication degree
                int repDegree = this.storage.getChunkCurrentDegree(chunk_id);
                System.out.println("\t  Perceived replication degree: "+repDegree);

            }
        }

        //For each chunk it stores
        System.out.println("-> For each chunk it stores:");
        for (int i = 0; i < this.storage.getStoredChunks().size(); i++) {
            Chunk chunk = this.storage.getStoredChunks().get(i);

            //Chunk id
            String chunk_id = chunk.getFile_id()+"-"+chunk.getChunk_no();
            System.out.println("\tChunk id: "+chunk_id);

            //Size
            int size = chunk.getChunk_size();
            System.out.println("\tChunk size: "+size);

            //Perceived replication degree
            int repDeg = this.storage.getChunkCurrentDegree(chunk_id);
            System.out.println("\tPerceived replication degree: "+repDeg);
        }

        return this.storage;
    }
}
