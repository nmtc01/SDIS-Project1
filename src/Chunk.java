import javafx.util.Pair;

public class Chunk {
    private Pair<String, Integer> chunk_id;
    private Float chunk_size; //DONT KNOW IF THEY ARE FINAL OR NOT
    private int replication_degree;

    public Chunk(Pair<String, Integer> chunk_id, Float chunk_size, int replication_degree) {
        this.chunk_id = chunk_id;
        this.chunk_size = chunk_size;
        this.replication_degree = replication_degree;
    }

    public Pair<String, Integer> getChunk_id() {
        return chunk_id;
    }

    public void setChunk_id(Pair<String, Integer> chunk_id) {
        this.chunk_id = chunk_id;
    }

    public Float getChunk_size() {
        return chunk_size;
    }

    public void setChunk_size(Float chunk_size) {
        this.chunk_size = chunk_size;
    }

    public int getReplication_degree() {
        return replication_degree;
    }

    public void setReplication_degree(int replication_degree) {
        this.replication_degree = replication_degree;
    }
}
