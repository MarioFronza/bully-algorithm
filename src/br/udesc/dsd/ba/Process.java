package br.udesc.dsd.ba;

import br.udesc.dsd.ba.observer.Observer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class Process extends Thread implements Observer {

    private int id;
    private int currentBoss;
    private int numberOfResponse;
    private boolean isElection;

    private Socket socket;
    private ObjectOutputStream outputStream;
    private Random random;


    public Process(int id) {
        this.id = id;
        this.numberOfResponse = 0;
        this.currentBoss = -1;
        this.isElection = false;
        this.random = new Random();
    }

    @Override
    public void run() {
        super.run();
        System.out.println("Processo " + id + " rodando...");
        sendMessageToAllProcess(Constants.WHO_IS_THE_BOSS);
        while (true) {
            waitRandomTime();
            verifyBoss();
        }
    }

    public void verifyBoss() {
        if (currentBoss == id) {
            System.out.println("Eu sou o coordenador");
        } else {
            currentBoss = -1;
            sendMessageToAllProcess(Constants.WHO_IS_THE_BOSS);
            waitRandomTime();
            if (currentBoss == -1) {
                if (!isElection) {
                    System.out.println("Não existe coordenador, o processo " + id + " vai iniciar uma eleição");
                    election();
                    verifyElectionResult();
                }
            } else {
                System.out.println("O processo " + currentBoss + " é o coordenador");
            }
        }
    }

    public void sendMessageToAllProcess(String message) {
        for (int i = 0; i < Constants.ports.length; i++) {
            if (i + 1 != id) {
                sendMessage(new Message(id, i + 1, message));
            }
        }
    }

    public void election() {
        for (int i = 0; i < Constants.ports.length; i++) {
            if (i + 1 > id) {
                sendMessage(new Message(id, i + 1, Constants.ELECTION_MESSAGE));
            }
        }
    }

    public void waitRandomTime() {
        try {
            Thread.sleep(100 + random.nextInt(5000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            socket = new Socket("localhost", Constants.ports[message.getTarget() - 1]);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(message);
            socket.close();
        } catch (IOException ex) {
//            System.out.println("Erro ao enviar mensagem");
        }
    }

    public void verifyElectionResult() {
        waitRandomTime();
        if (numberOfResponse == 0) {
            System.out.println("Eu sou o novo coordenador");
            currentBoss = id;
            sendMessageToAllProcess(Constants.BOSS_MESSAGE);
        } else {
            numberOfResponse = 0;
            System.out.println("Não serei o coordenador :(");
        }
        isElection = false;
    }

    @Override
    public void electionMessage(int sourceId) {
        sendMessage(new Message(id, sourceId, Constants.RESPONSE_MESSAGE));
        if (!isElection) {
            isElection = true;
            System.out.println("Respondi e agora eu irei iniciar a eleição");
            election();
            verifyElectionResult();
        }
    }

    @Override
    public void responseMessage() {
        numberOfResponse++;
    }

    @Override
    public void newBossMessage(int sourceId) {
        isElection = false;
        numberOfResponse = 0;
        currentBoss = sourceId;
    }

    @Override
    public void checkIfImTheBoss(int sourceId) {
        if (currentBoss == id) {
            sendMessage(new Message(id, sourceId, Constants.BOSS_MESSAGE));
        }
    }
}
