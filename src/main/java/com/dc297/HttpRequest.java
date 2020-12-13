package com.dc297;

import org.apache.tika.Tika;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

public class HttpRequest implements Runnable{

    private final Socket socket;
    private final ServerConfig serverConfig;
    String CRLF = "\r\n";
    private final int keepAliveTimeInSeconds = 5;
    private final String keepAliveHeader = "Connection: Keep-Alive" + CRLF + "Keep-Alive: timeout=" + keepAliveTimeInSeconds + ", max=1000" + CRLF;

    private final HashMap<String, String> headers = new HashMap<>();
    private String method;
    private String path;
    private boolean keepAlive = true;
    OutputStream outputStream;
    BufferedReader reader;


    public HttpRequest(Socket socket, ServerConfig serverConfig){
        this.socket = socket;
        this.serverConfig = serverConfig;
        try {
            outputStream = this.socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            while(keepAlive){
                System.out.println("Processing request");
                this.socket.setSoTimeout(keepAliveTimeInSeconds * 1000);
                processRequest();
            }
        }
        catch(SocketTimeoutException ste){
            System.out.println("Client disconnected");
        }
        catch (IOException ioException){
            System.out.println("Error while processing the request");
            ioException.printStackTrace();
            try {
                sendResponse(500, "Internal Server Error", null);
            } catch (IOException e) {
                System.out.println("FATAL! Failed while sending error response.");
                e.printStackTrace();
            }
        }
        finally{
            try{
                this.socket.close();
            }
            catch(IOException ioException){
                System.out.println("Exception while closing the connection");
                ioException.printStackTrace();
            }
        }
    }

    private void processRequest() throws IOException{
        parseRequest();

        if(!method.equals("GET")){
            sendResponse(405, "Method Not Allowed", null);
            return;
        }

        File file = getFile(path);

        if(!file.exists() || !file.isFile()){
            sendResponse(404, "Not Found", null);
            return;
        }

        sendResponse(200, "OK", file);
    }

    private void parseRequest() throws IOException {
        // Expected value [GET, /path.ext, HTTP/1.1]
        String[] params = reader.readLine().split(" ");

        this.method = params[0];
        this.path = params[1];

        System.out.println("Processing " + this.path);

        String line = reader.readLine();

        while(!line.isEmpty()) {
            String[] header = line.split(": ");
            headers.put(header[0], header[1]);
            line = reader.readLine();
        }

        this.keepAlive = "keep-alive".equals(headers.get("Connection"));
        System.out.println("New keepalive: " + this.keepAlive);
    }

    private File getFile(String filePath) {
        String directory = serverConfig.baseDirectory + filePath.substring(0, filePath.lastIndexOf('/'));
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);

        return new File(directory, fileName);
    }


    private void sendResponse(int code, String message, File file) throws IOException {
        String statusLine = "HTTP/1.1 " + code + " " + message + CRLF;

        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));
        outputStream.write(keepAliveHeader.getBytes(StandardCharsets.UTF_8));

        if(file != null){
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String contentTypeHeaderLine = "Content-type: " + getContentType(file) + CRLF;
            outputStream.write(contentTypeHeaderLine.getBytes(StandardCharsets.UTF_8));
            String contentLengthLine = "Content-Length: " + fileBytes.length + CRLF;
            outputStream.write(contentLengthLine.getBytes(StandardCharsets.UTF_8));
            outputStream.write(CRLF.getBytes(StandardCharsets.UTF_8));
            outputStream.write(fileBytes);
        }
        else{
            String contentLengthLine = "Content-Length: 0" + CRLF;
            outputStream.write(contentLengthLine.getBytes(StandardCharsets.UTF_8));
            outputStream.write(CRLF.getBytes(StandardCharsets.UTF_8));
        }

        outputStream.flush();
    }

    private String getContentType(File file) throws IOException {
        Tika tika = new Tika();
        return tika.detect(file);
    }


}
