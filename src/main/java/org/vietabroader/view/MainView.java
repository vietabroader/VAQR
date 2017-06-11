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
        DefaultPanel panelMain = new DefaultPanel();
        getContentPane().add(panelMain);
        GridBagConstraints c = panelMain.c;

        c.weightx = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(4,4,4,4);
        panelMain.add(createSignInPanel(), c);

        c.gridy = 2;
        panelMain.add(createSpreadsheetPanel(), c);

        c.gridy = 3;
        panelMain.add(createWorkspacePanel(), c);

        c.gridy = 4;
        panelMain.add(createColumnPanel(), c);

        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 5;
        c.fill = GridBagConstraints.BOTH;
        panelMain.add(createGeneratePanel(), c);

        c.gridx = 1;
        panelMain.add(createWebcamPanel(), c);

        setTitle("VAQR");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
    }

    private class DefaultPanel extends JPanel {
        private GridBagConstraints c;
        private DefaultPanel() {
            this.setLayout(new GridBagLayout());
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
        }
    }

    private JPanel createSignInPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new CompoundBorder(new TitledBorder("Account"), new EmptyBorder(8, 0, 0,0)));

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
        c.insets = new Insets(5, 5, 10, 10);
        c.ipadx = 70;
        panel.add(lblEmail, c);

        return panel;
    }

    private JPanel createSpreadsheetPanel() {
        DefaultPanel panel = new DefaultPanel();
        GridBagConstraints c = panel.c;
        panel.setBorder(new CompoundBorder(new TitledBorder("Spreadsheet"), new EmptyBorder(8, 0, 0,0)));

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
        DefaultPanel panel = new DefaultPanel();
        panel.setBorder(new CompoundBorder(new TitledBorder("Workspace"), new EmptyBorder(8, 0, 0,0)));
        GridBagConstraints c = panel.c;

        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Sheet: "), c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        String[] sheet = {"Sheet1", "Sheet2"};
        panel.add(new JComboBox(sheet), c);


        c = new GridBagConstraints();
        c.gridy = 1;
        panel.add(new JLabel("Column: "), c);

        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.33;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JSpinner(), c);

        c.fill = GridBagConstraints.NONE;
        c.gridx = 2;
        panel.add(new JLabel("to"), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        panel.add(new JSpinner(), c);


        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        panel.add(new JLabel("Row: "), c);

        c.gridwidth = 1;
        c.weightx = 0.33;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JSpinner(), c);

        c.fill = GridBagConstraints.NONE;
        c.gridx = 2;
        panel.add(new JLabel("to"), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        panel.add(new JSpinner(), c);

        return panel;
    }

    private JPanel createColumnPanel() {
        DefaultPanel panel = new DefaultPanel();
        GridBagConstraints c = panel.c;
        panel.setBorder(new CompoundBorder(new TitledBorder("Column"), new EmptyBorder(8, 0, 0,0)));

        String[] a = {"a", "b"};
        c.weightx = 0.25;
        c.fill = GridBagConstraints.HORIZONTAL;
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
        DefaultPanel panel = new DefaultPanel();
        GridBagConstraints c = panel.c;

        c.anchor = GridBagConstraints.CENTER;
        panel.add(new JLabel(label), c);

        c.gridy = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JSpinner(new SpinnerListModel(columnArray)), c);

        return panel;
    }

    private JPanel createGeneratePanel() {
        DefaultPanel panel = new DefaultPanel();
        GridBagConstraints c = panel.c;
        panel.setBorder(new CompoundBorder(new TitledBorder("Generate QR Code"), new EmptyBorder(8, 0, 0,0)));

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
        DefaultPanel panel = new DefaultPanel();
        GridBagConstraints c = panel.c;
        panel.setBorder(new CompoundBorder(new TitledBorder("Scan QR Code"), new EmptyBorder(8, 0, 0,0)));

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
