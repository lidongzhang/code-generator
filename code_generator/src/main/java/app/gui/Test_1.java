package app.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Test_1 {

    public static JFrame frmIpa;
    public JTextArea textArea;

    public Test_1() {
        // 窗口框架
        frmIpa = new JFrame();
        frmIpa.setTitle("ipa工具类");
        frmIpa.setBounds(600, 300, 500, 400);
        frmIpa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 面板1
        JPanel panel = new JPanel();
        frmIpa.getContentPane().add(panel, BorderLayout.NORTH);
        JButton button = new JButton("选择文件");
        // 监听button的选择路径
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 显示打开的文件对话框
                JFileChooser jfc = new JFileChooser();
                jfc.showSaveDialog(frmIpa);
                try {
                    // 使用文件类获取选择器选择的文件
                    File file = jfc.getSelectedFile();//
                    //这里是我的业务需求，各位不必照抄
//					String content = IpaService.getIpaInfoMap(file.toString());
//					textArea.setText(content);
                } catch (Exception e2) {
                    JPanel panel3 = new JPanel();
                    JOptionPane.showMessageDialog(panel3, "没有选中任何文件", "提示", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        panel.add(button);

        // 可滚动面板
        JScrollPane scrollPane = new JScrollPane();
        frmIpa.getContentPane().add(scrollPane, BorderLayout.CENTER);
        textArea = new JTextArea();
        scrollPane.setViewportView(textArea);
        //这个最好放在最后，否则会出现视图问题。
        frmIpa.setVisible(true);
    }
}
