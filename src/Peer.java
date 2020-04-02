import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Peer implements Message{

    private static String sub_protocol;
    private static String operand1;
    private static Integer replication_degree;
    private double peer_size;
    private Set<Chunk> chunks = new HashSet<Chunk>();
    //Args
    private static String protocol_version;
    private static Integer peer_id;
    private static String acc_point;
    private static Pair<String, Integer>[] channels = new Pair[3];

    public static boolean parseArgs(String[] args) {
        //Check the number of arguments given
        if (args.length != 6) {
            System.out.println("Usage: java Peer version id access_point MC MDB MDR");
            System.out.println("Each channel in format address:port");
            return false;
        }

        //Parse protocol version
        protocol_version = args[0];
        //Parse peer id
        peer_id = Integer.parseInt(args[1]);
        //Parse access point
        acc_point = args[2];
        //Parse channels
        for (int i = 3; i < 6; i++) {
            String[] MC = args[i].split(":", 2);
            Pair<String, Integer> MC_pair = new Pair<>(MC[0], Integer.parseInt(MC[1]));
            channels[i-3] = MC_pair;
        }

        return true;
    }

    public static void establishCommunication() {
        try {
            Peer peer = new Peer();
            Message stub = (Message) UnicastRemoteObject.exportObject(peer, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(Integer.toString(peer_id), stub);

            System.err.println("Starting Peer Protocol");
        } catch (Exception e) {
            System.err.println("Peer Protocol exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public static String handleRequest(){
        //TODO delete prints - only for debug
        //TODO write subprotocols - complete switch

        //Choose the path
        switch (sub_protocol) {
            case "BACKUP": {
                System.out.println("case 0 - TODO");
                System.out.println(sub_protocol + " " + operand1 + " " + replication_degree);
                System.out.println(protocol_version + " " + peer_id + " " + acc_point);
                for (int i = 0; i < 3; i++)
                    System.out.println(channels[i].getFirst() + " " + channels[i].getSecond());
                break;
            }
            case "RESTORE": {
                System.out.println("case 1 - TODO");
                System.out.println(sub_protocol + " " + operand1);
                System.out.println(protocol_version + " " + peer_id + " " + acc_point);
                for (int i = 0; i < 3; i++)
                    System.out.println(channels[i].getFirst() + " " + channels[i].getSecond());
                break;
            }
            case "DELETE": {
                System.out.println("case 2 - TODO");
                System.out.println(sub_protocol + " " + operand1);
                System.out.println(protocol_version + " " + peer_id + " " + acc_point);
                for (int i = 0; i < 3; i++)
                    System.out.println(channels[i].getFirst() + " " + channels[i].getSecond());
                break;
            }
            case "RECLAIM": {
                System.out.println("case 3 - TODO");
                System.out.println(sub_protocol + " " + operand1);
                System.out.println(protocol_version + " " + peer_id + " " + acc_point);
                for (int i = 0; i < 3; i++)
                    System.out.println(channels[i].getFirst() + " " + channels[i].getSecond());
                break;
            }
        }

        return sub_protocol;
    }

    @Override
    public String sendMessage(String operation, String op1, Integer op2) throws RemoteException {
        //Parse request
        sub_protocol = operation;
        operand1 = op1;
        replication_degree = op2;

        //Handle request
        String reply = handleRequest();

        //Send reply
        return reply;
    }

    public static void main(String args[]) {
        //Parse args
        if (!parseArgs(args))
            return;

        establishCommunication();
    }
}
