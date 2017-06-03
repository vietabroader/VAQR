package org.vietabroader.view;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainView view = new MainView();
                view.setVisible(true);
            }
        });
    }
}
