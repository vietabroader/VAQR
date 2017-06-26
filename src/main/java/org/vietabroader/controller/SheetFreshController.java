package org.vietabroader.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vietabroader.model.GlobalState;
import org.vietabroader.model.VASpreadsheet;

import javax.swing.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

public class SheetFreshController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(SheetFreshController.class);

    private JButton btnRefresh;
    private JComboBox comSheets;
    private JTextField txtRowFrom;
    private JTextField txtRowTo;
    private JLabel lblSheetMessage;
    private HashMap<String, JTextField> columnArray = new HashMap<>();

    public SheetFreshController setButtonRefresh(JButton btn) {
        btnRefresh = btn;
        return this;
    }

    public SheetFreshController setComSheets(JComboBox com) {
        comSheets = com;
        return this;
    }

    public SheetFreshController setRowFields(JTextField from, JTextField to) {
        txtRowFrom = from;
        txtRowTo = to;
        return this;
    }

    public SheetFreshController setSheetMessage(JLabel sheet) {
        lblSheetMessage = sheet;
        return this;
    }

    public SheetFreshController setColumnArray(String column, JTextField txt) {
        columnArray.put(column, txt);
        return this;
    }

    @Override
    public void control() {
        btnRefresh.addActionListener(e -> {
            GlobalState currentState = GlobalState.getInstance();
            VASpreadsheet spreadsheet = currentState.getSpreadsheet();

            spreadsheet.removeAllColumn();

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

            (new RefreshWorker(lblSheetMessage)).execute();
        });
    }

    private static class RefreshWorker extends SwingWorker<String, Object> {
        private JLabel lbl;

        RefreshWorker(JLabel lbl) {
            this.lbl = lbl;
        }

        @Override
        protected String doInBackground() throws Exception {
            GlobalState currentState = GlobalState.getInstance();
            VASpreadsheet spreadsheet = currentState.getSpreadsheet();
            logger.info("Start to refresh the sheet" + spreadsheet.getSpreadsheetTitle());
            try {
                spreadsheet.refreshAllColumns();
            } catch (Exception e) {
                logger.error("Cannot refresh the sheet" + e);
            }
            return "";
        }

        protected void done() {
            long now = Calendar.getInstance().getTimeInMillis();
            lbl.setText("The sheet has been refreshed at " + new Timestamp(now));
            logger.info("The sheet has been refreshed");
        }
    }
}
