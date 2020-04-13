public class ReceivedRemoved implements Runnable{
    private String version;
    String fileId;
    private int chunkNo;

    public ReceivedRemoved(String version, String fileId, int chunkNo) {
        this.version = version;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    @Override
    public void run() {

    }
}
