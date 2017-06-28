package org.vietabroader.model;

import java.util.Observable;

/**
 * This class contains the global state and data of the app such as whether the user has
 * signed in, connected to a spreadsheet and so on; data includes but may not limited to
 * user email address and connected spreadsheet. As this class is implemented as an Observable,
 * an Observer may also observe the change in this class.
 */
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
        QR_READING,
        REFRESHED
    }

    private Status status;
    private String userEmail;
    private VASpreadsheet spreadsheet;

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

    public VASpreadsheet getSpreadsheet() {
        return spreadsheet;
    }

    public void setUserEmail(String email) {
        userEmail = email;
        setChanged();
    }

    public void setSpreadsheet(VASpreadsheet input) {
        spreadsheet = input;
        setChanged();
    }
}
