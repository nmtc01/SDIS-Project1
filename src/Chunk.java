public class Chunk {
    private String file_id;
    private Integer chunk_no;
    private Float chunk_size; //DONT KNOW IF THEY ARE FINAL OR NOT
    private int replication_degree;

    public Chunk(String file_id, Integer chunk_no, Float chunk_size, int replication_degree) {
        this.file_id = file_id;
        this.chunk_no = chunk_no;
        this.chunk_size = chunk_size;
        this.replication_degree = replication_degree;
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
