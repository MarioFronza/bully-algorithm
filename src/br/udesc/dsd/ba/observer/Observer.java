package br.udesc.dsd.ba.observer;

public interface Observer {

    void electionMessage(int sourceId);

    void newBossMessage(int sourceId);

    void verifyNewBoss(int sourceId);

    void responseMessage();

    void idBossMessage(int sourceId);
}
