package org.vietabroader.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import org.vietabroader.view.WebcamView;

import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private List<String> qrUrl = new ArrayList<String>();

    public WebcamController setWebcam(Webcam cam, WebcamPanel panel) {
        webcam = cam;
        webcamPanel = panel;
        return this;
    }

    @Override
    public void control() {
        // TODO: put QR reading code here
        (new qrReaderWorker()).execute();
    }

    public class qrReaderWorker extends SwingWorker<Integer, String> {

        @Override
        public Integer doInBackground() {
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Result result = null;
                BufferedImage image = null;

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
//                    qrUrl.add(result.getText());
//                    System.out.println(qrUrl);
                    System.out.println(result.getText());
                }

            } while (!isCancelled());

            return 1;
        }


        @Override
        protected void process(List<String> chunks) {
            //        for (final String string : chunks) {
            //            messagesTextArea.append(string);
            //            messagesTextArea.append("\n");
            //        }
        }

//        private void cancel() {
//            searchWorker.cancel(true);
//        }
    }
}

// process method that can run at the same time
// publish method

// do in background
// check if the qr code exists in the list ->
// sleep in the while loop since it runs too fast
//
//while {
//    publish method -> run the process method


    // set function in view and set in controller
    // set webcam view(this)

    // stop webcam frame when window is closed
    // is cancelled in