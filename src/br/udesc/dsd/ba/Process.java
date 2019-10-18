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
    private boolean isBoss;
    private boolean isElection;


    private Socket socket;
    private ObjectOutputStream outputStream;
    private Random random;


    public Process(int id) {
        this.id = id;
        this.numberOfResponse = 0;
        this.isElection = false;
        this.isBoss = false;
        this.currentBoss = -1;
        this.random = new Random();

    }

    @Override
    public void run() {
        super.run();
        System.out.println("Cliente " + id + " rodando...");
        while (true) {
            if (!isElection) {
                verifyBoss();
            }
            try {
                Thread.sleep(2000 + random.nextInt(3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void verifyBoss() {
        System.out.println("Processo " + id + " está verificando se existe um coordenador");
        if (isBoss) {
            System.out.println("Eu sou o coordenador");
        } else {
            sendMessageToAllProcess(Constants.VERIFY_BOSS);
            if (currentBoss == -1) {
                System.out.println("Não existe coordenador, o processo " + id + " vai iniciar uma eleição");
                election();
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

    public void election() {
        for (int i = 0; i < Constants.ports.length; i++) {
            if (i + 1 > id) {
                sendMessage(new Message(id, i + 1, Constants.ELECTION_MESSAGE));
            }
        }
        if (numberOfResponse == 0) {
            System.out.println("Eu sou o novo coordenador");
            isBoss = true;
            currentBoss = id;
            isElection = false;
            numberOfResponse = 0;
            sendMessageToAllProcess(Constants.NEW_BOSS_MESSAGE);
        } else {
            System.out.println("Não serei o coordenador :(");
        }
    }


    @Override
    public void electionMessage(int sourceId) {
        isElection = true;
        sendMessage(new Message(id, sourceId, Constants.RESPONSE_MESSAGE));
        election();
    }

    @Override
    public void responseMessage() {
        numberOfResponse++;
    }

    @Override
    public void newBossMessage(int sourceId) {
        isElection = false;
        isBoss = false;
        numberOfResponse = 0;
        currentBoss = sourceId;
    }

    @Override
    public void idBossMessage(int sourceId) {
        currentBoss = sourceId;
    }

    @Override
    public void verifyNewBoss(int sourceId) {
        if (isBoss) {
            sendMessage(new Message(id, sourceId, Constants.ID_BOSS_MESSAGE));
        }
    }
}
