package app.gui;

import javax.swing.*;
import java.awt.*;

public class Processer {

    public JDialog dialog ;
    JProgressBar progressBar;

    public Processer(JDialog owner, Component parentComponent) {

        dialog = new JDialog(owner, "代码生成进度", true);
        progressBar = new JProgressBar();
        init_thread();
        init_dialog(owner, parentComponent);
    }

    private void init_thread(){
        Thread thread = new Thread(new ProcesserTask(this));
        thread.start();
    }

    private void init_dialog(JDialog owner, Component parentComponent){

        // 设置对话框的宽高
        dialog.setSize(650, 50);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        progressBar.setMaximum(100);
        dialog.add(progressBar);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }
}
