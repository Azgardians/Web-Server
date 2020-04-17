import java.io.*;
import java.net.*;
import java.util.*;

public final class HttpRequest implements Runnable
{
        final static String CRLF = "\r\n";
        Socket socket;

        public HttpRequest(Socket socket) throws IOException 
        {
                this.socket = socket;
        }
        
	public void run()
        {
                try {
                	processRequest();
        	} catch (Exception e) {
                	System.out.println(e);
        	}
        }

        private void processRequest() throws Exception
        {
                InputStream is = socket.getInputStream();
       	 	DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		
		String requestLine = br.readLine();
		System.out.println();
		System.out.println(requestLine);

		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
        		System.out.println(headerLine);
		}
		
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();  // skip over the method, which should be "GET"
		String fileName = tokens.nextToken();
		
		fileName = "." + fileName;
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
        		fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
        		fileExists = false;
		}
		
		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
        		statusLine = "HTTP/1.0 200 OK" + CRLF;
        		contentTypeLine = "Content-type: " + contentType( fileName ) + CRLF;
		} else {
        		statusLine ="HTTP/1.0 404 Not Found" + CRLF;
        		contentTypeLine = "Content-type: text/html" + CRLF;
        		entityBody = "<HTML>" +
				 "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
				"<BODY>Not Found</BODY></HTML>";
		}
		os.writeBytes(statusLine);
		os.writeBytes(contentTypeLine);
		os.writeBytes(CRLF);
		
		// Send the entity body.
		if (fileExists) {
        		sendBytes(fis, os);
        		fis.close();
		} else {
        		os.writeBytes(entityBody);
		}

		os.close();
		br.close();
		socket.close();
        }
	
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception
	{
        	// Construct a 1K buffer to hold bytes on their way to the socket.
        	byte[] buffer = new byte[1024];
        	int bytes = 0;

        	// Copy requested file into the socket's output stream.
        	while((bytes = fis.read(buffer)) != -1 ) {
                	os.write(buffer, 0, bytes);
        	}
	}
	private static String contentType(String fileName)
	{
        	if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
                	return "text/html";
        	}
        	else if(fileName.toLowerCase().endsWith(".gif")){
                	return "gif";
        	}
        	else if(fileName.toLowerCase().endsWith(".jpg")||fileName.toLowerCase().endsWith(".jpeg")||fileName.toLowerCase().endsWith(".jpg")){
                	return "image/jpeg";
        	}
        	return "application/octet-stream";
	}
}