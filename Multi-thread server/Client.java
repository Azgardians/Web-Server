
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class Client{
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_ADDRESS = 9090 ; 
    public static void main (String[] args)throws IOException{
        Socket ridereciever = new Socket(SERVER_IP , SERVER_ADDRESS);

        BufferedReader input = new BufferedReader(new InputStreamReader( ridereciever.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(ridereciever.getOutputStream(),true);
        
        while (true){
            System.out.println(">>");
            String command = keyboard.readLine();
            if (command.equals("quit")) break;
            out.println(command);

            String response = input.readLine();

            System.out.println("Server says :" + response);
        }
        
        ridereciever.close();
        System.exit(0);
    }
}