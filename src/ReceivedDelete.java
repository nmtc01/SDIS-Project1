public class ReceivedDelete implements Runnable {
    private String version;
    String fileId;

    public ReceivedDelete(String version, String fileId) {
        this.version = version;
        this.fileId = fileId;
    }

    @Override
    public void run() {
        Storage peerStorage = PeerProtocol.getPeer().getStorage();
        //TODO delete dos chunks do file aqui
        peerStorage.deleteChunk(fileId);
    }
}
