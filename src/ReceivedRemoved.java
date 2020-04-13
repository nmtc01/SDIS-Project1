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

        int desiredReplicationDegree = 0;
        Storage storage = PeerProtocol.getPeer().getStorage();
        for (int i = 0; i < storage.getStoredChunks().size(); i++) {
            Chunk chunk = storage.getStoredChunks().get(i);
            if (chunk.getFile_id().equals(fileId) && chunk.getChunk_no() == this.chunkNo) {
                desiredReplicationDegree = chunk.getDesired_replication_degree();
                break;
            }
        }

        String chunkKey = fileId +"-" + chunkNo;
        if (storage.getChunkCurrentDegree(chunkKey) < desiredReplicationDegree) {
            //TODO PUTCHUNK
        }
    }
}
