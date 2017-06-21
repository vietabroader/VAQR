package org.vietabroader.model;

import java.util.Observable;

/**
 * Created by tridinc on 6/20/17.
 */
public class GlobalState extends Observable {
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
        notifyObservers();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String email) {
        userEmail = email;
        notifyObservers();
    }
}
