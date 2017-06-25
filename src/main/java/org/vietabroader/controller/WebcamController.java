package org.vietabroader.controller;

import org.vietabroader.view.verifier.CameraView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WebcamController implements Controller {

    private JButton btnWebcam;

    public WebcamController setButtonWebcam(JButton btn) {
        btnWebcam = btn;
        return this;
    }

    public void control() {
        btnWebcam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CameraView();
            }
        });
    }
}
