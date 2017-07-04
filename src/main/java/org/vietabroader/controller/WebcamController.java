package org.vietabroader.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.sound.midi.SysexMessage;
import javax.swing.*;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import org.vietabroader.model.GlobalState;
import org.vietabroader.model.VASpreadsheet;

public class WebcamController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final int READER_SLEEP_TIME_MS = 1000;

    private List<Object> participantList;

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
                GlobalState.getInstance().setStatus(GlobalState.Status.REFRESHED);
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
                    publish(result.getText());
                }
            } while (!isCancelled());

            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            VASpreadsheet spreadsheet = GlobalState.getInstance().getSpreadsheet();
            int fromRow = spreadsheet.getFromRow();
            try {
                participantList = spreadsheet.readCol("Key");

            } catch (Exception e) {

            }

            for (String result : chunks) {
                System.out.println(result);
                txtWebcamMessage.setText(result);
                if (participantList.contains(result)) {
                    System.out.println("It is in the list");
                    System.out.println(participantList.indexOf(result));
                } else {
                    System.out.println("It is not in the list");
                }
//                for (Object participant: participantList) {
//                    if (result.equals(participant)) {
//                        // do something
//                        System.out.println("It is in the list");
//                        System.out.println(participantList.indexOf(participant));
//                    } else {
//                        System.out.println("It is not in the list");
//                    }
//                    // notify if marked
//                    // not existed
//                    // not marked
//
//
//                    // upload column
//                    // excute thread
////                    // Thread
////                    System.out.println(participant);
////                    System.out.println(spreadsheet.getFromRow());
//                }
            }


        }
    }
}