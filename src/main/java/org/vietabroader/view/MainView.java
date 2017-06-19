package org.vietabroader.view;

import org.vietabroader.GoogleAPIUtils;
import org.vietabroader.view.verifier.ColumnVerifier;
import org.vietabroader.view.verifier.RowVerifier;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Insets;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

/*
Prefix rules:
btn: JButton
lbl: JLabel
txt: JTextField
spn: JSpinner
cbb: JComboBox
pan: JPanel
 */

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
        c.insets = new Insets(4, 4, 4, 4);  // Outer margin of each panel
        panelMain.add(createSignInPanel(), c);

        c.gridy = 2;
        panelMain.add(createSpreadsheetPanel(), c);

        c.gridy = 3;
        panelMain.add(createWorkspacePanel(), c);

        c.gridy = 4;
        panelMain.add(createColumnPanel(), c);

        c.gridy = 5;
        c.gridwidth = 1;
        c.gridx = 0;
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
            this.setBorder(new CompoundBorder(
                    new TitledBorder(title),
                    new EmptyBorder(8, 8, 8, 8))    // Inner padding of each panel
            );
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
        final JTextField txtSpreadsheetID = new JTextField(15);
        panel.add(txtSpreadsheetID, c);

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 2;
        final JButton btnConnect = new JButton("Connect");
        panel.add(btnConnect);

        return panel;
    }

    private JPanel createWorkspacePanel() {
        TitledPanel panel = new TitledPanel("Workspace");

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1 / 6.0;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Sheet: "), c);

        c.gridx = 1;
        c.gridwidth = 3;
        c.weightx = 5 / 6.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        String[] sheet = {"Sheet1", "Sheet2"};
        final JComboBox cbbSheet = new JComboBox(sheet);
        panel.add(cbbSheet, c);


        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1 / 6.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Column: "), c);

        c.gridx = 1;
        c.weightx = 2 / 6.0;
        c.anchor = GridBagConstraints.CENTER;
        final JTextField txtColFrom = new JTextField("A",5);
        txtColFrom.setInputVerifier(new ColumnVerifier());
        panel.add(txtColFrom, c);

        c.gridx = 2;
        c.weightx = 1 / 6.0;
        panel.add(new JLabel("to"), c);

        c.gridx = 3;
        c.weightx = 2 / 6.0;
        final JTextField txtColTo = new JTextField("A",5);
        txtColTo.setInputVerifier(new ColumnVerifier());
        panel.add(txtColTo, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1 / 6.0;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Row: "), c);

        c.gridx = 1;
        c.weightx = 2 / 6.0;
        c.anchor = GridBagConstraints.CENTER;
        final JTextField txtRowFrom = new JTextField("1",5);
        txtRowFrom.setInputVerifier(new RowVerifier());
        panel.add(txtRowFrom, c);

        c.gridx = 2;
        c.weightx = 1 / 6.0;
        panel.add(new JLabel("to"), c);

        c.gridx = 3;
        c.weightx = 2 / 6.0;
        final JTextField txtRowTo = new JTextField("1",5);
        txtRowTo.setInputVerifier(new RowVerifier());
        panel.add(txtRowTo, c);

        return panel;
    }

    private JPanel createColumnPanel() {
        TitledPanel panel = new TitledPanel("Key Columns");
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1 / 4.0;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        final JPanel panKey = createOneColumn("Key");
        panel.add(panKey, c);

        c.gridx = 1;
        final JPanel panSecret = createOneColumn("Secret");
        panel.add(panSecret, c);

        c.gridx = 2;
        final JPanel panQR = createOneColumn("QR");
        panel.add(panQR, c);

        c.gridx = 3;
        final JPanel panOutput = createOneColumn("Output");
        panel.add(panOutput, c);
        return panel;
    }

    private JPanel createOneColumn(String label) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;

        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(new JLabel(label), c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        final JTextField txtCol = new JTextField("A",5);
        txtCol.setInputVerifier(new ColumnVerifier());
        panel.add(txtCol, c);

        return panel;
    }

    private JPanel createGeneratePanel() {
        TitledPanel panel = new TitledPanel("Generate QR Code");
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.LINE_START;
        panel.add(new JLabel("Drive folder ID"), c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        final JTextField txtDriveID = new JTextField(15);
        panel.add(txtDriveID, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        final JButton btnQR = new JButton("Generate QR Code");
        panel.add(btnQR, c);

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
      
        JButton webcamButton = new JButton("Start Webcam");
        panel.add(webcamButton, c);
        webcamButton.addActionListener(new OpenWebcamPanelAction());

        return panel;
    }

    public class OpenWebcamPanelAction implements ActionListener {
        private Webcam webcam = null;
        private WebcamPanel panel = null;

        public void actionPerformed(ActionEvent ae){
            JFrame webcamFrame = new JFrame("QR code");

            Dimension size = WebcamResolution.QVGA.getSize();

            webcamFrame.setVisible(true);
            webcamFrame.setSize(500, 500);


            Webcam webcam = Webcam.getDefault();
            panel = new WebcamPanel(webcam);
            panel.setPreferredSize(size);

            webcamFrame.add(panel);

            webcamFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e){
                    panel.stop();
                }
            });

        }
    }
}

