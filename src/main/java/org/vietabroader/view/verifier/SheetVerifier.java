package org.vietabroader.view.verifier;

import javax.swing.*;
import java.awt.*;

public class SheetVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
        return true;
    }

    @Override
    public boolean shouldYieldFocus(JComponent input) {
        boolean inputOK = verify(input);
        if (inputOK) {
            input.setForeground(Color.black);
            return true;
        } else {
            Toolkit.getDefaultToolkit().beep();
            input.setForeground(Color.red);
            return false;
        }
    }
}

