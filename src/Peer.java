import java.util.HashSet;
import java.util.Set;

public class Peer {
    private int peer_id;
    private double peer_size;
    private Set<Chunk> chunks = new HashSet<Chunk>();
}
