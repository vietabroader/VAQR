package org.vietabroader.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vietabroader.GoogleAPIUtils;
import org.vietabroader.model.GlobalState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthenticationController implements Controller {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

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
                    GoogleAPIUtils.signOut();
                    currentState.setStatus(GlobalState.Status.SIGNED_OUT);
                    logger.error("Cannot sign in", ex);
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
