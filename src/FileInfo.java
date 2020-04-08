import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FileInfo {
    private String fileId;
    private File file;
    private Set<Chunk> chunks = new HashSet<Chunk>();
    private int chunk_size = 64000;

    public FileInfo(String filename) {

        String root = System.getProperty("user.dir");
        String filepathWin = "\\resources\\" + filename; // in case of Windows
        String filepathUnix = "/resources/" + filename;
        String path = root+filepathWin;

        File tmp = new File(path);
        if (!tmp.exists()) {
            path = root + filepathUnix;
            tmp = new File(path);
        }
        this.file = tmp;
        this.fileId = generateFileID();
    }

    public String generateFileID() {
        String filename = this.file.getName();
        String date = String.valueOf(this.file.lastModified());
        String directory = this.file.getParent();
        String fileId;
        try {
            fileId = sha256toString(sha256(filename+"."+date+"."+directory));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        return fileId;
    }

    public byte[] sha256(String string) throws NoSuchAlgorithmException{
        byte[] fileId = string.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        return md.digest(fileId);
    }

    public String sha256toString(byte[] sha256) {
        // Convert byte array into big integer representation
        BigInteger number = new BigInteger(1, sha256);

        // Convert message digest into bitstring
        return number.toString();
    }

    public void prepareChunks(int replication_degree) {
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
                Chunk chunk = new Chunk(this.fileId, chunck_nr, nr_bytes, replication_degree, info);
                chunks.add(chunk);
                chunck_nr++;
            }
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", file.getName());
            e.printStackTrace();
        }
    }

    public Set<Chunk> getChunks() {
        return chunks;
    }

    public String getFileId() {
        return fileId;
    }

    public File getFile() {
        return this.file;
    }
}
