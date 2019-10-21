package br.udesc.dsd.ba;

import br.udesc.dsd.ba.observer.Observer;

import java.util.Random;

public class Process extends Thread implements Observer {

    private int id;

    private Random random;
    private Server server;


    public Process(int id) {
        this.id = id;
        this.random = new Random();
        this.server = Server.getInstance();
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            verifyBoss();
            waitRandomTime();
        }
    }

    public void verifyBoss() {
        if (server.getCurrentBoss() == id) {
            System.out.println("Eu sou o coordenador");
        } else {
            server.verifyWhoIsTheBoss(Constants.WHO_IS_THE_BOSS, id);
        }
    }

    public void waitRandomTime() {
        try {
            Thread.sleep(3000 + random.nextInt(5000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bossNotFound() {
        System.out.println("Iniciando eleição...");
        server.startElection(id);  //Agora o processo deve iniciar uma eleição
    }

    @Override
    public void imTheBoss() {
        System.out.println("Eu sou o novo coordenador");
        server.setCurrentBoss(id);
        server.sendMessageToAllProcess(Constants.BOSS_MESSAGE, id);
    }

    @Override
    public void electionMessage(int sourceId) {
        System.out.println("Enviando " + Constants.OK + " para o processo: " + sourceId);

        server.sendMessage(new Message(id, sourceId, Constants.OK), false);
        server.startElection(id);
    }

    @Override
    public void newBoss(int bossId) {
        if (bossId == id) {
            System.out.println("Eu sou o coordenador");
        } else {
            System.out.println("O processo " + bossId + " é o coordenador");
        }
        server.setFirstElection(false);
    }

    @Override
    public void checkIfImTheBoss(int sourceId) {
        if (server.getCurrentBoss() == id) {
            server.sendMessage(new Message(id, sourceId, Constants.BOSS_MESSAGE), true);
        } else {
            server.sendMessage(new Message(id, sourceId, Constants.IM_NOT_THE_BOSS_MESSAGE), true);
        }
    }
}
