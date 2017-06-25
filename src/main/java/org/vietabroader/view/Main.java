package org.vietabroader.view;

import org.vietabroader.model.GlobalState;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView();
            GlobalState.getInstance().addObserver(view);
            view.setVisible(true);
        });
    }
}
