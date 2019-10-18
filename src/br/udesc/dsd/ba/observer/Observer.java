package br.udesc.dsd.ba.observer;

public interface Observer {

    void electionMessage(int sourceId);

    void newBossMessage(int sourceId);

    void checkIfImTheBoss(int sourceId);

    void responseMessage();
}
