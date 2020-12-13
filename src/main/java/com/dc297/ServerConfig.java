package com.dc297;

public class ServerConfig {
    public int port;
    public int numThreads;
    public String baseDirectory;

    public ServerConfig(int port, int numThreads, String baseDirectory){
        this.port = port;
        this.numThreads = numThreads;
        this.baseDirectory = baseDirectory;
    }
}
