package org.vietabroader.controller;

import org.vietabroader.view.WebcamView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

public class WebcamController implements Controller {

    public WebcamController() {

    }

    @Override
    public void control() {
        // TODO: put QR reading code here
    }


//    public class QRreader extends SwingWorker<List<Integer>, Integer> {
//        @Override
//        protected String doInBackground() throws Exception {
//            return "";
//        }
//
//        @Override
//        protected void process(List<Integer> something) {
//
//        }
//    }

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