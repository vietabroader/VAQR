package org.vietabroader.controller;

import org.vietabroader.view.WebcamView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

public class WebcamController implements Controller {

    public WebcamController() {

    }

    @Override
    public void control() {
        // TODO: put QR reading code here
    }

    public class PrimeNumbersTask extends SwingWorker<String, Integer> {
        PrimeNumbersTask(JTextArea textArea, int numbersToFind) {
            //initialize
        }

        @Override
        public String doInBackground() {
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
                    textarea.setText(result.getText());
                }

            } while (true);
        }
        return numbers;
    }

    @Override
    protected void process(List<Integer> chunks) {
        for (int number : chunks) {
            textArea.append(number + "\n");
        }
    }
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