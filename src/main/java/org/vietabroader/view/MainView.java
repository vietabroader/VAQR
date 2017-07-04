package org.vietabroader.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vietabroader.GoogleAPIUtils;
import org.vietabroader.controller.AuthenticationController;
import org.vietabroader.controller.SheetRefreshController;
import org.vietabroader.controller.SpreadsheetConnectController;
import org.vietabroader.model.GlobalState;
import org.vietabroader.model.VASpreadsheet;
import org.vietabroader.view.verifier.RowVerifier;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.List;
import java.util.Observer;
import java.util.Observable;

/*
Main view of the app. This view observes the changes in GlobalState singleton.

Prefix rules:
btn: JButton
lbl: JLabel
txt: JTextField
spn: JSpinner
cbb: JComboBox
pan: JPanel
prg: JProgressBar
 */
public class MainView extends JFrame implements Observer {

    /**
     * Panel with title
     */
    private static class TitledPanel extends JPanel {
        private TitledPanel(String title) {
            this.setLayout(new GridBagLayout());
            this.setBorder(new CompoundBorder(
                    new TitledBorder(title),
                    new EmptyBorder(8, 2, 8, 2))    // Inner padding of each panel
            );
        }

        @Override
        public void setEnabled(boolean b) {
            super.setEnabled(b);
            Component[] children = this.getComponents();
            for (Component child: children) {
                child.setEnabled(b);
            }
        }
    }

    /**
     * This label cuts off text that is too long. Full text can be seen in tooltip.
     */
    private static class MessageLabel extends JLabel {
        private final int CUT_OFF = 60;
        MessageLabel(String text) {
            super(text);
        }

