import java.net.DatagramPacket;

public class SendMessagesManager implements Runnable {
    private DatagramPacket packet;

    SendMessagesManager(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void run() {
        byte[] message = parsePacket(this.packet);
        String[] p_str = parsePacketStr(this.packet);
        String subProtocol = p_str[1];

        switch (subProtocol) {
            case "PUTCHUNK":
                managePutChunk(message);
                break;
            case "STORED":
                manageStored();
                break;
            case "DELETE":
                manageDelete();
                break;
            case "GETCHUNK":
                manageGetChunk();
                break;
            case "CHUNK":
                manageChunk();
                break;
            case "REMOVED":
                manageRemoved();
                break;
            default:
                break;
        }
    }

    private byte[] parsePacket(DatagramPacket packet) {
        return packet.getData();
    }

    private String[] parsePacketStr(DatagramPacket packet) {
        String p = new String(packet.getData());
        String[] pArray = p.trim().split(" ");
        return pArray;
    }

    private void managePutChunk(byte[] message) {
        PeerProtocol.getCurrentPeer().getMDBChannel().send(message);
    }

    private void manageRemoved() {

    }

    private void manageChunk() {

    }

    private void manageGetChunk() {

    }

    private void manageDelete() {

    }

    private void manageStored() {
    }
}
