import java.net.DatagramPacket;

public class SendRestoreEnh implements Runnable {
    private DatagramPacket packet;

    SendRestoreEnh(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void run() {
        byte[] message = parsePacket(this.packet);
    }

    private byte[] parsePacket(DatagramPacket packet) {
        return packet.getData();
    }
}
