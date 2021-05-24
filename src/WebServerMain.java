
import java.io.IOException;
/**
 * Class for accepting inputs and starting the server
 */
public class WebServerMain {
   
    public static void main(String[] args) {
        String ROOT = null;
        int PORT = 0;
        try {
            ROOT = args[0];
            PORT = Integer.parseInt(args[1]);
            Server s = new Server(ROOT, PORT);
        }
        catch (ArrayIndexOutOfBoundsException aob) {
            System.err.println("Usage: java WebServerMain <document_root> <port>");
        }
    }
}
