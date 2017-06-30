package org.vietabroader.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.image.BufferedImage;


import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

public class WebcamController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final int READER_SLEEP_TIME_MS = 700;

    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private JFrame webcamView;
    private JTextField txtWebcamMessage;

    public WebcamController setWebcam(Webcam cam) {
        webcam = cam;
        return this;
    }

    public WebcamController setWebcamView(JFrame view) {
        webcamView = view;
        return this;
    }

    public WebcamController setWebcamPanel(WebcamPanel pan) {
        webcamPanel = pan;
        return this;
    }

    public WebcamController setTextWebcamMessage(JTextField txt) {
        txtWebcamMessage = txt;
        return this;
    }

    @Override
    public void control() {
        SwingWorker<Void, String> worker = new QRReaderWorker();

        webcamView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                worker.cancel(false);
                webcamPanel.stop();
            }
        });

        worker.execute();
    }

    public class QRReaderWorker extends SwingWorker<Void, String> {

        @Override
        public Void doInBackground() throws InterruptedException {
            do {
                Thread.sleep(READER_SLEEP_TIME_MS);

                Result result = null;
                BufferedImage image;

                if (webcam.isOpen()) {

                    if ((image = webcam.getImage()) == null) {
                        continue;
                    }

                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                    try {
                        result = new MultiFormatReader().decode(bitmap);
                    } catch (NotFoundException e) {
                        // fall thru, it means there is no QR code in image
                    }
                }

                if (result != null) {
                    System.out.println(result.getText());
                    txtWebcamMessage.setText(result.getText());
                }
            } while (!isCancelled());

            return null;
        }
    }
}