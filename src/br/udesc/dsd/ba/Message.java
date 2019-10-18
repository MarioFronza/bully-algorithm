package br.udesc.dsd.ba;

import java.io.Serializable;

public class Message implements Serializable {

    private int source;
    private int target;
    private String message;

    public Message(int source, int target, String message) {
        this.source = source;
        this.target = target;
        this.message = message;
    }

    public int getSource() {
        return source;
    }

    public int getTarget() {
        return target;
    }

    public String getMessage() {
        return message;
    }
}
