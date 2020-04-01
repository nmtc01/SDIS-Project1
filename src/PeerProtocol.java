import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class PeerProtocol implements Message {
    private static String remote_object_name;

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

    public static void main(String args[]) {
        //Parse args
        if (!parseArgs(args))
            return;

        try {
            PeerProtocol peer = new PeerProtocol();
            Message stub = (Message) UnicastRemoteObject.exportObject(peer, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(remote_object_name, stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public String sendMessage(String operation, String operand1, Integer operand2) throws RemoteException {
        String reply = "I'm communicating";
        return reply;
    }
}
