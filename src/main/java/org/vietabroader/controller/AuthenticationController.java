package org.vietabroader.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vietabroader.GoogleAPIUtils;
import org.vietabroader.model.GlobalState;

import javax.swing.*;

public class AuthenticationController implements Controller {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private JButton btnAuthenticate;
    private JProgressBar prgIndicator;

    public AuthenticationController setButtonAuthenticate(JButton btn) {
        btnAuthenticate = btn;
        return this;
    }

    public AuthenticationController setProgressIndicator(JProgressBar prg) {
        prgIndicator = prg;
        return this;
    }

    @Override
    public void control() {
        btnAuthenticate.addActionListener(e -> {
            GlobalState currentState = GlobalState.getInstance();
            if (currentState.getStatus() == GlobalState.Status.SIGNED_OUT) {
                prgIndicator.setIndeterminate(true);
                (new SignInWorker()).execute();
            }
            else {
                GoogleAPIUtils.signOut();
                currentState.setStatus(GlobalState.Status.SIGNED_OUT);
                currentState.setUserEmail("");
            }
        });
    }

    private class SignInWorker extends SwingWorker<String, Object> {
        @Override
        protected String doInBackground() throws Exception {
            return GoogleAPIUtils.signInAndGetEmail();
        }

        @Override
        protected void done() {
            GlobalState currentState = GlobalState.getInstance();
            try {
                String email = get();
                currentState.setUserEmail(email);
                currentState.setStatus(GlobalState.Status.SIGNED_IN);
            }
            catch (Exception ex) {
                GoogleAPIUtils.signOut();
                currentState.setStatus(GlobalState.Status.SIGNED_OUT);
                logger.error("Cannot sign in", ex);
            }
            prgIndicator.setIndeterminate(false);
        }
    }
}
