
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
/**
 * Class for handing client requests from Server
 */
public class ConnectionHandler  {
    private Socket conn;       // socket representing TCP/IP connection to Client
    private InputStream is;    // get data from client on this input stream	
    private BufferedOutputStream bos;   // can send data back to the client on this output stream
    private OutputStream os;    // to write data to a client
    private BufferedReader br;         // use buffered reader to read client data
    private PrintWriter pw;             // to write data to a client socket
    private String web_dir;             // root directory

    public ConnectionHandler(Socket conn, String root) {
            this.conn = conn;
            try {
                web_dir = root;
                is = conn.getInputStream();     // get data from client on this input stream
                os = conn.getOutputStream();
		bos = new BufferedOutputStream(conn.getOutputStream());  // to send data back to the client on this stream
		br = new BufferedReader(new InputStreamReader(is)); // use buffered reader to read client data
                pw = new PrintWriter(os);
		} catch (IOException ioe) {
                    System.out.println("ConnectionHandler: " + ioe.getMessage());
		}
	}
    /**
     * Method for handling client requests 
     */   
    public void handleRequests() {  
        System.out.println("new ConnectionHandler thread started .... ");
        try {
            String line = br.readLine(); 
            StringTokenizer st = new StringTokenizer(line);
            //parse in HTTP request method
            String requestType = st.nextToken().toUpperCase();
            //parse in the document requested
            String docRequested = st.nextToken().toLowerCase();
            //check for methods other than GET or HEAD     
            if (!requestType.equals("GET")  && !requestType.equals("HEAD")) {
                pw.println("HTTP/1.1 501 Not Implemented");
                pw.flush();
                cleanup();  //clear all objects close connection.
            }
            //if its a GET or HEAD
            else {
                //append document to root
                docRequested = web_dir + "/" + docRequested;
                File f;
                f = new File(docRequested);
                if (!f.exists()) {     //file not found
                    notFoundResponse();
                    cleanup();  //clear all objects and exit.
                }
                int contentLength = (int) f.length();
                String contentType = getContentType(docRequested);
                //Action for HEAD
                if (requestType.equals("HEAD")) {
                    printHeadRequest(contentLength, contentType); 
                    pw.flush(); 
                    cleanup();       //clear all objects and exit.         
                }
                //Action for GET
                else if (requestType.equals("GET")) {
                    byte[] fileData = getFileData(f, contentLength);
                    printHeadRequest(contentLength, contentType);
                    pw.println();       //empty line between header and content
                    pw.flush();
                    bos.write(fileData, 0, contentLength);  //write document details to socket
                    bos.flush();
                    cleanup();  //clear all objects and exit.
                } 
            }
        } catch (Exception e) {
            System.out.println("ConnectionHandler: " + e.getMessage());
            cleanup(); //clear all objects and exit.
        }
    }
     /**
      * Method that returns the type of a document requested from Server
      * @param type Type of document as parameter
      * @return type of document requested
      */   
    private String getContentType(String type) {
        if (type.endsWith("html")) {
            return "text/html";
        }
        return "plain text";
    }
    /**
     * Method for printing the HEAD of a document requested
     * @param cLength length of document
     * @param cType type of document
     */    
    private void printHeadRequest(int cLength, String cType) {
        pw.write("HTTP/1.1 200 OK \r\n");
        pw.println("Server: Simple Java Http Server");
        pw.println("Content-Type: " + cType);
        pw.println("Content-Length: " + cLength); 
    }
    /**
     * Method for printing the HEAD for a file that does not exist
     */           
    private void notFoundResponse() {
        pw.println("HTTP/1.1 404 Not Found");
        pw.println("Server: Simple Java Http Server");
        pw.println("Content-Type: Non Existent");
        pw.println("Content-Length: 0");
        pw.flush();           
    }
    /**
     * Method for reading a document to be written to server
     * @param f file to be written to server
     * @param fLength Length of the file to be written
     * @return the data in the file in bytes
     * @throws IOException possible Exception error when reading to the file.
     */  
    private byte[] getFileData(File f, int fLength) throws IOException {
        byte[] fileData = new byte[fLength];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            fis.read(fileData);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        fis.close();
        return fileData;
        }
    /**
     * method for cleaning and closing all objects opened
     * Gotten from Ozgur Akgun's examples
     */
    private void cleanup() {
	System.out.println("ConnectionHandler: ... cleaning up and exiting ... ");
        try {
            br.close();
            is.close();
            bos.close();
            conn.close();
            pw.close();
	} catch (IOException ioe) {
            System.out.println("ConnectionHandler:cleanup " + ioe.getMessage());
	}
    }
}
