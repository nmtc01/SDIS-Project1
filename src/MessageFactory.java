public class MessageFactory {
    public MessageFactory() {}

    //<Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
    public byte[] putChunkMsg(Chunk chunk, Integer replication_degree, int peer_id) {

        String version = PeerProtocol.getProtocol_version();
        String fileId = chunk.getFile_id();
        //TODO limit chunk size
        int chunkNo = chunk.getChunk_no();
        //TODO check this
        String headerTerms = "0xD0xA";
        String headerString = version + " " + "PUTCHUNK" + " " + peer_id + " " + fileId + " " + chunkNo + " "
                + replication_degree + " " + headerTerms;
        byte[] header = headerString.getBytes();
        byte[] content = chunk.getContent();
        byte[] putchunkMsg = new byte[header.length + content.length];
        System.arraycopy(header, 0, putchunkMsg, 0, header.length);
        System.arraycopy(content, 0, putchunkMsg, header.length, content.length);

        return putchunkMsg;
    }
}
