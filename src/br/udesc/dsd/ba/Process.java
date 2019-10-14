package br.udesc.dsd.ba;

import java.util.List;

public class Process extends Thread {

    private boolean isBoss;
    private boolean isRunning;

    private List<Process> processList;

    private Process currentBoss;

    private Server server;

    private int task1;
    private int task2;
    private int task3;
    private int task4;

    public Process() {
        this.isBoss = false;
        this.isRunning = true;
        this.server = Server.getInstance();
        this.processList = Server.getInstance().getProcessList();
    }


    public boolean isBoss() {
        return isBoss;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String response() {
        if (isRunning) {
            return "ok";
        }
        return "";
    }

    public void setBoss(boolean boss) {
        isBoss = boss;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void stopProcess() {
        this.isRunning = false;
        System.out.println("Processo" + this.getId() + "parou de funcionar");
    }

    public void restartProcess() {
        this.isRunning = true;
        System.out.println("Processo" + this.getId() + "voltou funcionar");
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            if (task1 == 5) {
                this.server.verifyBoss(this);
                this.task1 = 0;
            }

            if (task2 == 10) {
                stopProcess();
                task2 = 0;
            }

            if (task3 == 20) {
                restartProcess();
                task3 = 0;
            }

            if (task4 == 7) {
                this.server.killBos();
                task4 = 0;
            }

            if (isRunning) {

            }

            this.task1++;
            this.task2++;
            this.task3++;
            this.task4++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
