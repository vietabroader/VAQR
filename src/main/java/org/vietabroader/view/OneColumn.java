package org.vietabroader.view;

import org.vietabroader.view.verifier.ColumnVerifier;

import javax.swing.*;
import java.awt.*;

public class OneColumn extends JPanel {
    private JTextField textField;
    private String label;

    public OneColumn (String label) {
        final JTextField txtCol = new JTextField("A",5);
        this.textField = txtCol;

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;

        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        this.add(new JLabel(label), c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        txtCol.setInputVerifier(new ColumnVerifier());
        txtCol.setToolTipText("Enter a valid column name. Ex: A, AB, ...");
        this.add(txtCol, c);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
    }

    public JTextField getTextField() {
        return this.textField;
    }

    public String getString() {
        return this.label;
    }

}
