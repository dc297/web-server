package com.dc297;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    public static void main(String[] args) {
        try {
            ServerConfig serverConfig = ConfigParser.parse(args);
            ServerSocket serverSocket = new ServerSocket(serverConfig.port);
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(serverConfig.numThreads);

            System.out.println("Server started at port " + serverConfig.port + " with " + serverConfig.numThreads + " threads");

            while (true) {
                Socket requestSocket = serverSocket.accept();
                System.out.println("New connection");
                processRequest(requestSocket, executor, serverConfig);
            }
        }
        catch (IOException ioException){
            System.out.println("Failed to start the server");
            ioException.printStackTrace();
        }
    }

    private static void processRequest(Socket requestSocket, ThreadPoolExecutor executor, ServerConfig serverConfig) {
        HttpRequest request = new HttpRequest(requestSocket, serverConfig);
        executor.execute(request);
    }
}
