package org.vietabroader.view;

import org.vietabroader.GoogleAPIUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Insets;

class MainView extends JFrame {

    MainView() {
        initUI();
    }

    private void initUI() {
        JPanel panelMain = new JPanel();
        panelMain.setLayout(new GridBagLayout());
        getContentPane().add(panelMain);
        GridBagConstraints c = new GridBagConstraints();

        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(4, 4, 4, 4);
        panelMain.add(createSignInPanel(), c);

        c.gridy = 2;
        panelMain.add(createSpreadsheetPanel(), c);

        c.gridy = 3;
        panelMain.add(createWorkspacePanel(), c);

        c.gridy = 4;
        panelMain.add(createColumnPanel(), c);

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 1;
        panelMain.add(createGeneratePanel(), c);

        c.gridx = 1;
        panelMain.add(createWebcamPanel(), c);

        setTitle("VAQR");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private class TitledPanel extends JPanel {
        private TitledPanel(String title) {
            this.setLayout(new GridBagLayout());
            this.setBorder(new CompoundBorder(new TitledBorder(title), new EmptyBorder(8, 8, 8, 8)));
        }
    }

    private JPanel createSignInPanel() {
        TitledPanel panel = new TitledPanel("Account");
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        final JButton btnSignIn = new JButton("Sign In");
        final JLabel lblEmail = new JLabel("Please sign in with your Google account");

        btnSignIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String email = GoogleAPIUtils.signInAndGetEmail();
                    lblEmail.setText(email);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(btnSignIn, c);

        lblEmail.setPreferredSize(new Dimension(300, 15));
        c.gridx = 1;
        c.gridy = 0;
        panel.add(lblEmail, c);

        return panel;
    }

    private JPanel createSpreadsheetPanel() {
        TitledPanel panel = new TitledPanel("Spreadsheet");
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.LINE_START;
        panel.add(new JLabel("Spreadsheet ID: "), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JTextField(15), c);

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 2;
        panel.add(new JButton("Connect"));

        return panel;
    };

    private JPanel createWorkspacePanel() {
        TitledPanel panel = new TitledPanel("Workspace");

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1/6.0;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Sheet: "), c);

        c.gridx = 1;
        c.gridwidth = 3;
        c.weightx = 5/6.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        String[] sheet = {"Sheet1", "Sheet2"};
        panel.add(new JComboBox(sheet), c);


        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1/6.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Column: "), c);

        c.gridx = 1;
        c.weightx = 2/6.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JSpinner(), c);

        c.gridx = 2;
        c.weightx = 1/6.0;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("to"), c);

        c.gridx = 3;
        c.weightx = 2/6.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JSpinner(), c);


        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1/6.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Row: "), c);

        c.gridx = 1;
        c.weightx = 2/6.0;
        c.anchor = GridBagConstraints.LINE_END;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JSpinner(), c);

        c.gridx = 2;
        c.weightx = 1/6.0;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("to"), c);

        c.gridx = 3;
        c.weightx = 2/6.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JSpinner(), c);

        return panel;
    }

    private JPanel createColumnPanel() {
        TitledPanel panel = new TitledPanel("Key Columns");
        GridBagConstraints c = new GridBagConstraints();

        String[] a = {"a", "b"};
        c.weightx = 1/4.0;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        panel.add(createOneColumn("Key", a), c);

        c.gridx = 1;
        panel.add(createOneColumn("Secret", a), c);

        c.gridx = 2;
        panel.add(createOneColumn("QR", a), c);

        c.gridx = 3;
        panel.add(createOneColumn("Output", a), c);
        return panel;
    }

    private JPanel createOneColumn(String label, String[] columnArray) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;

        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(new JLabel(label), c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JSpinner(new SpinnerListModel(columnArray)), c);

        return panel;
    }

    private JPanel createGeneratePanel() {
        TitledPanel panel = new TitledPanel("Generate QR Code");
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.LINE_START;
        panel.add(new JLabel("Drive folder ID"), c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JTextField(15), c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(new JButton("Generate QR Code"), c);

        return panel;
    }

    private JPanel createWebcamPanel() {
        TitledPanel panel = new TitledPanel("Scan QR Code");
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridheight = GridBagConstraints.RELATIVE;
        panel.add(new JLabel("Webcam"), c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(new JButton("Start Webcam"), c);

        return panel;
    }
}
