import java.io.*;

public class Storage {
    private double free_space;
    private File directory;

    public Storage(double space, int peer_id){
        this.free_space = space;

        String root = System.getProperty("user.dir");
        String filepathWin = "\\PeerProtocol\\Peer" + peer_id; // in case of Windows
        String filepathUnix = "/PeerProtocol/Peer" + peer_id;
        String pathUnix = root+filepathUnix;
        String pathWin = root+filepathWin;

        File tmpUnix = new File(pathUnix);
        File tmpWin= new File(pathWin);
        if (!tmpUnix.exists()) {
            if (tmpUnix.mkdirs()) {
                this.directory = tmpUnix;
                System.out.println("Created folder for Peer" + peer_id);
            }
            else if (!tmpWin.exists()) {
                if (tmpWin.mkdirs()) {
                    this.directory = tmpWin;
                    System.out.println("Created folder for Peer" + peer_id);
                }
            }
        }
    }

    public void storeFile(FileInfo file, int peer_id) {
        String fileFolderUnix = directory.getPath()+ "/file" + file.getFileId();
        String fileFolderWin = directory.getPath()+ "\\file" + file.getFileId();

        File tmpUnix = new File(fileFolderUnix);
        File tmpWin = new File(fileFolderWin);
        if (!tmpUnix.exists()) {
            if (tmpUnix.mkdirs()) {
                System.out.println("Created folder for file " + file.getFile().getName() + " inside Peer" + peer_id);
                exportFile(tmpUnix, file.getFile(), true);
                System.out.println("Stored file " + file.getFile().getName() + " inside Peer" + peer_id);
            }
            else if (!tmpWin.exists()) {
                if (tmpWin.mkdirs()) {
                    System.out.println("Created folder for file " + file.getFile().getName() + " inside Peer" + peer_id);
                    exportFile(tmpWin, file.getFile(), false);
                    System.out.println("Stored file " + file.getFile().getName() + " inside Peer" + peer_id);
                }
            }
        }
    }

    public void exportFile(File directory, File fileIn, boolean isUnix) {
        try {
            File fileOut;
            if (isUnix) {
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

    public void storeChunk(Chunk chunk, int peer_id) {
        String fileFolderUnix = directory.getPath()+ "/file" + chunk.getFile_id();
        String fileFolderWin = directory.getPath()+ "\\file" + chunk.getFile_id();

        File tmpUnix = new File(fileFolderUnix);
        File tmpWin = new File(fileFolderWin);
        if (!tmpUnix.exists()) {
            if (tmpUnix.mkdirs()) {
                exportChunk(tmpUnix, chunk, true);
                System.out.println("Stored chunk inside Peer" + peer_id);
            }
            else if (!tmpWin.exists()) {
                if (tmpWin.mkdirs()) {
                    exportChunk(tmpWin, chunk, false);
                    System.out.println("Stored file inside Peer" + peer_id);
                }
            }
        }
    }

    public void exportChunk(File directory, Chunk chunk, boolean isUnix) {
        try {
            File fileOut;
            if (isUnix) {
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
}
