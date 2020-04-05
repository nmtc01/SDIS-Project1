public class Chunk {
    private String file_id;
    private Integer chunk_no;
    private Integer chunk_size;
    private int replication_degree;
    private byte[] content;

    public Chunk(String file_id, Integer chunk_no, Integer chunk_size, int replication_degree, byte[] content) {
        this.file_id = file_id;
        this.chunk_no = chunk_no;
        this.chunk_size = chunk_size;
        this.replication_degree = replication_degree;
        this.content = content;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public Integer getChunk_no() {
        return chunk_no;
    }

    public void setChunk_no(Integer chunk_no) {
        this.chunk_no = chunk_no;
    }

    public Integer getChunk_size() {
        return chunk_size;
    }

    public void setChunk_size(Integer chunk_size) {
        this.chunk_size = chunk_size;
    }

    public int getReplication_degree() {
        return replication_degree;
    }

    public void setReplication_degree(int replication_degree) {
        this.replication_degree = replication_degree;
    }

    public byte[] getContent() {
        return this.content;
    }
}
