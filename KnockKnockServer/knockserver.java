import java.net.*;
import java.io. *;
import java.util.*;
class knockserver
{
	public static void main(String args[]) throws IOException
	{
		try{
			ServerSocket kserver=new ServerSocket(6000);
			System.out.println("waiting for connection...");
			Socket kclient=kserver.accept();
			System.out.println("connection accepted");
			BufferedReader in =new BufferedReader(new InputStreamReader(kclient.getInputStream()));
			PrintWriter out=new PrintWriter(kclient.getOutputStream(),true);
			String inputLine, outputLine;
            
    			// Initiate conversation with client
    			KnockKnockProtocol kkp = new KnockKnockProtocol();
    			outputLine = kkp.processInput(null);
    			out.println(outputLine);

    			while ((inputLine = in.readLine()) != null) {
        			outputLine = kkp.processInput(inputLine);
        			out.println(outputLine);
        			if (outputLine.equals("Bye.")){
            				break;}
    			}
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}


}
