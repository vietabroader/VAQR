package org.vietabroader.view;

import org.vietabroader.view.verifier.ColumnVerifier;

import javax.swing.*;
import java.awt.*;

public class OneColumn extends JPanel {
    private final JTextField txtCol;
    private final JLabel lblCol;

    OneColumn (String label) {
        txtCol = new JTextField("A", 5);
        lblCol = new JLabel(label);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;

        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        this.add(lblCol, c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        txtCol.setInputVerifier(new ColumnVerifier());
        txtCol.setToolTipText("Enter a valid column name. Ex: A, AB, ...");
        this.add(txtCol, c);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtCol.setEnabled(enabled);
    }

    JTextField getTextField() {
        return this.txtCol;
    }

    String getLabelText() {
        return this.lblCol.getText();
    }

}
