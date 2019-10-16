package br.udesc.dsd.ba;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class Process {

    private int id;
    private boolean isBoss;
    private boolean isRunning;

    private Socket socket;
    private ObjectOutputStream outputStream;
    private Random random;


    private int timeToStopProcess;
    private int timeToRestartProcess;
    private int timeToVerifyBoss;


    public Process(int id) {
        this.id = id;
        this.isBoss = false;
        this.isRunning = true;
        this.random = new Random();
        this.timeToStopProcess = 0;
        this.timeToRestartProcess = 0;
        this.timeToVerifyBoss = 0;
    }

    public void start() {
        System.out.println("Cliente " + id + " rodando...");
        while (true) {
            if (timeToStopProcess == 5) {
                stopProcess();
                this.timeToStopProcess = 0;
            }
            if (timeToRestartProcess == 8) {
                restartProcess();
                this.timeToRestartProcess = 0;
            }

            if (timeToVerifyBoss == 17) {
                verifyBoss();
                this.timeToVerifyBoss = 0;
            }

            try {
                Thread.sleep(random.nextInt(1000) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.timeToStopProcess++;
            this.timeToRestartProcess++;
            this.timeToVerifyBoss++;
        }

    }

    public void stopProcess() {
        if (isRunning) {
            this.isRunning = false;
            System.out.println("Processo " + this.id + " parou de funcionar");
        }
    }

    public void restartProcess() {
        if (!isRunning) {
            this.isRunning = true;
            System.out.println("Processo " + this.id + " voltou funcionar");
        }
    }

    public void verifyBoss() {
        if (isRunning) {
            System.out.println("Processo " + this.id + " está verificando se existe um coordenador");
        }
    }

    public void sendMessage(Message message) {
        try {
            socket = new Socket("localhost", 56001);

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(message);
            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
