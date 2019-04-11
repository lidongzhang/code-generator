package app.gui;

import app.api.appProperties.AppProperties;
import app.api.config.CodeGeneratorConfig;
import app.api.config.CodeGeneratorConfigUtil;
import app.api.config.TableConfig;
import app.api.generator.Util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class GenerateCodeDialog {

    private JButton addButton;
    private JButton subButton;
    private JButton addAllButton;
    private JButton subAllButton;
    private JList<String> sourceList;
    private JList<String> destList;
    private JButton exitButton;
    private JButton generationButton;
    private JDialog dialog ;
    private JTextField htmlPathTextFile;
    private JTextField controll_servicePackageTextFile;
    private JTextField mybatisPackageTextFile;
    private JCheckBox mybatisCheckBox;
    private JCheckBox htmlCheckBox;
    private JCheckBox controllerAndServiceCheckBox;
    private JTextField resourceMybatisPackTextFile;

    private Processer p = null;

    public  GenerateCodeDialog(Frame owner, Component parentComponent){

        dialog = new JDialog(owner, "代码生成", true);
        // 设置对话框的宽高
        dialog.setSize(650, 550);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        setDialog();
        init_event();
        dialog.setVisible(true);
    }

    private void setDialog(){
        AppProperties appProperties = AppProperties.getAppProperties();
        //topPanel
        JPanel topPanel = new JPanel(new GridLayout(5,1));
        JPanel  topContent1Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        //topContent1
        topContent1Panel.add(new JLabel("生成内容:"));
        topPanel.add(topContent1Panel);
        //checkbox
        mybatisCheckBox = new JCheckBox("mybatis(dao、entity、mapper)");
        topContent1Panel.add(mybatisCheckBox);
        htmlCheckBox = new JCheckBox("html");
        topContent1Panel.add(htmlCheckBox);
        controllerAndServiceCheckBox = new JCheckBox("controll+servic");
        topContent1Panel.add(controllerAndServiceCheckBox);


        //topContent3
        JPanel topContent3Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel htmlLabel = new JLabel("html 路径:");
        topContent3Panel.add(htmlLabel );
        htmlLabel.setBorder(BorderFactory.createEmptyBorder(0, 120, 0, 0));
        topPanel.add(topContent3Panel);
        htmlPathTextFile = new JTextField(30);
        htmlPathTextFile.setText(appProperties.getGeneratorHtmlPath());
        topContent3Panel.add(htmlPathTextFile);


        //controll+service package
        JPanel topContent35Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel controll_servicePackageLabel = new JLabel("controll+service 包名:");
        controll_servicePackageLabel.setBorder(BorderFactory.createEmptyBorder(0, 45, 0, 0));
        topContent35Panel.add(controll_servicePackageLabel );
        controll_servicePackageTextFile = new JTextField(30);
        controll_servicePackageTextFile.setText(appProperties.getGeneratorJavaPackage());
        topContent35Panel.add(controll_servicePackageTextFile);
        topPanel.add(topContent35Panel);

        //mybatis package
        JPanel topContent15Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topContent15Panel.setBorder(BorderFactory.createEmptyBorder(0,20,0,0));
        topContent15Panel.add(new JLabel("mybatis(dao、entity)包名:"));
        mybatisPackageTextFile = new JTextField(30);
        mybatisPackageTextFile.setText(appProperties.getGeneratorJavaMybatisPackage());
        topContent15Panel.add(mybatisPackageTextFile);
        topPanel.add(topContent15Panel);
        //mybatis(mapper)包名
        JPanel topContent2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topContent2Panel.setBorder(BorderFactory.createEmptyBorder(0,45,0,0));
        topContent2Panel.add( new JLabel("mybatis(mapper)包名:"));
        topPanel.add(topContent2Panel);
        resourceMybatisPackTextFile = new JTextField(30);
        resourceMybatisPackTextFile.setText(appProperties.getGeneratorResourcesMybatisPackage());
        topContent2Panel.add(resourceMybatisPackTextFile);

        dialog.add(topPanel, BorderLayout.NORTH);
        //centerPanel
        JPanel centerPanel = new JPanel(new BorderLayout());
        //source
        DefaultListModel<String> sourceModel = new DefaultListModel<String>();
        //for(Integer i = 0; i < 20; i++) sourceModel.addElement(i.toString());
        addTablesToModel(sourceModel);
        sourceList = new JList<String>(sourceModel);
        JScrollPane sourcePane = new JScrollPane(sourceList);
        sourcePane.setBorder(BorderFactory.createEmptyBorder(0,50,0,50));
        sourcePane.setBackground(dialog.getBackground());
        sourcePane.setPreferredSize(new Dimension(300, 0));
        centerPanel.add(sourcePane, BorderLayout.WEST);
        //dest
        //String[] destData = {"1","2","3","1","2","3","1","2","3","1","2","3","1","2","3","1","2","3","1","2","3"};
        DefaultListModel<String> destModel = new DefaultListModel<String>();
        destList = new JList<String>(destModel);
        JScrollPane destPane = new JScrollPane(destList);
        destPane.setBorder(BorderFactory.createEmptyBorder(0,50,0,50));
        destPane.setBackground(dialog.getBackground());
        destPane.setPreferredSize(new Dimension(300, 0));
        centerPanel.add(destPane, BorderLayout.EAST);
        //buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(4,1));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(30,0,30,0));
        addButton = new JButton(">");

        subButton = new JButton("<");

        addAllButton = new JButton(">>");
        subAllButton = new JButton("<<");
        buttonsPanel.add(addButton);
        buttonsPanel.add(subButton);
        buttonsPanel.add(addAllButton);
        buttonsPanel.add(subAllButton);
        centerPanel.add(buttonsPanel);
        dialog.add(centerPanel);
        //endPanel
        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitButton = new JButton("退出");

        generationButton = new JButton("生成");
        endPanel.add(generationButton);
        endPanel.add(exitButton);
        dialog.add(endPanel, BorderLayout.SOUTH);
    }

    private void init_event(){
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DefaultListModel s = (DefaultListModel) (sourceList.getModel());
                DefaultListModel d = (DefaultListModel) (destList.getModel());
                int i = sourceList.getSelectedIndex();
                if (i != -1) {
                    String tableName = s.get(i).toString();
                    s.remove(i);
                    d.addElement(tableName);
                }
            }
        });

        subButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel s = (DefaultListModel) (sourceList.getModel());
                DefaultListModel d = (DefaultListModel) (destList.getModel());
                int i = destList.getSelectedIndex();
                if (i != -1) {
                    String tableName = d.get(i).toString();
                    d.remove(i);
                    s.addElement(tableName);
                }
            }
        });

        addAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel s = (DefaultListModel) (sourceList.getModel());
                DefaultListModel d = (DefaultListModel) (destList.getModel());
                for(int i = s.size() - 1; i >= 0; i--){
                    String tableName = s.get(i).toString();
                    d.addElement(tableName);
                    s.remove(i);
                }
            }
        });

        subAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel s = (DefaultListModel) (sourceList.getModel());
                DefaultListModel d = (DefaultListModel) (destList.getModel());
                for(int i = d.size() - 1; i >= 0; i--){
                    String tableName = d.get(i).toString();
                    s.addElement(tableName);
                    d.remove(i);
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });


        generationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean mybatis = mybatisCheckBox.isSelected();
                boolean html = htmlCheckBox.isSelected();
                boolean java = controllerAndServiceCheckBox.isSelected();
                if(mybatis == false && html == false && java == false){
                    JOptionPane.showMessageDialog(null,"没有选择生成内容", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                DefaultListModel d = (DefaultListModel) destList.getModel();
                Set<String> tables = new HashSet<String>();
                for(int i = 0; i < d.size(); i++){
                    tables.add(d.get(i).toString());
                }
                if(tables.size() == 0){
                    JOptionPane.showMessageDialog(null,"没有选择要生成的表", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                //是否有配置信息检查
                String str = CodeGeneratorConfigUtil.configCheck(tables);
                if(!(str.isEmpty())){
                    JOptionPane.showMessageDialog(null, str);
                    return;
                }

                int n = JOptionPane.showConfirmDialog(null, "请确保生成文件存放的目录中没有文件，如果有则同名文件将被覆盖，确认生成吗?", "提示",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
                if(n == JOptionPane.NO_OPTION){
                    return;
                }else {

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            p = new Processer(dialog, dialog);
                        }
                    });

                    System.out.println("continue ...");
                    String javaPath = AppProperties.getAppProperties().getGeneratorJavaPath();
                    Thread thread = new Thread(new GenerateTask(mybatis, html, java,
                            resourceMybatisPackTextFile.getText(), htmlPathTextFile.getText(),
                            javaPath,
                            mybatisPackageTextFile.getText(), controll_servicePackageTextFile.getText(), tables) );
                    thread.start();

                }
            }
        });
    }

    private void addTablesToModel(DefaultListModel<String> model){
        CodeGeneratorConfig config = CodeGeneratorConfigUtil.getCodeGeneratorConfig();
        for(TableConfig t : config.getTableList()){
            model.addElement(t.getName());
        }
    }
}
