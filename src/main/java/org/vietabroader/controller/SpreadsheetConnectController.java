package org.vietabroader.controller;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.vietabroader.model.GlobalState;
import org.vietabroader.model.VASpreadsheet;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpreadsheetConnectController implements Controller {

    private static final Logger logger = LoggerFactory.getLogger(SpreadsheetConnectController.class);

    private JButton btnSpreadsheetConnect;
    private JTextField txtSpreadsheetConnect;
    private JLabel lblSpreadsheetMessage;
    private JProgressBar prgIndicator;

    public SpreadsheetConnectController setButtonConnect(JButton btn) {
        btnSpreadsheetConnect = btn;
        return this;
    }

    public SpreadsheetConnectController setTextSpreadsheetID(JTextField txt) {
        txtSpreadsheetConnect = txt;
        return this;
    }

    public SpreadsheetConnectController setLabelSpreadsheetMessage(JLabel lbl) {
        lblSpreadsheetMessage = lbl;
        return this;
    }

    public SpreadsheetConnectController setProgressIndicator(JProgressBar prg) {
        prgIndicator = prg;
        return this;
    }

    @Override
    public void control() {
        btnSpreadsheetConnect.addActionListener(e -> {
            GlobalState currentState = GlobalState.getInstance();
            if (currentState.getStatus() == GlobalState.Status.SIGNED_IN
                    || currentState.getStatus() == GlobalState.Status.CONNECTED
                    || currentState.getStatus() == GlobalState.Status.REFRESHED) {
                String spreadsheetId = txtSpreadsheetConnect.getText().trim();
                if (spreadsheetId.isEmpty()) {
                    lblSpreadsheetMessage.setBackground(Color.YELLOW);
                    lblSpreadsheetMessage.setForeground(Color.BLACK);
                    lblSpreadsheetMessage.setText("Please enter a spreadsheet ID.");
                    return;
                }
                (new SpreadsheetConnectWorker(spreadsheetId)).execute();
                prgIndicator.setIndeterminate(true);
            }
        });
    }

    private class SpreadsheetConnectWorker extends SwingWorker<VASpreadsheet, Object> {
        private String spreadsheetId;

        SpreadsheetConnectWorker(String spreadsheetId) {
            this.spreadsheetId = spreadsheetId;
        }

        @Override
        protected VASpreadsheet doInBackground() throws Exception {
            VASpreadsheet spreadsheet = new VASpreadsheet(spreadsheetId);
            spreadsheet.connect();
            return spreadsheet;
        }

        @Override
        protected void done() {
            GlobalState currentState = GlobalState.getInstance();
            try {
                VASpreadsheet spreadsheet = get();
                currentState.setSpreadsheet(spreadsheet);
                currentState.setStatus(GlobalState.Status.CONNECTED);

                String spreadsheetTitle = spreadsheet.getSpreadsheetTitle();
                logger.debug("Connected to sheet: " + spreadsheetTitle);
            } catch (Exception ex) {
                Throwable innerEx = ex.getCause();
                String errorMessage = "";
                if (innerEx instanceof GoogleJsonResponseException) {
                    GoogleJsonResponseException gJsonEx = (GoogleJsonResponseException)innerEx;
                    errorMessage = gJsonEx.getDetails().getMessage();
                    logger.error("Error while loading spreadsheet", gJsonEx);
                }
                else {
                    errorMessage = "Cannot connect to this spreadsheet.";
                    logger.error("Error while loading spreadsheet", ex);

                }
                currentState.setStatus(GlobalState.Status.SIGNED_IN);
                lblSpreadsheetMessage.setText(errorMessage);
                lblSpreadsheetMessage.setBackground(Color.RED);
                lblSpreadsheetMessage.setForeground(Color.WHITE);
            }
            prgIndicator.setIndeterminate(false);
        }
    }
}
