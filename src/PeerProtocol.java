import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class PeerProtocol {
    //Args
    private static String protocol_version;
    private static Integer peer_id;
    private static String acc_point;
    private static Peer peer;
    private static ScheduledThreadPoolExecutor threadExecutor;

    public static void main(String args[]) {
        System.out.println("Starting Peer Protocol");
        //Parse args
        if (!parseArgs(args))
            return;

        //Parse channels
        Channel[] channels = new Channel[3];
        try {
            Channel MC = new Channel(args[3], args[4]);
            channels[0] = MC;
            Channel MDB = new Channel(args[5], args[6]);
            channels[1] = MDB;
            Channel MDR = new Channel(args[7], args[8]);
            channels[2] = MDR;
        }
        catch (IOException e) {
            System.out.println(e.toString());
            return;
        }

        //Create executor
        threadExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);

        //Create initiator peer
        peer = new Peer(peer_id, channels);
        System.out.println("Started peer with id " + peer_id);

        //Establish RMI communication between TestApp and Peer
        establishCommunication(peer);

        //Initiate storage for initiator peer
        peer.initiateStorage();

    }

    public static boolean parseArgs(String[] args) {
        //Check the number of arguments given
        if (args.length != 9) {
            System.out.println("Usage: java PeerProtocol version peer_id access_point MC MDB MDR");
            System.out.println("Each channel in format address port");
            return false;
        }

        //Parse protocol version
        protocol_version = args[0];
        if (protocol_version.length() != 3) {
            System.out.println("Version in format <n>.<m>");
            return false;
        }
        //Parse peer id
        peer_id = Integer.parseInt(args[1]);
        //Parse access point
        acc_point = args[2];

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

    public static String getProtocol_version() {
        return protocol_version;
    }

    public static Peer getPeer() {
        return peer;
    }

    public static ScheduledThreadPoolExecutor getThreadExecutor() {
        return threadExecutor;
    }
}
