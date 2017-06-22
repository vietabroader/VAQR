package org.vietabroader.controller;

import org.vietabroader.GoogleAPIUtils;
import org.vietabroader.model.GlobalState;
import org.vietabroader.model.VASpreadsheet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectController implements Controller {

    private JButton btnConnect;
    private JTextField txtConnect;
    private static final Logger logger = LoggerFactory.getLogger(ConnectController.class);

    public ConnectController setConnectButton(JButton btn) {
        btnConnect = btn;
        return this;
    }

    public ConnectController setTextSpreadsheetID(JTextField txt) {
        txtConnect = txt;
        return this;
    }

    @Override
    public void control() {
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GlobalState currentState = GlobalState.getInstance();
                if (currentState.getStatus() == GlobalState.Status.SIGNED_OUT) {

                }
                else if (currentState.getStatus() == GlobalState.Status.SIGNED_IN) {
                    try {
                        VASpreadsheet spreadsheet = new VASpreadsheet(txtConnect.getText());
                        spreadsheet.connect();

                        currentState.setStatus(GlobalState.Status.CONNECTED);
                        currentState.setSpreadsheet(spreadsheet);

                        logger.info("Sheet connected");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });
    }
}
