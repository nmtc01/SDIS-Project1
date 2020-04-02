import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {
    private static String remote_object_name;
    private static String sub_protocol;
    private static String operand1;
    private static Integer operand2;

    public static boolean parseArgs(String[] args) {
        //Check the number of arguments given
        if (args.length != 4 && args[1] == "BACKUP" && args.length != 3) {
            System.out.println("Usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> [<opnd_2>]");
            return false;
        }

        //Parse peer_ap
        remote_object_name = args[0];
        //Parse sub_protocol
        sub_protocol = args[1];
        //Parse operands
        operand1 = args[2];
        if (sub_protocol.equals("BACKUP"))
            operand2 = Integer.parseInt(args[3]);

        return true;
    }

    public static void main(String[] args) {
        //Parse args
        if (!parseArgs(args))
            return;

        try {
            Registry registry = LocateRegistry.getRegistry(); //default port 1099
            Message stub = (Message) registry.lookup(remote_object_name);
            String response = stub.sendMessage(sub_protocol, operand1, operand2);
            System.out.println(response);
        } catch (Exception e) {
            System.err.println("TestApp exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
