import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class Storage {
    private double free_space;
    private File directory;
    private boolean isUnix = true;
    private ArrayList<Chunk> storedChunks = new ArrayList<>();
    private ConcurrentHashMap<String, Integer> chunks_current_degrees = new ConcurrentHashMap<>();

    public Storage(int peer_id){
        this.free_space = 1000000000;
        createPeerDirectory(peer_id);
    }

    private void createPeerDirectory(int peer_id) {
        String root = System.getProperty("user.dir");
        String filepathWin = "\\PeerProtocol\\Peer" + peer_id; // in case of Windows
        String filepathUnix = "/PeerProtocol/Peer" + peer_id;
        String pathUnix = root+filepathUnix;
        String pathWin = root+filepathWin;

        File tmpUnix = new File(pathUnix);
        File tmpWin= new File(pathWin);
        this.directory = tmpUnix;

        if (!tmpUnix.exists()) {
            if (tmpUnix.mkdirs()) {
                this.isUnix = true;
                System.out.println("Created folder for Peer" + peer_id);
            }
            else {
                this.isUnix = false;
                this.directory = tmpWin;
                if (!tmpWin.exists())
                    if (tmpWin.mkdirs())
                        System.out.println("Created folder for Peer" + peer_id);
            }
        }
    }

    public void storeFile(FileInfo file, int peer_id) {
        String fileFolder;
        if (this.isUnix)
            fileFolder = directory.getPath()+ "/file" + file.getFileId();
        else fileFolder = directory.getPath()+ "\\file" + file.getFileId();

        File tmp = new File(fileFolder);
        if (!tmp.exists()) {
            if (tmp.mkdirs()) {
                System.out.println("Created folder for file " + file.getFile().getName() + " inside Peer" + peer_id);
                exportFile(tmp, file.getFile());
                System.out.println("Stored file " + file.getFile().getName() + " inside Peer" + peer_id);
            }
        }
        else exportFile(tmp, file.getFile());

        //Decrement free space
        decFreeSpace(file.getFile().length());
    }

    public void exportFile(File directory, File fileIn) {
        try {
            File fileOut;
            if (this.isUnix) {
                fileOut = new File(directory.getPath() + "/" + fileIn.getName());
            }
            else fileOut = new File(directory.getPath() + "\\" + fileIn.getName());

            //READ
            FileInputStream myReader = new FileInputStream(fileIn);
            byte[] input = new byte[(int) fileIn.length()];
            myReader.read(input);
            myReader.close();

            //WRITE
            FileOutputStream myWriter = new FileOutputStream(fileOut);
            myWriter.write(input);
            myWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteChunk(String fileId) {
        for (Iterator<Chunk> chunkIterator = this.storedChunks.iterator(); chunkIterator.hasNext();) {
            Chunk chunk = chunkIterator.next();

            if (chunk.getFile_id().equals(fileId)) {
                String file_path = directory.getPath() + "/file" + chunk.getFile_id();
                File file = new File(file_path);
                file.delete();

                chunkIterator.remove();
            }
        }
    }

    public void storeChunk(Chunk chunk) {
        //Store on system
        String fileFolder;
        if (this.isUnix)
            fileFolder = directory.getPath()+ "/file" + chunk.getFile_id();
        else fileFolder = directory.getPath()+ "\\file" + chunk.getFile_id();

        File tmp = new File(fileFolder);
        if (!tmp.exists()) {
            if (tmp.mkdirs()) {
                exportChunk(tmp, chunk);
            }
        }
        else exportChunk(tmp, chunk);

        //Store on hashmap and list
        String key = chunk.getFile_id()+"-"+chunk.getChunk_no();
        incrementChunkOccurences(key);
        this.storedChunks.add(chunk);

        //Decrement free space
        decFreeSpace(chunk.getContent().length);
    }

    public void exportChunk(File directory, Chunk chunk) {
        try {
            File fileOut;
            if (this.isUnix) {
                fileOut = new File(directory.getPath() + "/" + "chunk" + chunk.getChunk_no());
            }
            else fileOut = new File(directory.getPath() + "\\" + "chunk" + chunk.getChunk_no());

            byte[] input = chunk.getContent();

            //WRITE
            FileOutputStream myWriter = new FileOutputStream(fileOut);
            myWriter.write(input);
            myWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void incrementChunkOccurences(String key) {
        if (this.chunks_current_degrees.containsKey(key)) {
            this.chunks_current_degrees.replace(key, this.chunks_current_degrees.get(key)+1);
        }
        else {
            this.chunks_current_degrees.put(key, 1);
        }
    }

    public int getChunkCurrentDegree(String chunkKey) {
        if (this.chunks_current_degrees.containsKey(chunkKey))
            return this.chunks_current_degrees.get(chunkKey);
        else return 0;
    }

    public synchronized void decFreeSpace(double size) {
        this.free_space -= size;
    }

    public synchronized double getFreeSpace() {
        return free_space;
    }
}
