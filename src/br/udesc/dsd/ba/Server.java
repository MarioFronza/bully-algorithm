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

    private boolean firstElection;
    public boolean verifyElection;

    private Socket client;
    private Socket targetClient;
    private ServerSocket serverSocket;
    private Random random;
    private ObjectOutputStream outputStream;

    private List<Observer> observerList;

    private int countOfResponseMessages;
    private int countOfUnsentMessages;

    private int countOfUnsetElectionMessages;
    private int countOfResponseElectionMessages;
    private int countOfElectionMessages;

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private Server() {
        this.client = null;
        this.countOfResponseMessages = 0;
        this.countOfUnsentMessages = 0;
        this.countOfElectionMessages = 0;
        this.countOfResponseElectionMessages = 0;
        this.countOfUnsetElectionMessages = 0;
        this.currentBoss = -1;
        this.firstElection = false;
        this.verifyElection = false;
        this.random = new Random();

        this.observerList = new ArrayList<>();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setCurrentBoss(int currentBoss) {
        this.currentBoss = currentBoss;
    }

    public int getCurrentBoss() {
        return currentBoss;
    }

    public void setFirstElection(boolean firstElection) {
        this.firstElection = firstElection;
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

                new Thread(() -> validateMessage(message)).start();

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void validateMessage(Message message) {
        if (message.getMessage().equals(Constants.WHO_IS_THE_BOSS))
            notifyWhoIsTheBossMessage(message.getSource());

        if (message.getMessage().equals(Constants.IM_NOT_THE_BOSS_MESSAGE)) {
            countOfResponseMessages++;
        }

        if (message.getMessage().equals(Constants.OK)) {
            countOfResponseElectionMessages++;
        }

        if (message.getMessage().equals(Constants.ELECTION_MESSAGE)) {
            notifyElectionMessage(message.getSource());
        }

        if (message.getMessage().equals(Constants.BOSS_MESSAGE)) {
            currentBoss = message.getSource();
            countOfResponseMessages++;
            notifyNewBoss(message.getSource());
        }
    }


    public void resetCountMessages() {
        countOfUnsentMessages = 0;
        countOfResponseMessages = 0;
    }

    public void resetCountElectionMessages() {
        countOfElectionMessages = 0;
        countOfUnsetElectionMessages = 0;
        countOfResponseElectionMessages = 0;
    }


    public void startElection(int id) {
        if (!firstElection && currentBoss == -1) {
            resetCountElectionMessages();
            for (int i = 0; i < Constants.ports.length; i++) {
                if (i + 1 > id) {
                    countOfElectionMessages++;
                    sendMessage(new Message(id, i + 1, Constants.ELECTION_MESSAGE), true);
                }
            }
            new Thread(() -> verifyElectionResult()).start();
            firstElection = true;
        }
    }

    public void verifyWhoIsTheBoss(String message, int sourceId) {
        new Thread(() -> verifyAndNotifyNewBoss(message, sourceId)).start();
    }

    public void verifyAndNotifyNewBoss(String message, int sourceId) {
        if (!verifyElection) {
            currentBoss = -1;
            resetCountMessages();

            sendMessageToAllProcess(message, sourceId);
            boolean findBoss = false;

            while (!findBoss) {
                int responseCount = countOfResponseMessages;
                int errorCount = countOfUnsentMessages;
                waitRandomTime();

                if (responseCount + errorCount == Constants.ports.length - 1) {
                    if (currentBoss == -1) {
                        notifyBossNotFound(); // nenhum coordenador encontrado
                    } else {
                        notifyNewBoss(currentBoss);
                    }
                    findBoss = true;
                }
            }
        }
    }

    private void verifyElectionResult() {
        verifyElection = true;
        boolean hasResult = false;
        while (!hasResult) {
            int responseElectionCount = countOfResponseElectionMessages;
            int unsetElectionCount = countOfUnsetElectionMessages;
            int processCount = countOfElectionMessages;
            waitRandomTime();
            if (responseElectionCount + unsetElectionCount == processCount) {
                if (responseElectionCount == 0) {
                    notifyImTheNewBoss();
                }
                hasResult = true;
            }


        }
        verifyElection = false;
    }

    public void waitRandomTime() {
        try {
            Thread.sleep(1000 + random.nextInt(3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToAllProcess(String message, int sourceId) {
        for (int i = 0; i < Constants.ports.length; i++) {
            if (i + 1 != sourceId) {
                sendMessage(new Message(sourceId, i + 1, message), false);
            }
        }
    }

    public void sendMessage(Message message, boolean isElection) {
        try {
//            targetClient = new Socket("localhost", Constants.ports[message.getTarget() - 1]);
            targetClient = new Socket(Constants.ips[message.getTarget() - 1], Constants.ports[message.getTarget() - 1]);
            outputStream = new ObjectOutputStream(targetClient.getOutputStream());
            outputStream.writeObject(message);
            targetClient.close();
        } catch (IOException ex) {
            if (isElection) {
                countOfUnsetElectionMessages++;
            } else {
                countOfUnsentMessages++;
            }
        }
    }

    public void addObserver(Observer observer) {
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
