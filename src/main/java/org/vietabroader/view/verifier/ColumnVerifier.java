package org.vietabroader.view.verifier;

import javax.swing.*;
import java.awt.*;

public class ColumnVerifier extends SheetVerifier {
    @Override
    public boolean verify(JComponent input) {
        JTextField txt = (JTextField) input;
        return txt.getText().matches("[A-Z]{1,2}");
    }

}
