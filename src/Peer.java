import java.util.concurrent.ScheduledThreadPoolExecutor;

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

    @Override
    public String backup(String file_path, Integer replication_degree) {
        FileInfo file = new FileInfo(file_path, replication_degree);
        file.prepareChunks();


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
