package br.udesc.dsd.ba.old;

import java.util.Random;

public class OldProcess extends Thread {

    private boolean isBoss;
    private boolean isRunning;
    private Random random;

    private Test test;

    private int task1;
    private int task2;
    private int task3;
    private int task4;

    public OldProcess() {
        this.isBoss = false;
        this.isRunning = true;
        this.test = Test.getInstance();
    }


    public boolean isBoss() {
        return isBoss;
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
        if (isRunning) {
            this.isRunning = false;
            System.out.println("Processo " + this.getId() + " parou de funcionar");
        }
    }

    public void restartProcess() {
        if(!isRunning){
            this.isRunning = true;
            System.out.println("Processo " + this.getId() + " voltou funcionar");
        }
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            if (task1 == 5) {
                this.test.verifyBoss(this);
                this.task1 = 0;
            }

            if (task2 == 10) {
                stopProcess();
                task2 = 0;
            }

            if (task3 == 15) {
                restartProcess();
                task3 = 0;
            }

            if (task4 == 20) {
                this.test.killBos();
                task4 = 0;
            }

            if (isRunning) {

            }

            this.task1++;
            this.task2++;
            this.task3++;
            this.task4++;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
