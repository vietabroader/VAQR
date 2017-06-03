package org.vietabroader.view;

import org.vietabroader.model.GoogleAPIUtils;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MainView extends JFrame {

    MainView() {
        initUI();
    }

    private void initUI() {
        JPanel panelMain = new JPanel();
        getContentPane().add(panelMain);

        panelMain.add(createSignInPanel());

        setTitle("VAQR");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
    }

    private JPanel createSignInPanel() {
        JPanel panel = new JPanel();
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
        c.insets = new Insets(5, 5, 10, 10);
        c.ipadx = 70;
        panel.add(lblEmail, c);

        return panel;
    }
}
