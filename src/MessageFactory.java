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
        int chunkNo = chunk.getChunk_no();
        this.messageString = version + " " + "PUTCHUNK" + " " + peer_id + " " + fileId + " " + chunkNo + " " + replication_degree;
        String headerTerms = this.messageString + " \r\n\r\n";
        byte[] header = headerTerms.getBytes();
        byte[] content = chunk.getContent();
        byte[] putchunkMsg = new byte[header.length + content.length];
        System.arraycopy(header, 0, putchunkMsg, 0, header.length);
        System.arraycopy(content, 0, putchunkMsg, header.length, content.length);

        return putchunkMsg;
    }

    //<Version> STORED <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
    public byte[] storedMsg(String version, int senderId, String fileId, int chunkNo) {

        byte[] headerTerms = new byte[2];
        headerTerms[0] = 0xD;
        headerTerms[1] = 0xA;
        this.messageString = version + " " + "STORED" + " " + senderId + " " + fileId + " " + chunkNo;
        String headerString = this.messageString + " \r\n\r\n";
        byte[] header = headerString.getBytes();
        byte[] putchunkMsg = new byte[header.length + headerTerms.length];
        System.arraycopy(header, 0, putchunkMsg, 0, header.length);
        System.arraycopy(headerTerms, 0, putchunkMsg, header.length, headerTerms.length);

        return putchunkMsg;
    }
}
