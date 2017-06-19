package org.vietabroader.view.verifier;

import javax.swing.*;
import java.awt.*;

public class RowVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
        JTextField txt = (JTextField) input;
        try {
            return Integer.parseInt(txt.getText()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean shouldYieldFocus(JComponent input) {
        boolean inputOK = verify(input);
        if (inputOK) {
            return true;
        } else {
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
    }
}
