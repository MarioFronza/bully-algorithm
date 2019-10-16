package br.udesc.dsd.ba.old;

import java.util.ArrayList;
import java.util.List;

public class Test {

    private List<OldProcess> oldProcessList;

    private static Test instante;

    private OldProcess currentBoss;

    public static Test getInstance() {
        if (instante == null) {
            instante = new Test();
        }

        return instante;
    }

    private Test() {
        this.oldProcessList = new ArrayList<>();
    }


    public void start() {
        for (int i = 0; i < 5; i++) {
            this.oldProcessList.add(new OldProcess());
            this.oldProcessList.get(i).start();
            this.currentBoss = null;
        }
    }


    public synchronized void verifyBoss(OldProcess oldProcess) {
        System.out.println(oldProcess.getId() + " está verificando se existe um coordenador");
        if (currentBoss == null) {
            System.out.println("Não existe coordenador, " + oldProcess.getId() + " vai iniciar uma eleição");
            elect(oldProcess);
            System.out.println(currentBoss.getId() + " é o novo coordenador");
        } else {
            System.out.println("O processo " + currentBoss.getId() + " é o coordenador");
        }
    }

    public void elect(OldProcess oldProcess) {
        for (int i = 0; i < 5; i++) {
            if (oldProcess.getId() < oldProcessList.get(i).getId()) {
                if (oldProcessList.get(i).response().equals("ok")) {
                    oldProcessList.get(i).setBoss(true);
                    oldProcessList.get(i).setRunning(true);
                    currentBoss = oldProcessList.get(i);
                    System.out.println("Election message is sent from " + oldProcess.getId() + " to " + currentBoss.getId());
                    elect(currentBoss);
                } else {
                    currentBoss = oldProcessList.get(i);
                    System.out.println("Election message is not sent from " + oldProcess.getId() + " to " + currentBoss.getId());
                }
            }
        }
    }

    public synchronized void killBos() {
        if(currentBoss != null){
            for (int i = 0; i < 5; i++) {
                if (oldProcessList.get(i).isBoss()) {
                    this.oldProcessList.get(i).setBoss(false);
                    this.oldProcessList.get(i).setRunning(false);
                }
            }
            System.out.println("O coordenador " + currentBoss.getId() + " parou de funcionar");
            this.currentBoss = null;
        }
    }
}
