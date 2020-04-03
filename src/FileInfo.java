import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class FileInfo {
    private String fileId;
    private File file;
    private int replication_degree;
    private Set<Chunk> chunks = new HashSet<Chunk>();
    private int chunk_size = 64000;

    public FileInfo(String filename, Integer replication_degree) {
        this.replication_degree = replication_degree;
        this.file = new File(filename);
        //TODO fileId
    }

    public void prepareChunks() {
        byte[] content = new byte[chunk_size];
        int chunck_nr = 0;

        try
        {
            FileInputStream fileIn = new FileInputStream(this.file);
            BufferedInputStream buffer = new BufferedInputStream(fileIn);

            int nr_bytes;
            while ((nr_bytes = buffer.read(content)) > 0)
            {
                byte[] info = Arrays.copyOf(content, nr_bytes);
                Chunk chunk = new Chunk(this.fileId, chunck_nr, nr_bytes, this.replication_degree);
                chunks.add(chunk);
                chunck_nr++;
                //TODO finish this
            }
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", fileId);
            e.printStackTrace();
        }
    }
}
