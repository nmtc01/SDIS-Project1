import javax.imageio.IIOException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Peer implements PeerInterface{

    private static String sub_protocol;
    private static String operand1;
    private static Integer replication_degree;
    private double peer_size;
    private Set<Chunk> chunks = new HashSet<Chunk>();
    //Args
    private static Double protocol_version;
    private static Integer peer_id;
    private static String acc_point;
    private static Channel[] channels = new Channel[3];

    public static boolean parseArgs(String[] args) {
        //Check the number of arguments given
        if (args.length != 6) {
            System.out.println("Usage: java Peer version id access_point MC MDB MDR");
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

    public static void establishCommunication() {
        try {
            Peer peer = new Peer();
            System.out.printf("Created peer with id " + peer_id);
            peer_id++;
            PeerInterface stub = (PeerInterface) UnicastRemoteObject.exportObject(peer, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(Integer.toString(peer_id), stub);

            System.err.println("Starting Peer Protocol");
        } catch (Exception e) {
            System.err.println("Peer Protocol exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        //Parse args
        if (!parseArgs(args))
            return;

        establishCommunication();
    }

    @Override
    public String backup(String file, Integer replication_degree) {
        return "backup";
    }

    @Override
    public String restore(String file) {
        return "restore";
    }

    @Override
    public String delete(String file) {
        return "delete";
    }

    @Override
    public String reclaim(Integer max_space) {
        return "reclaim";
    }

    @Override
    public String state() {
        return "state";
    }
}
