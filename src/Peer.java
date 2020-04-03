import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements PeerInterface{

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
    public String backup(String file_path, Integer replication_degree) {
        FileInfo file = new FileInfo(file_path, replication_degree);

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
