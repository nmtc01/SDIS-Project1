import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class PeerProtocol implements Message {
    private static String remote_object_name;
    private static String sub_protocol;
    private static String operand1;
    private static Integer replication_degree;

    public static boolean parseArgs(String[] args) {
        //Check the number of arguments given
        if (args.length != 1) {
            System.out.println("Usage: java PeerProtocol");
            return false;
        }

        //Parse acces_point
        remote_object_name = args[0];

        //TODO ver quais args é suposto passar

        return true;
    }

    public static void establishCommunication() {
        try {
            PeerProtocol peer = new PeerProtocol();
            Message stub = (Message) UnicastRemoteObject.exportObject(peer, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(remote_object_name, stub);

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

    public static String handleRequest(){
        //Choose the path
        switch (sub_protocol) {
            case "BACKUP": {
                System.out.println("case 0 - TODO");
                System.out.println(sub_protocol + " " + operand1 + " " + replication_degree);
                break;
            }
            case "RESTORE": {
                System.out.println("case 1 - TODO");
                System.out.println(sub_protocol + " " + operand1);
                break;
            }
            case "DELETE": {
                System.out.println("case 2 - TODO");
                System.out.println(sub_protocol + " " + operand1);
                break;
            }
            case "RECLAIM": {
                System.out.println("case 3 - TODO");
                System.out.println(sub_protocol + " " + operand1);
                break;
            }
        }

        return null;
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
}
