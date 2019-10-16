package br.udesc.dsd.ba;


public class Main {

    private static int ports[] = {56000, 56001, 56002, 56003, 56004};
    private static int id;

    public static void main(String[] args) {

        id = Integer.parseInt(args[0]);
        Process process = new Process(id);
        process.start();

        Server server = new Server(ports[id - 1]);
        server.startServer();

    }
}