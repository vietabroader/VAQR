package org.vietabroader.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.awt.event.WindowEvent;
import java.util.List;
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

import static org.vietabroader.view.MainView.setMessageColor;
import static org.vietabroader.view.MainView.MessageType;

public class WebcamController implements Controller {

    private static final Logger logger = LoggerFactory.getLogger(WebcamController.class);

    private final int READER_SLEEP_TIME_MS = 500;

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
                        // fall through, it means there is no QR code in image
                    }
                }

                if (result != null && !result.getText().isEmpty()) {
                    publish(result.getText());
                }
            } while (!isCancelled());

            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            try {
                VASpreadsheet spreadsheet = GlobalState.getInstance().getSpreadsheet();
                List<Object> participantList = spreadsheet.readCol(VASpreadsheet.KEY_COL_NAME);
                for (String result : chunks) {
                    String cleanedResult = result.trim();
                    if (participantList.contains(cleanedResult)) {
                        int foundValueAt = participantList.indexOf(cleanedResult);

                        if (spreadsheet.readValue(VASpreadsheet.OUTPUT_COL_NAME,
                                                  foundValueAt).toString().isEmpty()) {
                            spreadsheet.writeValue(VASpreadsheet.OUTPUT_COL_NAME,
                                    foundValueAt,
                                    VASpreadsheet.CHECKED_MARK);

                            setMessageColor(txtWebcamMessage, MessageType.SUCCESS);
                            txtWebcamMessage.setText("Checking " + result + " in... OK");
                            new Thread(() -> {
                                try {
                                    spreadsheet.uploadOneColumn(VASpreadsheet.OUTPUT_COL_NAME);
                                } catch (Exception e) {
                                    logger.error("Error while uploading column Output", e);
                                }
                            }).start();

                        } else {
                            setMessageColor(txtWebcamMessage, MessageType.WARNING);
                            txtWebcamMessage.setText("This person (" + result + ") has already checked in");
                        }

                    } else {
                        setMessageColor(txtWebcamMessage, MessageType.ERROR);
                        txtWebcamMessage.setText("Cannot find " + result);
                    }
                }
            } catch (Exception e) {
                logger.error("Error while processing QR code", e);
            }
        }
    }
}