import java.io.File;

public class Storage {
    private double free_sapce;
    private File directory;

    public Storage(double space, int peer_id){
        this.free_sapce = space;

        File tmp = new File("PeerProtocol/Peer" + peer_id);
        if (!tmp.exists()) {
            if (tmp.mkdirs()) {
                this.directory = tmp;
                System.out.println("Created folder for Peer" + peer_id);
            }
        }
    }

    public void storeFile(int peer_id) {
        //TODO
    }
}
