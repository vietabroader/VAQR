package org.vietabroader.controller;

import org.vietabroader.GoogleAPIUtils;
import org.vietabroader.model.GlobalState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthenticationController implements Controller {

    private JButton btnAuthenticate;

    public AuthenticationController setButtonAuthenticate(JButton btn) {
        btnAuthenticate = btn;
        return this;
    }

    @Override
    public void control() {
        btnAuthenticate.addActionListener(e -> {
            GlobalState currentState = GlobalState.getInstance();
            if (currentState.getStatus() == GlobalState.Status.SIGNED_OUT) {
                try {
                    String email = GoogleAPIUtils.signInAndGetEmail();
                    currentState.setUserEmail(email);
                    currentState.setStatus(GlobalState.Status.SIGNED_IN);
                } catch (Exception ex) {
                    // Ensure that we are signed out when there is error
                    currentState.setStatus(GlobalState.Status.SIGNED_OUT);
                    // TODO: Report error on the UI
                    ex.printStackTrace();
                }
            }
            else if (currentState.getStatus() == GlobalState.Status.SIGNED_IN
                    || currentState.getStatus() == GlobalState.Status.CONNECTED) {
                GoogleAPIUtils.signOut();
                currentState.setStatus(GlobalState.Status.SIGNED_OUT);
                currentState.setUserEmail("");
            }
        });
    }
}
