package org.vietabroader.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vietabroader.model.GlobalState;
import org.vietabroader.model.VASpreadsheet;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

public class SheetRefreshController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(SheetRefreshController.class);

    private JButton btnRefresh;
    private JComboBox comSheets;
    private JTextField txtRowFrom;
    private JTextField txtRowTo;
    private JLabel lblSheetMessage;
    private HashMap<String, JTextField> columnArray = new HashMap<>();
    private JProgressBar prgIndicator;

    public SheetRefreshController setButtonRefresh(JButton btn) {
        btnRefresh = btn;
        return this;
    }

    public SheetRefreshController setComSheets(JComboBox com) {
        comSheets = com;
        return this;
    }

    public SheetRefreshController setRowFields(JTextField from, JTextField to) {
        txtRowFrom = from;
        txtRowTo = to;
        return this;
    }

    public SheetRefreshController setSheetMessage(JLabel sheet) {
        lblSheetMessage = sheet;
        return this;
    }

    public SheetRefreshController setColumnArray(String column, JTextField txt) {
        columnArray.put(column, txt);
        return this;
    }

    public SheetRefreshController setProgressIndicator(JProgressBar prg) {
        prgIndicator = prg;
        return this;
    }

    @Override
    public void control() {
        btnRefresh.addActionListener(e -> {

            GlobalState currentState = GlobalState.getInstance();

            if (currentState.getStatus() == GlobalState.Status.CONNECTED) {

                VASpreadsheet spreadsheet = currentState.getSpreadsheet();

                spreadsheet.removeAllColumns();

                String selected = (String) comSheets.getSelectedItem();
                spreadsheet.setSheetName(selected);
                logger.info("Sheet " + selected + " is selected");

                int from = Integer.parseInt(txtRowFrom.getText());
                int to = Integer.parseInt(txtRowTo.getText());
                spreadsheet.setRow(from, to);
                logger.info("Row " + from + " " + to);

                for(String key: columnArray.keySet()) {
                    spreadsheet.setColumnChar(key, columnArray.get(key).getText());
                    logger.info(key + " " + columnArray.get(key).getText());
                }

                (new RefreshWorker()).execute();
                prgIndicator.setIndeterminate(true);
            }
        });
    }

    private class RefreshWorker extends SwingWorker<VASpreadsheet, Object> {

        @Override
        protected VASpreadsheet doInBackground() throws Exception {
            VASpreadsheet spreadsheet = GlobalState.getInstance().getSpreadsheet();
            spreadsheet.refreshAllColumns();
            return spreadsheet;
        }

        @Override
        protected void done() {
            try {
                VASpreadsheet spreadsheet = GlobalState.getInstance().getSpreadsheet();
                spreadsheet.refreshAllColumns();

                long now = Calendar.getInstance().getTimeInMillis();
                lblSheetMessage.setText("The sheet has been refreshed at " + new Timestamp(now));
                logger.info("The sheet has been refreshed");

                for (int i = 0; i < 10; i++) {
                    logger.info("Key, " + i + ": " + spreadsheet.readValue("Key", i).toString());
                }
            } catch (Exception e) {
                lblSheetMessage.setBackground(Color.RED);
                lblSheetMessage.setForeground(Color.WHITE);
                lblSheetMessage.setText("The sheet cannot be refreshed ");
                logger.error("Cannot refresh the sheet" + e);
            }
            prgIndicator.setIndeterminate(false);
        }
    }
}