        @Override
        public void setText(String text) {
            String cutOff = text;
            if (cutOff.length() > CUT_OFF) {
                cutOff = cutOff.substring(0, CUT_OFF) + "...";
            }
            super.setText(cutOff);
            setToolTipText(text);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(GoogleAPIUtils.class);

    private final String QR_GENERATOR_URL = "https://vietabroader-qr.appspot.com/";

    private final String BUTTON_TEXT_SIGN_IN = "Sign In";
    private final String BUTTON_TEXT_SIGN_OUT = "Sign Out";
    private final String LABEL_TEXT_SIGN_IN = "Please sign in with your Google account";
    private final String BUTTON_TEXT_CONNECT = "Connect";
    private final String BUTTON_TEXT_REFRESH = "Refresh";

    private final Dimension BUTTON_DIM_AUTHENTICATE = new Dimension(100, 30);
    private final Dimension LABEL_DIM_EMAIL = new Dimension(300, 15);
    private final Dimension BUTTON_DIM_REFRESH = new Dimension(100, 50);

    private final JButton btnAuthenticate = new JButton(BUTTON_TEXT_SIGN_IN);
    private final JLabel lblAuthMessage = new JLabel(LABEL_TEXT_SIGN_IN);
    private final JButton btnConnect = new JButton(BUTTON_TEXT_CONNECT);
    private final JTextField txtSpreadsheetID = new JTextField(15);
    private final MessageLabel lblSpreadsheetMessage = new MessageLabel(" ");
    private final MessageLabel lblSheetMessage = new MessageLabel(" ");
    private final JComboBox<String> cbbSheet = new JComboBox<>();
    private final JProgressBar prgIndicator = new JProgressBar();
    private final JButton btnRefresh = new JButton(BUTTON_TEXT_REFRESH);
    private final JTextField txtRowFrom = new JTextField("1",5);
    private final JTextField txtRowTo = new JTextField("1",5);
    private final OneColumn[] columnArray = new OneColumn[4];

    private final JPanel panSignIn = createSignInPanel();
    private final JPanel panSpreadsheet = createSpreadsheetPanel();
    private final JPanel panWorkspace = createWorkspacePanel();
    private final JPanel panGenerate = createGeneratePanel();
    private final JPanel panWebcam = createWebcamPanel();
    private final JPanel panFooter = createFooterPanel();

    public MainView() {
        initUI();
        resetOnSignedOut();
        initControllers();
    }

    private void resetOnSignedOut() {
        setMessageColor(lblSpreadsheetMessage, MessageType.INACTIVE);
        lblSpreadsheetMessage.setText(" ");

        setMessageColor(lblSheetMessage, MessageType.INACTIVE);
        lblSheetMessage.setText(" ");

        btnAuthenticate.setText(BUTTON_TEXT_SIGN_IN);
        lblAuthMessage.setText(LABEL_TEXT_SIGN_IN);

        cbbSheet.removeAllItems();
    }

    private void initUI() {
        JPanel panelMain = new JPanel();
        panelMain.setLayout(new GridBagLayout());
        getContentPane().add(panelMain);
        GridBagConstraints c = new GridBagConstraints();

        // Sign in panel
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(4, 4, 4, 4);  // Outer margin of each panel

        panelMain.add(panSignIn, c);

        // Spreadsheet connect panel
        c.gridy = 2;
        panelMain.add(panSpreadsheet, c);

        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        lblSpreadsheetMessage.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        lblSpreadsheetMessage.setOpaque(true);
        panelMain.add(lblSpreadsheetMessage, c);

        // Sheet workspace panel
        c.gridy = 4;
        panelMain.add(panWorkspace, c);

        c.gridy = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        lblSheetMessage.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        lblSheetMessage.setOpaque(true);
        panelMain.add(lblSheetMessage, c);

        // QR generating and reading panel
        c.gridy = 6;
        c.gridwidth = 1;
        c.weightx = 1 / 2.0;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        panelMain.add(panGenerate, c);

        c.gridx = 1;
        panelMain.add(panWebcam, c);

        c.gridy = 7;
        c.gridx = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 0, 0);
        panelMain.add(panFooter, c);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                GoogleAPIUtils.signOut();
            }
        });

        this.setTitle("VAQR");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private JPanel createSignInPanel() {
        TitledPanel panel = new TitledPanel("Account");
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        btnAuthenticate.setPreferredSize(BUTTON_DIM_AUTHENTICATE);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(btnAuthenticate, c);

        lblAuthMessage.setPreferredSize(LABEL_DIM_EMAIL);
        c.gridx = 1;
        c.gridy = 0;
        panel.add(lblAuthMessage, c);

        return panel;
    }

    private JPanel createSpreadsheetPanel() {
        TitledPanel panel = new TitledPanel("Spreadsheet");
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.LINE_START;
        panel.add(new JLabel("Spreadsheet ID: "), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtSpreadsheetID, c);

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 2;
        panel.add(btnConnect);

        return panel;
    }


    private JPanel createWorkspacePanel() {
        TitledPanel panel = new TitledPanel("Workspace");

        // Sheet selection
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Sheet: "), c);

        c.gridx = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(cbbSheet, c);

        // Row range specification
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_END;
        c.weightx = 1/6.0;
        panel.add(new JLabel("Row: "), c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 2/6.0;

        txtRowFrom.setInputVerifier(new RowVerifier());
        txtRowFrom.setToolTipText("Enter a positive integer");
        panel.add(txtRowFrom, c);

        c.gridx = 2;
        c.weightx = 1/6.0;
        panel.add(new JLabel("to"), c);

        c.gridx = 3;
        c.weightx = 2/6.0;
        txtRowTo.setInputVerifier(new RowVerifier());
        txtRowTo.setToolTipText("Enter a positive integer");
        panel.add(txtRowTo, c);

        // Key columns and refresh button
        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0);
        JPanel panCol = createColumnPanel();
        panel.add(panCol, c);

        return panel;
    }

    private JPanel createColumnPanel() {
        JPanel panelMain = new JPanel() {
            @Override
            public void setEnabled(boolean b) {
                super.setEnabled(b);
                Component[] children = this.getComponents();
                for (Component child: children) {
                    child.setEnabled(b);
                }
            }
        };
        panelMain.setLayout(new GridBagLayout());
        TitledPanel panel = new TitledPanel("Columns");
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1 / 4.0;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        OneColumn colKey = new OneColumn(VASpreadsheet.KEY_COL_NAME);
        panel.add(colKey, c);
        columnArray[0] = colKey;

        c.gridx = 1;
        OneColumn colSecret = new OneColumn(VASpreadsheet.SECRET_COL_NAME);
        // TODO: enable or remove secret column in next version
        colSecret.setVisible(false);
        panel.add(colSecret, c);
        columnArray[1] = colSecret;

        c.gridx = 2;
        OneColumn colQR = new OneColumn(VASpreadsheet.QR_COL_NAME);
        // TODO: enable or remove QR column in next version
        colQR.setVisible(false);
        panel.add(colQR, c);
        columnArray[2] = colQR;

        c.gridx = 3;
        OneColumn colOutput = new OneColumn(VASpreadsheet.OUTPUT_COL_NAME);
        panel.add(colOutput, c);
        columnArray[3] = colOutput;

        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1 / 2.0;
        panelMain.add(panel, c);

        c.gridx = 1;
        btnRefresh.setPreferredSize(BUTTON_DIM_REFRESH);
        panelMain.add(btnRefresh, c);

        return panelMain;
    }

    private JPanel createGeneratePanel() {
        TitledPanel panel = new TitledPanel("Generate QR Code");
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        JButton btnGenerator = new JButton("Go to QR Generator");
        btnGenerator.setPreferredSize(new Dimension(160, 60));
        panel.add(btnGenerator, c);

        btnGenerator.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI(QR_GENERATOR_URL));
            } catch (Exception ex) {
                logger.error("Cannot open QR Generator", ex);
            }
        });

        return panel;
    }

    private JPanel createWebcamPanel() {
        TitledPanel panel = new TitledPanel("Scan QR Code");
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        JButton btnWebcam = new JButton("Start Webcam");
        btnWebcam.setPreferredSize(new Dimension(150, 60));
        panel.add(btnWebcam, c);

        btnWebcam.addActionListener(e -> {
            WebcamView camView = new WebcamView();
            camView.setVisible(true);

            GlobalState currentState = GlobalState.getInstance();
            currentState.setStatus(GlobalState.Status.QR_READING);
        });

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1/2.0;
        c.insets = new Insets(5, 10, 5, 10);

        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        JLabel lblCopyright = new JLabel("\u00a9 VietAbroader 2017");
        lblCopyright.setForeground(Color.GRAY);
        panel.add(lblCopyright, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_END;
        prgIndicator.setIndeterminate(false);
        panel.add(prgIndicator, c);

        return panel;
    }

    /**
     * Initialize controllers
     */
    private void initControllers() {
        AuthenticationController authController = new AuthenticationController();
        authController.setButtonAuthenticate(btnAuthenticate)
                .setProgressIndicator(prgIndicator)
                .setLabelAuthenticate(lblAuthMessage)
                .control();

        SpreadsheetConnectController spreadSheetConnectController = new SpreadsheetConnectController();
        spreadSheetConnectController.setButtonConnect(btnConnect)
                .setTextSpreadsheetID(txtSpreadsheetID)
                .setLabelSpreadsheetMessage(lblSpreadsheetMessage)
                .setProgressIndicator(prgIndicator)
                .control();

        SheetRefreshController sheetRefreshController = new SheetRefreshController();
        sheetRefreshController.setButtonRefresh(btnRefresh)
                .setComSheets(cbbSheet)
                .setRowFields(txtRowFrom, txtRowTo)
                .setSheetMessage(lblSheetMessage)
                .setProgressIndicator(prgIndicator);
        for (OneColumn column: columnArray) {
            sheetRefreshController.setColumnArray(column.getLabelText(), column.getTextField());
        }
        sheetRefreshController.control();

    }

    /**
     * Handle global state change
     */
    @Override
    public void update(Observable o, Object arg) {
        logger.debug("Global state changed");

        GlobalState currentState = (GlobalState) o;
        GlobalState.Status currentStatus = currentState.getStatus();
        switch (currentStatus) {
            case SIGNED_OUT:
                resetOnSignedOut();

                setEnabledChildren(false);
                panSignIn.setEnabled(true);
                break;
            case SIGNED_IN:
                btnAuthenticate.setText(BUTTON_TEXT_SIGN_OUT);
                lblAuthMessage.setText(currentState.getUserEmail());

                setEnabledChildren(false);
                panSignIn.setEnabled(true);
                panSpreadsheet.setEnabled(true);
                break;
            case CONNECTED:
                VASpreadsheet currentSpreadsheet = currentState.getSpreadsheet();
                String spreadsheetTitle = currentSpreadsheet.getSpreadsheetTitle();
                setMessageColor(lblSpreadsheetMessage, MessageType.SUCCESS);
                lblSpreadsheetMessage.setText("Connected to: " + spreadsheetTitle);
                List<String> sheets = currentSpreadsheet.getSheetTitles();
                cbbSheet.removeAllItems();
                sheets.forEach(cbbSheet::addItem);

                setMessageColor(lblSheetMessage, MessageType.INACTIVE);
                lblSheetMessage.setText(" ");

                setEnabledChildren(true);
                panWebcam.setEnabled(false);
                break;
            case REFRESHED:
                setEnabledChildren(true);
                break;
            case QR_READING:
                setEnabledChildren(false);
                break;

        }
    }

    private void setEnabledChildren(boolean b) {
        panSignIn.setEnabled(b);
        panSpreadsheet.setEnabled(b);
        panWorkspace.setEnabled(b);
        panWebcam.setEnabled(b);
    }

    public enum MessageType {
        INACTIVE,
        SUCCESS,
        WARNING,
        ERROR
    }

    /**
     * Set the background and foreground color of a component depending on
     * selected {@link MessageType}:
     * @param comp a Swing component
     * @param type type of message
     */
    public static void setMessageColor(JComponent comp, MessageType type) {
        Color background = Color.LIGHT_GRAY;
        Color foreground = Color.BLACK;
        switch (type) {
            case INACTIVE:
                background = Color.LIGHT_GRAY;
                foreground = Color.BLACK;
                break;
            case SUCCESS:
                background = Color.GREEN;
                foreground = Color.BLACK;
                break;
            case WARNING:
                background = Color.YELLOW;
                foreground = Color.BLACK;
                break;
            case ERROR:
                background = Color.RED;
                foreground = Color.WHITE;
                break;
        }
        comp.setBackground(background);
        comp.setForeground(foreground);
        if (comp instanceof JTextComponent) {
            ((JTextComponent) comp).setDisabledTextColor(foreground);
        }
    }

}

