package org.vietabroader.view.verifier;

import javax.swing.*;
import java.awt.*;

public class RowVerifier extends TextVerifier {
    @Override
    public boolean verify(JComponent input) {
        JTextField txt = (JTextField) input;
        try {
            return Integer.parseInt(txt.getText()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
