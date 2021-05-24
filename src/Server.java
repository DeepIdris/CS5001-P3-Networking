
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Class for creating and handling Server connections
 */
public class Server {
    private ServerSocket ss;
    
    public Server(String root, int port) {
        try {
            ss = new ServerSocket(port);
            System.out.println("Server started ... Listening for connections on port " + port + " ...");
            while (true) {
                Socket conn = ss.accept(); // will wait until client requests a connection, then returns connection (socket)
                System.out.println("Server got new connection request from " + conn.getInetAddress());
                ConnectionHandler ch = new ConnectionHandler(conn, root); // create new handler for this connection
                ch.handleRequests();    // start handler thread
            }
	} catch (IOException ioe) {
            System.out.println("Ooops " + ioe.getMessage());
	} 
    }
}
