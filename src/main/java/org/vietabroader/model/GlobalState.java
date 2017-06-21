package org.vietabroader.model;

import java.util.Observable;

public class GlobalState extends Observable {

    // Singleton Pattern
    private static GlobalState singleton = new GlobalState();

    /**
     * Make constructor private to prevent others from initiating
     * Also set initial values here
     */
    private GlobalState() {
        status = Status.SIGNED_OUT;
    }

    public static GlobalState getInstance() {
        return singleton;
    }

    public enum Status {
        SIGNED_OUT,
        SIGNED_IN,
        CONNECTED,
        QR_READING
    }

    private Status status;
    private String userEmail;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status s) {
        status = s;
        setChanged();
        notifyObservers();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String email) {
        userEmail = email;
        setChanged();
    }
}
