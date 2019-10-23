package br.udesc.dsd.ba;

import br.udesc.dsd.ba.observer.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Server extends Thread {

    private static Server instance;

    private int port;
    private int currentBoss;
    private int countOfResponseElectionMessages;
    private int verifyWhoIsTheBossCountLimit;
    private int verifyElectionResultCountLimit;
    private boolean firstElection;

    private Socket client;
    private Random random;
    private List<Observer> observerList;


    static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private Server() {
        this.client = null;
        this.currentBoss = -1;
        this.firstElection = false;
        this.countOfResponseElectionMessages = 0;
        this.verifyWhoIsTheBossCountLimit = 0;
        this.verifyElectionResultCountLimit = 0;
        this.random = new Random();
        this.observerList = new ArrayList<>();
    }

    void setPort(int port) {
        this.port = port;
    }

    void setCurrentBoss(int currentBoss) {
        this.currentBoss = currentBoss;
    }

    int getCurrentBoss() {
        return currentBoss;
    }

    void setFirstElection(boolean firstElection) {
        this.firstElection = firstElection;
    }

    @Override
    public void run() {
        super.run();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                this.client = serverSocket.accept();

                ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                Message message = (Message) objectInputStream.readObject();

                validateMessage(message);

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void validateMessage(Message message) {
        if (message.getMessage().equals(Constants.WHO_IS_THE_BOSS))
            notifyWhoIsTheBossMessage(message.getSource());


        if (message.getMessage().equals(Constants.OK)) {
            countOfResponseElectionMessages++;
        }

        if (message.getMessage().equals(Constants.ELECTION_MESSAGE)) {
            notifyElectionMessage(message.getSource());
        }

        if (message.getMessage().equals(Constants.BOSS_MESSAGE)) {
            notifyNewBoss(message.getSource());
        }
    }


    void startElection(int id) {
        if (!firstElection && currentBoss == -1) {
            System.out.println("Iniciando eleição...");
            for (int i = 0; i < Constants.ports.length; i++) {
                if (i + 1 > id) {
                    sendMessage(new Message(id, i + 1, Constants.ELECTION_MESSAGE));
                }
            }
            verifyElectionResult();
            firstElection = true;
        }
    }


    void verifyWhoIsTheBoss(String message, int sourceId) {
        currentBoss = -1;
        sendMessageToAllLargerProcess(message, sourceId);
        boolean findBoss = false;
        while (!findBoss && verifyWhoIsTheBossCountLimit < 5) {
            waitRandomTime();
            if (currentBoss == -1) {
                notifyBossNotFound(); // nenhum coordenador encontrado
                findBoss = true;
            } else {
                notifyNewBoss(currentBoss);
                findBoss = true;
            }
            verifyWhoIsTheBossCountLimit = 0;
            verifyWhoIsTheBossCountLimit++;
        }
    }


    private void verifyElectionResult() {
        boolean hasResult = false;
        System.out.println("Verificando resultado da eleição...");
        while (!hasResult && verifyElectionResultCountLimit < 5 && currentBoss == -1) {
            waitRandomTime();
            if (countOfResponseElectionMessages == 0 && currentBoss == -1) {
                notifyImTheNewBoss();
                hasResult = true;
            }
            verifyElectionResultCountLimit++;
        }
        verifyElectionResultCountLimit = 0;
        countOfResponseElectionMessages = 0;
    }

    private void waitRandomTime() {
        try {
            Thread.sleep(1000 + random.nextInt(2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void sendMessageToAllProcess(String message, int sourceId) {
        for (int i = 0; i < Constants.ports.length; i++) {
            if (i + 1 != sourceId) {
                sendMessage(new Message(sourceId, i + 1, message));
            }
        }
    }

    private void sendMessageToAllLargerProcess(String message, int sourceId) {
        for (int i = 0; i < Constants.ports.length; i++) {
            if (i + 1 > sourceId) {
                sendMessage(new Message(sourceId, i + 1, message));
            }
        }
    }

    void sendMessage(Message message) {
        try {
            Socket targetClient = new Socket("localhost", Constants.ports[message.getTarget() - 1]);
            //targetClient = new Socket(Constants.ips[message.getTarget() - 1], Constants.ports[message.getTarget() - 1]);
            ObjectOutputStream outputStream = new ObjectOutputStream(targetClient.getOutputStream());
            outputStream.writeObject(message);
            targetClient.close();
        } catch (IOException ignored) {
        }
    }

    void addObserver(Observer observer) {
        this.observerList.add(observer);
    }

    private void notifyImTheNewBoss() {
        for (Observer observer : observerList)
            observer.imTheBoss();
    }

    private void notifyElectionMessage(int sourceId) {
        for (Observer observer : observerList)
            observer.electionMessage(sourceId);
    }

    private void notifyNewBoss(int bossId) {
        for (Observer observer : observerList)
            observer.newBoss(bossId);
    }

    private void notifyWhoIsTheBossMessage(int sourceId) {
        for (Observer observer : observerList)
            observer.checkIfImTheBoss(sourceId);
    }

    private void notifyBossNotFound() {
        for (Observer observer : observerList)
            observer.bossNotFound();
    }


}
