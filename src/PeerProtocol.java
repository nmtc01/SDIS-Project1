import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class PeerProtocol {
    //Args
    private static Double protocol_version;
    private static Integer peer_id;
    private static String acc_point;
    private static Channel[] channels = new Channel[3];

    public static void main(String args[]) {
        System.out.println("Starting Peer Protocol");
        //Parse args
        if (!parseArgs(args))
            return;

        //Create initiator peer
        Peer peer = new Peer(peer_id, channels);
        System.out.println("Started peer with id " + peer_id);

        //Establish RMI communication between TestApp and Peer
        establishCommunication(peer);

        //Initiate storage for initiator peer
        peer.initiateStorage(0.0);
    }

    public static boolean parseArgs(String[] args) {
        //Check the number of arguments given
        if (args.length != 6) {
            System.out.println("Usage: java PeerProtocol version peer_id access_point MC MDB MDR");
            System.out.println("Each channel in format address:port");
            return false;
        }

        //Parse protocol version
        protocol_version = Double.parseDouble(args[0]);
        //Parse peer id
        peer_id = Integer.parseInt(args[1]);
        //Parse access point
        acc_point = args[2];
        //Parse channels
        try {
            for (int i = 3; i < 6; i++) {
                Channel MC = new Channel(args[i]);
                channels[i-3] = MC;
            }
        }
        catch (IOException e) {
            System.out.println(e.toString());
            return false;
        }

        return true;
    }

    public static void establishCommunication(Peer peer) {
        try {
            PeerInterface stub = (PeerInterface) UnicastRemoteObject.exportObject(peer, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(acc_point, stub);

        } catch (Exception e) {
            System.err.println("Peer Protocol exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public static Double getProtocol_version() {
        return protocol_version;
    }

}
