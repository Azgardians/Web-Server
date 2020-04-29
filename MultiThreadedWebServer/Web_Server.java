import java.io.*;
import java.net.*;
import java.util.*;

final class WebServer {
    public static void main(String argv[]) throws Exception {
        int port = 6897;

        // Establish the listen socket.
        ServerSocket listenSocket = new ServerSocket(port);

        while (true) {
            // Listen for a TCP connection request.
            Socket connectionSocket = listenSocket.accept();

            // Construct an object to process the HTTP request message
            HttpRequest request = new HttpRequest(connectionSocket);

            // Create a new thread to process the request.
            Thread thread = new Thread(request);

            // Start the thread.
            thread.start();
        }

    }
}

final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    // Constructor
    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    // Implement the run() method of the Runnable interface.
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        // Get a reference to the socket's input and output streams.
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // Set up input stream filters.
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // Get the request line of the HTTP request message.
        String requestLine = br.readLine();

        // Extract the filename from the request line.
        StringTokenizer tokens = new StringTokenizer(requestLine);

        boolean badRequest = false;
        boolean methodNotImplemented = false;

        String fileName = null;
        String version = null;

        String command = tokens.nextToken();
        if (tokens.hasMoreTokens()) {
            fileName = tokens.nextToken();
            // Prepend a "." so that file request is within the current directory.
            fileName = "." + fileName;
        } else {
            badRequest = true;
        }
        if (tokens.hasMoreTokens()) {
            version = tokens.nextToken();
        } else {
            badRequest = true;
        }

        if (tokens.hasMoreTokens()) {
            badRequest = true;
        }

        boolean fileExists = true;
        boolean fileOpen = true;
        FileOutputStream fos = null;
        FileInputStream fis = null;

        if (!((command.equals("GET")) || (command.equals("HEAD")) || (command.equals("PUT")))) {
            methodNotImplemented = true;
        }

        if (!version.matches("HTTP/.[.].")) {
            System.out.println("Version pattern mismatch");

            badRequest = true;
        }

        // Construct the response message.
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;

        if (badRequest) {
            statusLine = "HTTP/1.0 400 Bad Request" + CRLF;
            contentTypeLine = "Content-type: " + "text/html" + CRLF;
            entityBody = "<HTML>" + "<HEAD><TITLE>Bad Request</TITLE></HEAD>" + "<BODY>" + requestLine
                    + " is a bad request</BODY></HTML>" + CRLF;
        }

        if (methodNotImplemented) {
            statusLine = "HTTP/1.0 501 Method Not Implemented" + CRLF;
            contentTypeLine = "Content-type: " + "text/html" + CRLF;
            entityBody = "<HTML>" + "<HEAD><TITLE>Method Not Implemented</TITLE></HEAD>" + "<BODY> Method " + command
                    + " Not Implemented</BODY></HTML>" + CRLF;
        }

        if (!(badRequest || methodNotImplemented)) {
            if (command.equals("PUT")) {
                try {
                    fos = new FileOutputStream(fileName);
                } catch (Exception e) {
                    fileOpen = false;
                }
            } else {
                // Open the requested file.
                try {
                    fis = new FileInputStream(fileName);
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                    fileExists = false;
                }
            }

            if (fileExists && fileOpen) {
                statusLine = "HTTP/1.0 200 OK" + CRLF;
                contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
            } else {
                if (!(fileExists)) {
                    statusLine = "HTTP/1.0 404 Not Found" + CRLF;
                    contentTypeLine = "Content-type: " + "text/html" + CRLF;
                    entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>File " + fileName
                            + " Not Found</BODY></HTML>" + CRLF;
                } else {
                    statusLine = "HTTP/1.0 404 Not Found" + CRLF;
                    contentTypeLine = "Content-type: " + "text/html" + CRLF;
                    entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>File " + fileName
                            + " could not be written</BODY></HTML>" + CRLF;
                }
            }
        }

        // Send the status line.
        os.writeBytes(statusLine);

        // Send the content-type line.
        os.writeBytes(contentTypeLine);

        // Send a blank line to indicate the end of the header lines.
        os.writeBytes(CRLF);

        // Send the entity body.
        if (!(badRequest || methodNotImplemented) && (fileExists && fileOpen)) {
            if (command.equals("PUT")) {
                try {
                    int character;

                    // Read the input data one character at a time until end
                    // of stream reached
                    character = br.read();
                    while (character != -1) {
                        fos.write(character);
                        character = br.read();
                    }

                    fos.close();
                } catch (Exception e) {
                    fos.close();
                }
            } else {
                if (command.equals("GET")) {
                    try {
                        sendBytes(fis, os);
                    } catch (Exception e) {
                        System.out.println("Exception raised");
                    }
                }
                fis.close();
            }
        } else {
            System.out.println(entityBody);
            os.writeBytes(entityBody);
        }

        // Display the request line.
        System.out.println();
        System.out.println(requestLine);

        os.close();
        br.close();
        socket.close();
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copy requested file into the socket's output stream.
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        if (fileName.endsWith(".txt")) {
            return "text/plain";
        }
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/octet-stream";
    }
}