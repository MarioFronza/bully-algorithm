package br.udesc.dsd.ba;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private Socket client;
    private ServerSocket serverSocket;
    private int port;

    public Server(int port) {
        this.client = null;
        this.port = port;
    }

    public void startServer() {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Server rodando na porta: " + port);
            while (true) {
                this.client = serverSocket.accept();
                System.out.println("Conexao estabelecida com " +
                        client.getInetAddress().getHostAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
