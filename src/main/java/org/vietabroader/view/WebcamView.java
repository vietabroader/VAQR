package org.vietabroader.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import org.vietabroader.controller.WebcamController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class WebcamView extends JFrame {

    private final Webcam webcam = Webcam.getDefault();
    private WebcamPanel webcamPanel;
    private final JTextField txtWebcamMessage = new JTextField("");

    WebcamView() {
        initUI();
        initControllers();
    }

    private void initUI() {
        // Configure webcam and webcam panel
        Dimension size = WebcamResolution.VGA.getSize();
        webcam.setViewSize(size);
        webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setPreferredSize(size);
        webcamPanel.setMirrored(true);

        // Lay out components in the frame
        JPanel panWebcam = new JPanel();
        panWebcam.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        panWebcam.add(webcamPanel, c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        txtWebcamMessage.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        txtWebcamMessage.setEnabled(false);
        panWebcam.add(txtWebcamMessage, c);

        // Configure the frame
        this.setTitle("QR Reader");
        this.add(panWebcam);

        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void initControllers() {
        WebcamController webcamController = new WebcamController();
        webcamController.setWebcam(webcam)
                .setWebcamView(this)
                .setWebcamPanel(webcamPanel)
                .setTextWebcamMessage(txtWebcamMessage)
                .control();
    }

}
