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
            while (true) {
                this.client = serverSocket.accept();

                ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                Message message = (Message) objectInputStream.readObject();
                client.setSoTimeout(5000);

                validateInputMessage(message);


            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void validateInputMessage(Message message) {
        if (message.getMessage().equals(Constants.ELECTION_MESSAGE))
            notifyElectionMessage(message.getSource());

        if (message.getMessage().equals(Constants.RESPONSE_MESSAGE))
            notifyResponseMessage();

        if (message.getMessage().equals(Constants.BOSS_MESSAGE))
            notifyNewBossMessage(message.getSource());

        if (message.getMessage().equals(Constants.WHO_IS_THE_BOSS))
            notifyWhoIsTheBossMessage(message.getSource());
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

    private void notifyWhoIsTheBossMessage(int sourceId) {
        for (Observer observer : observerList) {
            observer.checkIfImTheBoss(sourceId);
        }
    }


}
