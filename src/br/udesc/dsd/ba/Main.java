package br.udesc.dsd.ba;


public class Main {


    private static int id;

    public static void main(String[] args) {

        id = Integer.parseInt(args[0]);
        Process process = new Process(id);
        process.start();

        Server server = new Server(Constants.ports[id - 1]);
        server.addObserver(process);
        server.start();

    }
}