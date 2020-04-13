import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.Socket;

class ClientHandler implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private static final String[] names = {"Ajay","Matt","Prithvi","Rahul"};
    private static final String[] rides = {"olx", "uber","rapido","fluer"};

    public ClientHandler(Socket clienthand)throws IOException{
        this.client= clienthand;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(),true);

    }

    public void run(){
        try{
            while (true){
                String request = in.readLine();
                if (request.contains("name")){
                    out.println( getRandomName() );
                }else {
                    out.println("Please 'add name tag' to get a ride");
                }
            }
        }catch(IOException e){
            System.err.println("IO EXCEPTION ocurred ");
        }
        finally{
            try {
                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.err.println("IOEXCEPTION caught");
            }
        }
       
    }
    public static String getRandomName(){
        String name = names[(int) (Math.random()*names.length)];
        String ride = rides[(int)(Math.random()*rides.length)];
        return name + " is riding on " + ride ;
    }

}