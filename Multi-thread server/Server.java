import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.*;

class Server {

    
    private static final int PORT = 9090 ; 
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(3);

    public static void main (String []args) throws IOException{
        ServerSocket listener = new ServerSocket (PORT);
        while (true) {

            System.out.println("[SERVER] Waiting for client connection ...");
            Socket client = listener.accept();
            System.out.println("[SERVER] Client got connected...");    
            ClientHandler clientThread = new ClientHandler(client);
            clients.add(clientThread);

            pool.execute(clientThread);
        }
    }
}