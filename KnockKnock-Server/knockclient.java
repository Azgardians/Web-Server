import java.net.*;
import java.io. *;
import java.util.*;
class knockclient
{
	private static String ip = "127.0.0.1";
	public static void main(String args[]) throws IOException
	{
		try{
			
			Socket kkSocket = new Socket(ip,6000);
    			PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
   			BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
			BufferedReader stdIn =new BufferedReader(new InputStreamReader(System.in));
            		String fromServer;
            		String fromUser;
			while ((fromServer = in.readLine()) != null) {
    				System.out.println("Server: " + fromServer);
    				if (fromServer.equals("Bye.")){
        				break;}

	    			fromUser = stdIn.readLine();
    				if (fromUser != null) {
        				System.out.println("Client: " + fromUser);
        				out.println(fromUser);
    				}
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}
}