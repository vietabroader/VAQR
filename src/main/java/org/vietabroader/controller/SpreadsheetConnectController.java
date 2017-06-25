package org.vietabroader.controller;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.vietabroader.model.GlobalState;
import org.vietabroader.model.VASpreadsheet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpreadsheetConnectController implements Controller {

    private static final Logger logger = LoggerFactory.getLogger(SpreadsheetConnectController.class);

    private JButton btnSpreadsheetConnect;
    private JTextField txtSpreadsheetConnect;
    private JLabel lblSpreadsheetMessage;

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

    @Override
    public void control() {
        btnSpreadsheetConnect.addActionListener(e -> {

            GlobalState currentState = GlobalState.getInstance();
            if (currentState.getStatus() == GlobalState.Status.SIGNED_IN
                    || currentState.getStatus() == GlobalState.Status.CONNECTED) {
                String spreadsheetId = txtSpreadsheetConnect.getText().trim();
                if (spreadsheetId.isEmpty()) {
                    lblSpreadsheetMessage.setBackground(Color.YELLOW);
                    lblSpreadsheetMessage.setForeground(Color.BLACK);
                    lblSpreadsheetMessage.setText("Please enter a spreadsheet Id.");
                    return;
                }
                boolean hasError = false;
                try {
                    VASpreadsheet spreadsheet = new VASpreadsheet(spreadsheetId);
                    spreadsheet.connect();

                    currentState.setSpreadsheet(spreadsheet);
                    currentState.setStatus(GlobalState.Status.CONNECTED);

                    String spreadsheetTitle = spreadsheet.getSpreadsheetTitle();
                    logger.debug("Connected to sheet: " + spreadsheetTitle);
                }
                catch (GoogleJsonResponseException ex) {
                    currentState.setStatus(GlobalState.Status.SIGNED_IN);
                    hasError = true;
                    lblSpreadsheetMessage.setText(ex.getDetails().getMessage());
                    logger.error("Error while loading spreadsheet", ex);
                }
                catch (Exception ex) {
                    currentState.setStatus(GlobalState.Status.SIGNED_IN);
                    hasError = true;
                    lblSpreadsheetMessage.setText("Cannot connect to the spreadsheet.");
                    logger.error("Error while loading spreadsheet", ex);
                }
                if (hasError) {
                    lblSpreadsheetMessage.setBackground(Color.RED);
                    lblSpreadsheetMessage.setForeground(Color.WHITE);
                }
            }
        });
    }
}
