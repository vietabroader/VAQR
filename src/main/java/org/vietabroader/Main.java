package org.vietabroader;

import org.vietabroader.model.GlobalState;
import org.vietabroader.view.MainView;

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
