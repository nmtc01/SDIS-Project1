import java.rmi.Remote;

public interface PeerInterface extends Remote {
    String backup(String file, Integer replication_degree);
    String restore(String file);
    String delete(String file);
    String reclaim(Integer max_space);
    String state();
}
