package br.udesc.dsd.ba.observer;

public interface Observer {


    void checkIfImTheBoss(int sourceId);

    void bossNotFound();

    void imTheBoss();

    void electionMessage(int sourceId);

    void newBoss(int bossId);
}
