package br.udesc.dsd.ba;

import br.udesc.dsd.ba.observer.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private Socket client;
    private ServerSocket serverSocket;
    private int port;

    private List<Observer> observerList;

    public Server(int port) {
        this.client = null;
        this.port = port;
        this.observerList = new ArrayList<>();
    }

    @Override
    public void run() {
        super.run();
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Server rodando na porta: " + port);
            while (true) {
                this.client = serverSocket.accept();

                ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                Message message = (Message) objectInputStream.readObject();

                if (message.getMessage().equals(Constants.ELECTION_MESSAGE))
                    notifyElectionMessage(message.getSource());

                if (message.getMessage().equals(Constants.RESPONSE_MESSAGE))
                    notifyResponseMessage();

                if (message.getMessage().equals(Constants.NEW_BOSS_MESSAGE))
                    notifyNewBossMessage(message.getSource());

                if (message.getMessage().equals(Constants.VERIFY_BOSS))
                    notifyVerifyBossMessage(message.getSource());

                if (message.getMessage().equals(Constants.ID_BOSS_MESSAGE))
                    notifyIdBossMessage(message.getSource());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }




    public void addObserver(Observer observer) {
        this.observerList.add(observer);
    }

    public void notifyElectionMessage(int sourceId) {
        for (Observer observer : observerList) {
            observer.electionMessage(sourceId);
        }
    }

    public void notifyResponseMessage() {
        for (Observer observer : observerList) {
            observer.responseMessage();
        }
    }

    private void notifyNewBossMessage(int sourceId) {
        for (Observer observer : observerList) {
            observer.newBossMessage(sourceId);
        }
    }

    private void notifyVerifyBossMessage(int sourceId) {
        for (Observer observer : observerList) {
            observer.verifyNewBoss(sourceId);
        }
    }

    private void notifyIdBossMessage(int sourceId) {
        for (Observer observer : observerList) {
            observer.idBossMessage(sourceId);
        }
    }


}
