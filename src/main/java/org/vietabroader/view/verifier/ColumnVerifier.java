package org.vietabroader.view.verifier;

import javax.swing.*;
import java.awt.*;

public class ColumnVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
        JTextField txt = (JTextField) input;
        return txt.getText().matches("[A-Z]{1,2}");
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
            input.setToolTipText("Enter a valid column name. Ex: A, AB, ...");
            return false;
        }
    }
}
