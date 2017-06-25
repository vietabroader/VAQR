package org.vietabroader.view.verifier;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observer;

public class CameraView extends JFrame{
    public CameraView() {
        initUI();
    }

    private void initUI() {
        Dimension size = WebcamResolution.VGA.getSize();

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(size);

        WebcamPanel panel = new WebcamPanel(webcam); // Webcam is opened after this point
        panel.setPreferredSize(size);

        JPanel panWebcam = new JPanel();
        panWebcam.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        panWebcam.add(panel, c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblWebcam = new JLabel("Webcam");
        panWebcam.add(lblWebcam, c);

        JFrame webcamFrame = new JFrame("QR Reader");
        webcamFrame.add(panWebcam);

        webcamFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                panel.stop();
            }
        });

        webcamFrame.setResizable(false);
        webcamFrame.pack();
        webcamFrame.setLocationRelativeTo(null);
        webcamFrame.setVisible(true);
    }

}
