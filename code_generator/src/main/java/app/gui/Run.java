package app.gui;

import javax.swing.*;

public class Run {

    public static  void main(String[] args){
        //SwingUtilities.invokeLater(() -> {MainFrame.run();});

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame.run();
            }
        });
    }
}
