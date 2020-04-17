import java.io.*;
import java.net.*;
import java.util.*;

public final class Webserver
{
	final static int port= 6001; 
	public static void main(String args[]) throws IOException
	{
		ServerSocket wserver=new ServerSocket(port);
		System.out.println("waiting for connecton");
		while(true)
		{
			Socket wclient=wserver.accept();
			// Construct an object to process the HTTP request message.
			HttpRequest request = new HttpRequest(wclient);
			// Create a new thread to process the request.
			Thread thread = new Thread(request);	
			// Start the thread.
			thread.start();
		}
	}
}
