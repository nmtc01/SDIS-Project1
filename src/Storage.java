import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Storage implements java.io.Serializable {
    private double free_space;
    private double space_used;
    private File directory;
    private boolean isUnix = true;
    private ArrayList<FileInfo> storedFiles = new ArrayList<>();
    private ArrayList<Chunk> storedChunks = new ArrayList<>();
    private ConcurrentHashMap<String, byte[]> restoreChunks = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> chunks_current_degrees = new ConcurrentHashMap<>();
    private double total_space;

    public Storage(int peer_id) {
        this.free_space = 1000000000;
        createPeerDirectory(peer_id);
    }

    private void createPeerDirectory(int peer_id) {
        String root = System.getProperty("user.dir");
        String filepathWin = "\\PeerProtocol\\Peer" + peer_id; // in case of Windows
        String filepathUnix = "/PeerProtocol/Peer" + peer_id;
        String pathUnix = root + filepathUnix;
        String pathWin = root + filepathWin;

        File tmpUnix = new File(pathUnix);
        File tmpWin = new File(pathWin);
        this.directory = tmpUnix;

        if (!tmpUnix.exists()) {
            if (tmpUnix.mkdirs()) {
                this.isUnix = true;
                System.out.println("Created folder for Peer" + peer_id);
            } else {
                this.isUnix = false;
                this.directory = tmpWin;
                if (!tmpWin.exists())
                    if (tmpWin.mkdirs())
                        System.out.println("Created folder for Peer" + peer_id);
            }
        }
    }

    public ArrayList<Chunk> getStoredChunks() {
        return storedChunks;
    }

    public boolean isUnix() {
        return isUnix;
    }

    public void storeFile(FileInfo file, int peer_id) {
        String fileFolder;
        if (this.isUnix)
            fileFolder = directory.getPath() + "/file" + file.getFileId();
        else fileFolder = directory.getPath() + "\\file" + file.getFileId();

        File tmp = new File(fileFolder);
        if (!tmp.exists()) {
            if (tmp.mkdirs()) {
                System.out.println("Created folder for file " + file.getFile().getName() + " inside Peer" + peer_id);
                exportFile(tmp, file.getFile());
                System.out.println("Stored file " + file.getFile().getName() + " inside Peer" + peer_id);
            }
        } else exportFile(tmp, file.getFile());

        //Store on list
        this.storedFiles.add(file);

        //Decrement free space
        decFreeSpace(file.getFile().length());
    }

    public void restoreFile(File fileIn) {
        File fileFolder;
        boolean can_export = true;
        if (this.isUnix)
            fileFolder = new File(directory.getPath() + "/Restored");
        else fileFolder = new File(directory.getPath() + "\\Restored");

        if (!fileFolder.exists()) {
            if (fileFolder.mkdirs()) {
                System.out.println("Created folder to restored files");
            }
            else can_export = false;
        }

        if (!can_export)
            return;

        List<String> sortedChunkKeys = new ArrayList<>(this.getRestoreChunks().keySet());
        sortedChunkKeys.sort(new ChunkKeyComparator());
        File fileOut = new File(fileFolder+"/"+fileIn.getName());
        try {
            FileOutputStream myWriter;
            myWriter = new FileOutputStream(fileOut);
            for (String key : sortedChunkKeys) {
                //WRITE
                myWriter.write(this.restoreChunks.get(key));
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.restoreChunks.clear();
    }

    public void exportFile(File directory, File fileIn) {
        try {
            File fileOut;
            if (this.isUnix) {
                fileOut = new File(directory.getPath() + "/" + fileIn.getName());
            } else fileOut = new File(directory.getPath() + "\\" + fileIn.getName());

            //READ
            FileInputStream myReader = new FileInputStream(fileIn);
            byte[] input = new byte[(int) fileIn.length()];
            myReader.read(input);
            myReader.close();

            //WRITE
            FileOutputStream myWriter = new FileOutputStream(fileOut);
            myWriter.write(input);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteChunk(String fileId) {
        for (Iterator<Chunk> chunkIterator = this.storedChunks.iterator(); chunkIterator.hasNext(); ) {
            Chunk chunk = chunkIterator.next();

            if (chunk.getFile_id().equals(fileId)) {
                String file_path = directory.getPath() + "/file" + chunk.getFile_id() + "/chunk" + chunk.getChunk_no();
                File file = new File(file_path);
                file.delete();

                free_space += chunk.getChunk_size();

                chunkIterator.remove();
            }
        }
        String fileFolder = directory.getPath() + "/file" + fileId;
        File folder = new File(fileFolder);
        folder.delete();
    }

    public void storeChunk(Chunk chunk) {
        //Store on system
        String fileFolder;
        if (this.isUnix)
            fileFolder = directory.getPath() + "/file" + chunk.getFile_id();
        else fileFolder = directory.getPath() + "\\file" + chunk.getFile_id();

        File tmp = new File(fileFolder);
        if (!tmp.exists()) {
            if (tmp.mkdirs()) {
                exportChunk(tmp, chunk);
            }
        } else exportChunk(tmp, chunk);

        //Store on hashmap and list
        String key = chunk.getFile_id() + "-" + chunk.getChunk_no();
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
            } else fileOut = new File(directory.getPath() + "\\" + "chunk" + chunk.getChunk_no());

            byte[] input = chunk.getContent();

            //WRITE
            FileOutputStream myWriter = new FileOutputStream(fileOut);
            myWriter.write(input);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void incrementChunkOccurences(String key) {
        if (this.chunks_current_degrees.containsKey(key)) {
            this.chunks_current_degrees.replace(key, this.chunks_current_degrees.get(key) + 1);
        } else {
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

    public double getOccupiedSpace() {
        space_used = this.getTotalSpace() + free_space;
        return space_used;
    }

    public double getTotalSpace() {
        for (Iterator<Chunk> chunkIterator = this.storedChunks.iterator(); chunkIterator.hasNext(); ) {
            Chunk chunk = chunkIterator.next();
            total_space += chunk.getChunk_size();
        }
        return total_space;
    }

    public void reclaimSpace(double spaceClaimed, double spaceUsed) {
        this.total_space = spaceClaimed;
        this.free_space = spaceClaimed - spaceUsed;
        this.space_used = spaceUsed;
    }

    public void deleteChunk(Chunk chunk) {
        String file_path = directory.getPath() + "/file" + chunk.getFile_id() + "/chunk" + chunk.getChunk_no();
        File file = new File(file_path);
        file.delete();
    }

    public ArrayList<FileInfo> getStoredFiles() {
        return this.storedFiles;
    }

    public ConcurrentHashMap<String, byte[]> getRestoreChunks() {
        return restoreChunks;
    }

    public class ChunkKeyComparator implements Comparator<String> {

        @Override
        public int compare(String chunkKey1, String chunkKey2) {
            String[] chunk1 = chunkKey1.split("-");
            String[] chunk2 = chunkKey2.split("-");

            int chunkNo1 = Integer.parseInt(chunk1[1]);
            int chunkNo2 = Integer.parseInt(chunk2[1]);

            if (chunkNo1 < chunkNo2)
                return -1;
            if (chunkNo1 > chunkNo2)
                return 1;
            return 0;
        }
    }
}