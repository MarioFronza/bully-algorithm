package br.udesc.dsd.ba;

import java.util.ArrayList;
import java.util.List;

public class Server {

    private List<Process> processList;

    private static Server instante;

    private Process currentBoss;

    public static Server getInstance() {
        if (instante == null) {
            instante = new Server();
        }

        return instante;
    }

    private Server() {
        this.processList = new ArrayList<>();
    }


    public void start() {
        for (int i = 0; i < 5; i++) {
            this.processList.add(new Process());
            this.processList.get(i).start();
        }
    }

    public List<Process> getProcessList() {
        return processList;
    }

    public Process getCurrentBoss() {
        return currentBoss;
    }

    public synchronized void verifyBoss(Process process) {
        Process boss = null;
        System.out.println("Processo " + process.getId() + " está verificando se existe um coordenador");
        for (Process currentProcess : processList) {
            if (currentProcess.isBoss() && currentProcess.isRunning()) {
                boss = currentProcess;
            }
        }

        if (boss == null) {
            System.out.println("Não existe coordenador, " + process.getId() + " vai iniciar uma eleição");
            elect(process);
            System.out.println(currentBoss.getId() + " é o novo coordenador");
        } else {
            System.out.println("O processo " + boss.getId() + " é o coordenador");
        }
    }

    public void elect(Process process) {
        Process currentProcess;
        for (int i = 0; i < 5; i++) {
            if (process.getId() < processList.get(i).getId()) {
                if (processList.get(i).response().equals("ok")) {
                    currentProcess = processList.get(i);
                    processList.get(i).setBoss(true);
                    processList.get(i).setRunning(true);
                    currentBoss = processList.get(i);
                    System.out.println("Election message is sent from " + process.getId() + " to " + currentProcess.getId());
                    if (currentProcess.isRunning())
                        elect(currentProcess);
                } else {
                    currentProcess = processList.get(i);
                    System.out.println("Election message is not sent from " + process.getId() + " to " + currentProcess.getId());
                }
            }
        }
    }

    public synchronized void killBos() {
        for (int i = 0; i < 5; i++) {
            if (processList.get(i).isBoss()) {
                this.processList.get(i).setBoss(false);
            }
        }
        this.currentBoss = null;
    }
}
