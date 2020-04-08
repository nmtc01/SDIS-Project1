public class MessageFactory {
    private String messageString;

    public MessageFactory() {}

    public String getMessageString() {
        return messageString;
    }

    //<Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
    public byte[] putChunkMsg(Chunk chunk, Integer replication_degree, int peer_id) {

        String version = PeerProtocol.getProtocol_version();
        String fileId = chunk.getFile_id();
        //TODO limit chunk size
        int chunkNo = chunk.getChunk_no();
        //TODO check this
        byte[] headerTerms = new byte[2];
        headerTerms[0] = 0xD;
        headerTerms[1] = 0xA;
        String headerString = version + " " + "PUTCHUNK" + " " + peer_id + " " + fileId + " " + chunkNo + " "
                + replication_degree + " ";
        this.messageString = headerString;
        byte[] header = headerString.getBytes();
        byte[] content = chunk.getContent();
        byte[] putchunkMsg = new byte[header.length + headerTerms.length + content.length];
        System.arraycopy(header, 0, putchunkMsg, 0, header.length);
        System.arraycopy(headerTerms, 0, putchunkMsg, header.length, headerTerms.length);
        System.arraycopy(content, 0, putchunkMsg, header.length+headerTerms.length, content.length);

        return putchunkMsg;
    }

    //<Version> STORED <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
    public byte[] storedMsg(String version, int senderId, String fileId, int chunkNo, int repDeg) {

        byte[] headerTerms = new byte[2];
        headerTerms[0] = 0xD;
        headerTerms[1] = 0xA;
        String headerString = version + " " + "STORED" + " " + senderId + " " + fileId + " " + chunkNo + " "
                + repDeg + " ";
        this.messageString = headerString;
        byte[] header = headerString.getBytes();
        byte[] putchunkMsg = new byte[header.length + headerTerms.length];
        System.arraycopy(header, 0, putchunkMsg, 0, header.length);
        System.arraycopy(headerTerms, 0, putchunkMsg, header.length, headerTerms.length);

        return putchunkMsg;
    }
}
