package app.gui;

import app.api.appProperties.AppProperties;
import app.api.config.*;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import sun.swing.table.DefaultTableCellHeaderRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.*;
import java.util.List;

public class MainFrame {

    private JFrame mainJFrame;
    private JTextField dataBaseTextField = null;
    private JLabel dataBaseStatusLabel = null;
    private JRadioButton mssqlRadioButton = null;
    private JRadioButton mysqlRadioButton = null;
    private JButton codeGenerateButton = null;
    private JTextField userTextFile = null;
    private JTextField passwordTextFile = null;
    private JButton linkDatabaseButton = null;

    private JPanel columnSetPanel = null;
    private JTree tableTree = null;
    private JTable columnTable = null;

    private JLabel nameLabel = null;
    private JCheckBox generateCheckBox = null;
    private JCheckBox generateAsQueryCheckBox = null;
    private JComboBox dataTypeComboBox = null;
    private JTextField titleTextField = null;
    private JTextField memoTextField = null;
    private JCheckBox requireCheckBox = null;
    private JTextField minTextField = null;
    private JTextField maxTextField = null;
    private JTextField precisionTextField = null;
    private JButton saveButton = null;

    private Color enableColor;

    private MainFrame(){ }

    private static MainFrame mainFrame;

    public static void run(){
        if(mainFrame == null)
            mainFrame = new MainFrame();
        mainFrame.init();
    }

    private void init(){
        mainJFrame =  new JFrame();
        AppProperties appProperties = AppProperties.getAppProperties();
        mainJFrame.setTitle("代码生成工具 " + appProperties.getVersion());
        mainJFrame.setSize(800, 600);
        mainJFrame.setLocationRelativeTo(null);
        mainJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //panel
        JPanel topPanel = new JPanel(new  GridLayout(2,1));
        JPanel leftPanel = new JPanel(new BorderLayout());

        JPanel centerPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(0, 70));
        leftPanel.setPreferredSize(new Dimension(200,0));

        //topPanel
        initTopPanel(topPanel, mainJFrame);

        //leftPanel
        initLeftPanel(leftPanel);

        //centerPanel
        initCenterPanel(centerPanel);

        //columnSetPanel
        initColumnPanel(centerPanel);

        //add panel
        Container contentContainer = mainJFrame.getContentPane();
        contentContainer.add(topPanel, BorderLayout.NORTH);
        contentContainer.add(leftPanel, BorderLayout.LINE_START);
        contentContainer.add(centerPanel, BorderLayout.CENTER);
        //
        mainJFrame.setVisible(true);

        init_data();

        init_event();
    }
    private void init_data(){
        //设置 topPanel
        AppProperties appProperties = AppProperties.getAppProperties();
        dataBaseTextField.setText(appProperties.getDatabaseUrl());
        userTextFile.setText(appProperties.getDatabaseUser());
        passwordTextFile.setText(appProperties.getDatabasePassword());

        String s = appProperties.getDatabaseType();
        if(s.equals("mysql"))
            mysqlRadioButton.setSelected(true);
        else
            mysqlRadioButton.setSelected(false);
        if(s.equals("mssql"))
            mssqlRadioButton.setSelected(true);
        else
            mssqlRadioButton.setSelected(false);

        //设置树
        set_tree_tables();
    }

    private void set_tree_tables(){
        CodeGeneratorConfig  codeGeneratorConfig = CodeGeneratorConfigUtil.getCodeGeneratorConfig();
        DefaultTreeModel defaultModel = (DefaultTreeModel)tableTree.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)tableTree.getModel().getRoot();
        rootNode.removeAllChildren();
        defaultModel.reload();
        for(TableConfig t : codeGeneratorConfig.getTableList()){
            rootNode.add(new DefaultMutableTreeNode(t.getName()));
        }
        tableTree.expandPath(new TreePath(rootNode));
        defaultModel.reload();
    }

    private void init_event(){
        tableTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tableTree.getLastSelectedPathComponent();
                if(node == null){
                    return;
                }

                if(node.isLeaf()) {
                    columnSetPanel.setVisible(false);
                    setColumnTableData(node.toString());
                }
                if(node.isRoot()){
                    columnTable.setVisible(false);
                    columnSetPanel.setVisible(false);
                }
            }
        });

        codeGenerateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = CodeGeneratorConfigUtil.configCheck();
                if(!(str.isEmpty())){
                    int n = JOptionPane.showConfirmDialog(null, String.format("有配置信息没有保存:\r\n%s\r\n继续吗?",str), "提示",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
                    if(n == JOptionPane.NO_OPTION)
                        return;
                }
                new GenerateCodeDialog(mainJFrame,mainJFrame);
            }
        });

        linkDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String databaseType = null;
               if(mssqlRadioButton.isSelected())
                   databaseType = "mssql";
               if(mysqlRadioButton.isSelected())
                   databaseType = "mysql";
               String databaseUrl = dataBaseTextField.getText();
               String user = userTextFile.getText();
               String password = passwordTextFile.getText();
               try {
                   data_set_database(databaseType, databaseUrl, user, password);
                   clearColumnPanelContent();
                   columnSetPanel.setVisible(false);
                   clearColumnTable();
                   set_tree_tables();
                   dataBaseStatusLabel.setText("联机");
                   dataBaseStatusLabel.setForeground(Color.BLUE);
               }catch (Exception ex){
                   ex.printStackTrace();
               }
            }
        });

        //columnTable.a
        ListSelectionModel cellSelectionModel = columnTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
               if (e.getValueIsAdjusting()) return ; //防止事件重复触发; 鼠标按下直接退出，鼠标释放继续执行

                int r = columnTable.getSelectedRow();
                if ( r >=0 ){
                    showColumnPanelContent(r);
                    columnSetPanel.setVisible(true);
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(checkColumnPanelContent() )
                    saveColumnPanelContent();
            }
        });
    }

    //region checkColumnPanelContent
    private boolean checkMinAndMax(){
        if(!Util.isLong(minTextField.getText())){
            JOptionPane.showMessageDialog(null, "最小值必须填写数字!");
            return false;
        }
        if(!Util.isLong(maxTextField.getText())){
            JOptionPane.showMessageDialog(null, "最大值必须填写数字!");
            return false;
        }
        return true;
    }

    private BigInteger getAllowMin(DataTypeEnum type, ColumnConfig c){
        if(type == DataTypeEnum.STRING){
            return new BigInteger("0");
        }
        if(type == DataTypeEnum.INT){
            if(c.getJdbcType().equals("TINYINT"))
                return new BigInteger(MIN_TINYINT.toString());
            if(c.getJdbcType().equals("INTEGER"))
                return new BigInteger(MIN_INTEGER.toString());
            if(c.getJdbcType().equals("SMALLINT"))
                return new BigInteger(MIN_SMALLINT.toString());
        }
        if(type == DataTypeEnum.LONGINT)
            return new BigInteger(MIN_LONGINT.toString());
        if(type == DataTypeEnum.DECIMAL){
            BigInteger bi = Util.generateLong(c.getLength() - c.getPrecision());
            return bi.multiply(BigInteger.valueOf(-1)) ;
        }
        return null;
    }

    private BigInteger getAllowMax(DataTypeEnum type, ColumnConfig c){
        if(type == DataTypeEnum.STRING)
            return BigInteger.valueOf(c.getLength());
        if(type == DataTypeEnum.INT){
            if(c.getJdbcType().equals("TINYINT"))
                return BigInteger.valueOf(MAX_TINYINT);
            if(c.getJdbcType().equals("INTEGER"))
                return BigInteger.valueOf(MAX_INTEGER);
            if(c.getJdbcType().equals("SMALLINT"))
                return BigInteger.valueOf(MAX_SMALLINT);
        }
        if(type == DataTypeEnum.LONGINT)
            return BigInteger.valueOf(MAX_LONGINT);
        if(type == DataTypeEnum.DECIMAL)
            return Util.generateLong(c.getLength());
        return null;
    }

    private boolean checkMsg(BigInteger allowMin, BigInteger allowMax){
        if( checkMinAndMax() == false )
            return false;
        BigInteger min = new BigInteger(minTextField.getText());
        if(  min.compareTo(allowMin) == -1 || min.compareTo(allowMax) == 1){
            JOptionPane.showMessageDialog(null,
                    String.format("最小值必须大于等于%d，小于等于%d", allowMin, allowMax));
            return false;
        }
        BigInteger max = new BigInteger(maxTextField.getText());
        if(max.compareTo(allowMin) == -1  || max.compareTo(allowMax) == 1) {
            JOptionPane.showMessageDialog(null,
                    String.format("最大值必须大于等于0,小于等于%d", allowMax, allowMax));
            return false;
        }
        if(min.compareTo(max) ==1 ){
            JOptionPane.showMessageDialog(null, "最小值必须小于等于最大值!");
            return  false;
        }

        return true;
    }

    private boolean checkColumnPanelContent(){
        ColumnConfig c = getColumnConfig();
        //generate
        if(generateCheckBox.isSelected() == false &&
                nameLabel.getText().toLowerCase().equals("id") != true ) {
            int r =JOptionPane.showConfirmDialog(null, "不生成此字段吗？");
            if(r != JOptionPane.YES_OPTION)
                return false;
        }
        //dataType
        DataTypeEnum dataType = (DataTypeEnum) dataTypeComboBox.getSelectedItem();
        if(dataType == DataTypeEnum.NONE){
            JOptionPane.showMessageDialog(null, "数据类型不能是NONE");
            return false;
        }

        if(c.getJdbcType().equals("DECIMAL") && c.getPrecision() == 0 ){
            JOptionPane.showMessageDialog(null, "类型是DECIMAL的字段请不要把精度设置为0 !");
            return false;
        }

        //LONGINT, INT, STRING, TIME, DATETIME, DATE, DECIMAL, BOOLEAN, NONE
        AppProperties appProperties = AppProperties.getAppProperties();
        if(dataType == DataTypeEnum.STRING && appProperties.getDatabaseType().equals("mssql") &&
                (c.getJdbcType().equals("CHAR") ||
                 c.getJdbcType().equals("VARCHAR") ||
                 c.getJdbcType().equals("LONGVARCHAR"))){
            JOptionPane.showMessageDialog(null,
                    String.format("mssql 数据库，不建议使用 %s 类型字段!", c.getJdbcType()));
            return false;
        }
        BigInteger allowMin;
        BigInteger allowMax;
        switch (dataType){
            case STRING:
            case INT:
            case LONGINT:
                allowMin = getAllowMin(dataType, c);
                allowMax = getAllowMax(dataType, c);
                return checkMsg(allowMin, allowMax);
            case DECIMAL:
                allowMin = getAllowMin(dataType, c);
                allowMax = getAllowMax(dataType, c);
                if( !checkMsg(allowMin, allowMax)) return false;
                return checkPrecison(precisionTextField.getText());
        }

        return true;
    }
    private boolean checkPrecison(String value){
        ColumnConfig c = getColumnConfig();
        Integer i;
        try{
            i = Integer.parseInt(value);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "精度必须填写，并且为数字!");
            return false;
        }
        if(i < 0 || i > c.getPrecision()){
            JOptionPane.showMessageDialog(null, String.format("精度必须在0-%s之间!", c.getPrecision().toString()));
            return  false;
        }
        return true;
    }
    //endregion
    private ColumnConfig getColumnConfig(){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tableTree.getLastSelectedPathComponent();
        String tableName = node.toString();
        int i = columnTable.getSelectedRow();
        String columnName = (String) columnTable.getValueAt(i, 0);
        return CodeGeneratorConfigUtil.findColumn(tableName, columnName,CodeGeneratorConfigUtil.getCodeGeneratorConfig().getTableList());
    }

    private void saveColumnPanelContent(){

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tableTree.getLastSelectedPathComponent();
        String tableName = node.toString();
        int r = columnTable.getSelectedRow();
        String columnName = (String) columnTable.getValueAt(r, 0);
        ColumnConfig c = CodeGeneratorConfigUtil.findColumn(tableName, columnName, CodeGeneratorConfigUtil.getCodeGeneratorConfig().getTableList());
        if(generateCheckBox.isSelected())
            c.setGen_generate(true);
        else
            c.setGen_generate(false);

        if(generateAsQueryCheckBox.isSelected())
            c.setGen_generateAsQuery(true);
        else
            c.setGen_generateAsQuery(false);

        c.setGen_dataType((DataTypeEnum) dataTypeComboBox.getSelectedItem());
        c.setGen_title(titleTextField.getText());
        c.setGen_memo(memoTextField.getText());
        if(requireCheckBox.isSelected())
            c.setGen_require(true);
        else
            c.setGen_require(false);

        if(minTextField.getText().trim().equals("")==false)
            c.setGen_min(Long.parseLong(minTextField.getText()));
        else
            c.setGen_min(null);
        if(maxTextField.getText().trim().equals("")==false)
            c.setGen_max(Long.parseLong(maxTextField.getText()));
        else
            c.setGen_max(null);
        if(precisionTextField.getText().trim().equals("")==false)
            c.setGen_precision(Integer.parseInt(precisionTextField.getText()));
        else
            c.setGen_precision(null);

        CodeGeneratorConfigUtil.saveCodeGeneratorConfigToFile(CodeGeneratorConfigUtil.getCodeGeneratorConfig());
    }

    private void clearColumnPanelContent(){
        nameLabel.setText("");
        generateCheckBox.setSelected(false);
        requireCheckBox.setSelected(false);
        dataTypeComboBox.setSelectedItem(DataTypeEnum.NONE);
        titleTextField.setText("");
        memoTextField.setText("");
        minTextField.setEnabled(true);
        minTextField.setBackground(enableColor);
        minTextField.setText("");
        maxTextField.setText("");
        maxTextField.setEnabled(true);
        maxTextField.setBackground(enableColor);
        precisionTextField.setText("");
        precisionTextField.setEnabled(true);
        precisionTextField.setBackground(enableColor);
    }

    private void setColumnPanelContentEnable(DataTypeEnum type){

        enableColor = titleTextField.getBackground();
        Color disableColor = Color.GRAY;
        Util.setComponentEnable(generateCheckBox, enableColor);
        Util.setComponentEnable(dataTypeComboBox, enableColor);
        Util.setComponentEnable(titleTextField, enableColor);
        Util.setComponentEnable(memoTextField, enableColor);
        Util.setComponentEnable(requireCheckBox, enableColor);
        Util.setComponentEnable(minTextField, enableColor);
        Util.setComponentEnable(maxTextField, enableColor);
        Util.setComponentEnable(precisionTextField, enableColor);

        if(type == DataTypeEnum.NONE){
            return ;
        }
        if(type == DataTypeEnum.LONGINT || type == DataTypeEnum.INT){
            Util.setComponentDisable(precisionTextField, disableColor);
        }
        if(type == DataTypeEnum.STRING){
            Util.setComponentDisable(precisionTextField, disableColor);
        }
        if(type == DataTypeEnum.TIME ||
                type == DataTypeEnum.DATETIME ||
                type == DataTypeEnum.DATE ||
                type == DataTypeEnum.BOOLEAN){
            Util.setComponentDisable(minTextField, disableColor);
            Util.setComponentDisable(maxTextField, disableColor);
            Util.setComponentDisable(precisionTextField, disableColor);
        }
        if(type == DataTypeEnum.DATETIME){
            return;
        }

    }

    private void showColumnPanelContent(int r){
        clearColumnPanelContent();

        ColumnConfig c = getColumnConfig();

        setColumnPanelContentEnable(convertJdbcTypeToDataType(c.getJdbcType()));

        nameLabel.setText(c.getName());
        //generate
        if(c.isGen_generate() == null )
        {
            if(c.getName().toLowerCase().equals("id"))
                generateCheckBox.setSelected(false);
            else
                generateCheckBox.setSelected(true);
        }else
            generateCheckBox.setSelected(c.isGen_generate());

        //generateAsQuery
        if(!(c.getGen_generateAsQuery() == null))
            if(c.getGen_generateAsQuery() == true)
                generateAsQueryCheckBox.setSelected(true);
            else
                generateAsQueryCheckBox.setSelected(false);
        else
            generateAsQueryCheckBox.setSelected(false);

        //dataType
        if(c.getGen_dataType() == null)
            dataTypeComboBox.setSelectedItem(convertJdbcTypeToDataType(c.getJdbcType()));
        else
            dataTypeComboBox.setSelectedItem(c.getGen_dataType());

        //
        DataCheckContent dc = getCheckContent(c);

        //title
        titleTextField.setText(c.getGen_title());
        //memo
        memoTextField.setText(c.getGen_memo());
        //require
        if( c.isGen_require() != null && c.isGen_require())
            requireCheckBox.setSelected(true);
        else
            requireCheckBox.setSelected(false);

        //min
        if(c.getGen_min() != null)
            minTextField.setText(c.getGen_min().toString());
        else
            if(dc.getCheck())
                minTextField.setText(dc.getMin().toString());
        //max
        if(c.getGen_max() != null)
            maxTextField.setText(c.getGen_max().toString());
        else
            if(dc.getCheck())
                maxTextField.setText(dc.getMax().toString());
        //precision
        if(c.getGen_precision() != null)
            precisionTextField.setText(c.getGen_precision().toString());
        else
            if(dc.getCheck() && dc.getCheckPrecision())
                precisionTextField.setText(dc.getPrecision().toString());
    }

    private final Long MIN_TINYINT = -128L;
    private final Long MAX_TINYINT = 127L;
    private final Long MIN_SMALLINT = -32768L;
    private final Long MAX_SMALLINT = 32767L;
    private final Long MIN_INTEGER = -2147483648L;
    private final Long MAX_INTEGER = 2147483647L;
    private final Long MIN_LONGINT = -9223372036854775808L;
    private final Long MAX_LONGINT = 9223372036854775807L;
    private DataCheckContent getCheckContent(ColumnConfig c){
        DataCheckContent r = new DataCheckContent();
        r.setCheck(true);
        r.setCheckPrecision(false);

        DataTypeEnum dataType = c.getGen_dataType();
        if(dataType == null)
            dataType = convertJdbcTypeToDataType(c.getJdbcType());

        if(dataType == DataTypeEnum.NONE ||
           dataType == DataTypeEnum.BOOLEAN ||
           dataType == DataTypeEnum.DATE ||
           dataType == DataTypeEnum.TIME ||
           dataType == DataTypeEnum.DATETIME ){
            r.setCheck(false);
            return r;
        }
        if(dataType == DataTypeEnum.STRING){
            r.setMin(BigInteger.valueOf(0));
            r.setMax(BigInteger.valueOf(c.getLength())); //mssql 要选择n开头的字符串; mysql 5.0 以后 字符串长度是按照字符存储的，不是字节。
            return r;
        }
        if(dataType == DataTypeEnum.INT){
            if(c.getJdbcType().equals("TINYINT")){
                r.setMin(BigInteger.valueOf(MIN_TINYINT));
                r.setMax(BigInteger.valueOf(MAX_TINYINT));
                return r;
            }
            if(c.getJdbcType().equals("SMALLINT")){
                r.setMin(BigInteger.valueOf(MIN_SMALLINT));
                r.setMax(BigInteger.valueOf(MAX_SMALLINT));
                return r;
            }
            if(c.getJdbcType().equals("INTEGER")){
                r.setMin(BigInteger.valueOf(MIN_INTEGER));
                r.setMax(BigInteger.valueOf(MAX_INTEGER));
                return r;
            }
        }
        if(dataType == DataTypeEnum.LONGINT){
            r.setMin(BigInteger.valueOf(MIN_LONGINT));
            r.setMax(BigInteger.valueOf(MAX_LONGINT));
            return r;
        }
        if(dataType == DataTypeEnum.DECIMAL){
           int n = c.getLength() - c.getPrecision();
           BigInteger l = Util.generateLong(n);
           r.setMin(l.multiply(BigInteger.valueOf(-1)));
           r.setMax(l);

           r.setCheckPrecision(true);
           r.setPrecision(c.getPrecision());
           return r;
        }

        return r;
    }

    private DataTypeEnum convertJdbcTypeToDataType(String JdbcType){
        Map<String,DataTypeEnum> map = new HashMap<String, DataTypeEnum>();
        map.put("CHAR", DataTypeEnum.STRING);
        map.put("NCHAR", DataTypeEnum.STRING);
        map.put("VARCHAR", DataTypeEnum.STRING);
        map.put("NVARCHAR", DataTypeEnum.STRING);
        map.put("LONGVARCHAR", DataTypeEnum.STRING);
        map.put("LONGNVARCHAR", DataTypeEnum.STRING);
        //不建议使用NUMERIC，所以默认设置成NONE类型
        //map.put("NUMERIC", DataTypeEnum.DECIMAL);
        map.put("DECIMAL", DataTypeEnum.DECIMAL);
        map.put("BIT", DataTypeEnum.BOOLEAN);
        map.put("BOOLEAN", DataTypeEnum.BOOLEAN);
        map.put("TINYINT", DataTypeEnum.INT);
        map.put("SMALLINT", DataTypeEnum.INT);
        map.put("INTEGER", DataTypeEnum.INT);
        map.put("BIGINT", DataTypeEnum.LONGINT);
        map.put("DATE", DataTypeEnum.DATE);
        map.put("TIME", DataTypeEnum.TIME);
        map.put("TIMESTAMP", DataTypeEnum.DATETIME);

        DataTypeEnum r = map.get(JdbcType);
        if(r == null)
            r = DataTypeEnum.NONE;
        return r;
    }

    private DefaultTableModel clearColumnTable(){
        DefaultTableModel t = (DefaultTableModel)columnTable.getModel();
        for(int i = t.getRowCount() -1 ; i >= 0 ; i--){
            t.removeRow(i);
        }
        return t;
    }

    private void setColumnTableData(String tableName){
        DefaultTableModel t = clearColumnTable();

        List<TableConfig> tableList = CodeGeneratorConfigUtil.getCodeGeneratorConfig().getTableList();
        TableConfig table = CodeGeneratorConfigUtil.findTable(tableName, tableList);
        for(ColumnConfig c : table.getColumns()){
            Vector<Object> vector = new Vector<Object>();
            vector.add(c.getName());
            vector.add(c.getJdbcType());
            vector.add(c.getLength());
            vector.add(c.getPrecision());
            vector.add(c.getIsNull());
            vector.add(c.getPrimaryKey());
            vector.add(c.getAutoIncrement());
            t.addRow(vector);
        }
        columnTable.setVisible(true);
    }

    private void data_set_database(String databaseType, String databaseUrl, String user, String password)
        throws Exception{
        boolean b = MybatisGenerateUtil.testDatabaseConnection(databaseType, databaseUrl, user, password);
        if (!b){
            JOptionPane.showMessageDialog(null, "数据库连接失败！");
            return;
        }

        //更改mybatisGenerate数据库参数
        MybatisGenerateUtil.saveDatabaseInfoToConfigFile(databaseType, databaseUrl, user, password);
        MybatisGenerateUtil.initTableToConfig();
        //获取数据库信息
        List<IntrospectedTable> tableList = MybatisGenerateUtil.getDatabaseTableInfo();
        ComparisonTableInfoResult result = Util.comparisonTableInfo(tableList, CodeGeneratorConfigUtil.getCodeGeneratorConfig());

        //提示，是否保存
        int c = JOptionPane.showConfirmDialog(null,formatResult(result), "", JOptionPane.NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if( c == JOptionPane.YES_OPTION){
            data_set_database_save(tableList, result,
                    databaseType, databaseUrl, user, password);

        }else{
            //不保存
            //恢复 mybatisGenerate 的数据库参数
            AppProperties appProperties = AppProperties.getAppProperties();
            databaseType = appProperties.getDatabaseType();
            databaseUrl = appProperties.getDatabaseUrl();
            user = appProperties.getDatabaseUser();
            password = appProperties.getDatabasePassword();
            MybatisGenerateUtil.saveDatabaseInfoToConfigFile(databaseType, databaseUrl, user, password);
        }
    }

    private void data_set_database_save(List<IntrospectedTable> tableList, ComparisonTableInfoResult result,
                                        String databaseType, String databaseUrl, String user, String password) throws  Exception {

        data_set_database_setCodeGenerator(tableList, result);
        //保存
        //AppProperties
        AppProperties appProperties = AppProperties.getAppProperties();
        appProperties.setDatabaseType(databaseType);
        appProperties.setDatabaseUrl(databaseUrl);
        appProperties.setDatabaseUser(user);
        appProperties.setDatabasePassword(password);
        //CodeGenerator
        CodeGeneratorConfigUtil.saveCodeGeneratorConfigToFile(CodeGeneratorConfigUtil.getCodeGeneratorConfig());
        //mybatisGenerate
        MybatisGenerateUtil.saveDatabaseInfoToConfigFile(databaseType, databaseUrl, user, password);
        //mybatis
        MybatisUtil.saveDatabaseInfoToConfigFile(databaseType,
                databaseUrl, user, password);
    }

    private void data_set_database_setCodeGenerator(List<IntrospectedTable> tableList,
                                                    ComparisonTableInfoResult result){

        CodeGeneratorConfig config = CodeGeneratorConfigUtil.getCodeGeneratorConfig();
        //remove table
        if(!result.getLessInfo().equals("")){
            String[] tableName = result.getLessInfo().trim().split(" ");
            List<TableConfig> tlist = config.getTableList();
            for(String tName : tableName){
                for(int i = tlist.size() - 1; i >= 0; i--){
                    if(tName.equals(tlist.get(i).getName()))
                        tlist.remove(i);
                }
            }
        }
        //remove column
        if(!result.getLessColumn().equals("")){
            String ts[] = result.getLessColumn().trim().split("\r\n");
            for (String t : ts) {
                String[] t1 = t.split(":");
                String tableName = t1[0];
                String[] cols = t1[1].split(" ");
                TableConfig table = CodeGeneratorConfigUtil.findTable(tableName, config.getTableList());
                List<ColumnConfig> columns = table.getColumns();
                for (String cName : cols) {
                    if (cName == null || cName.trim().equals(""))
                        continue;
                    for(int i = columns.size() - 1; i >= 0; i--){
                        if(columns.get(i).getName().equals(cName)){
                            columns.remove(i);
                        }
                    }
                }
            }
        }

        //add table
        if(!result.getMoreInfo().equals("")) {
            String[] tableName = result.getMoreInfo().trim().split(" ");
            //add table
            for (String tName : tableName) {
                TableConfig t = new TableConfig();
                List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
                t.setColumns(columns);
                IntrospectedTable dt = MybatisGenerateUtil.findDatabaseTable(tName, tableList);
                t.setName(tName);
                //t.setJavaClassName(new FullyQualifiedJavaType(dt.getBaseRecordType()).toString());
                t.setJavaClassName(app.api.generator.Util.toUpperCaseFirstOne(tName));
                for (IntrospectedColumn dc : dt.getAllColumns()) {
                    ColumnConfig c = new ColumnConfig();
                    c.setName(dc.getActualColumnName());
                    c.setJavaFieldName(dc.getJavaProperty());
                    c.setJdbcType(dc.getJdbcTypeName());
                    c.setAutoIncrement(dc.isAutoIncrement());
                    c.setPrimaryKey(dc.isIdentity());
                    c.setLength(dc.getLength());
                    c.setIsNull(dc.isNullable());
                    c.setPrecision(dc.getScale());
                    columns.add(c);
                }
                config.getTableList().add(t);
            }
        }
        //add column
        if(!result.getMoreColumn().trim().equals("")) {
            String ts[] = result.getMoreColumn().trim().split("\r\n");
            for (String t : ts) {
                String[] t1 = t.split(":");
                String tableName = t1[0];
                String[] cols = t1[1].split(" ");
                TableConfig table = CodeGeneratorConfigUtil.findTable(tableName, config.getTableList());
                IntrospectedTable dt = MybatisGenerateUtil.findDatabaseTable(tableName, tableList);
                List<ColumnConfig> columns = table.getColumns();
                for (String cName : cols) {
                    if (cName == null || cName.trim().equals(""))
                        continue;
                    IntrospectedColumn dc = MybatisGenerateUtil.findDatabaseColumn(tableName, cName, tableList);
                    ColumnConfig c = new ColumnConfig();
                    c.setName(dc.getActualColumnName());
                    c.setJavaFieldName(dc.getJavaProperty());
                    c.setJdbcType(dc.getJdbcTypeName());
                    c.setAutoIncrement(dc.isAutoIncrement());
                    c.setPrimaryKey(dc.isIdentity());
                    c.setLength(dc.getLength());
                    c.setIsNull(dc.isNullable());
                    c.setPrecision(dc.getScale());
                    columns.add(c);
                }
            }
        }

    }

    private String formatResult(ComparisonTableInfoResult result){

        String str = "数据库多: \r\n" ;
        String str1 = (result.getMoreInfo().equals("")) ? "无" : result.getMoreInfo();
        str += "    表: " + str1 + "\r\n" ;
        str1 = (result.getMoreColumn().equals("")) ? "无" : result.getMoreColumn();
        str += "    字段: " + str1 + "\r\n";
        str += "数据库少:\r\n";
        str1 = (result.getLessInfo().equals("")) ? "无" : result.getLessInfo();
        str +=  "   表:" + str1 + "\r\n";
        str1 = (result.getLessColumn().equals("")) ? "无" : result.getLessColumn();
        str += "   字段:" + str1 + "\r\n";

        str += "\r\n是否更新本地信息？";
        return str;
    }

    private void initTopPanel(JPanel topPanel, JFrame mainJFrame){
        //topShowPanel
//        JPanel topShowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
//        AppProperties appProperties = AppProperties.getAppProperties();
//        topShowPanel.add(new JLabel("代码生成:"));
//        JLabel l = new JLabel(appProperties.getCodeGeneratorConfig());
//        l.setForeground(Color.BLUE);
//        topShowPanel.add(l);
//
//        topShowPanel.add(new JLabel("mybatis配置文件:"));
//        topShowPanel.add(new JLabel());
//        l = new JLabel(appProperties.getMybatisConfig());
//        l.setForeground(Color.BLUE);
//        topShowPanel.add(l);
//        topShowPanel.add(new JLabel("mybatis生成文件:"));
//        l = new JLabel(appProperties.getMybatisGeneratorConfig());
//        l.setForeground(Color.BLUE);
//        topShowPanel.add(l);
//
//        topPanel.add(topShowPanel);

        //topOperatePanel
        JPanel topOperatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,5));
        topOperatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //mssql
        mssqlRadioButton = new JRadioButton();
        mssqlRadioButton.setText("mssql");
        mssqlRadioButton.setSelected(true);
        radioButtonGroup.add(mssqlRadioButton);
        topOperatePanel.add(mssqlRadioButton);
        //mysql
        mysqlRadioButton = new JRadioButton();
        mysqlRadioButton.setText("mysql");
        topOperatePanel.add(mysqlRadioButton);
        radioButtonGroup.add(mysqlRadioButton);
        //link
        linkDatabaseButton = new JButton("连接数据库");

        topOperatePanel.add(linkDatabaseButton);

        //status
        dataBaseStatusLabel = new JLabel("脱机");
        dataBaseStatusLabel.setForeground(Color.RED);
        topOperatePanel.add(dataBaseStatusLabel);

        codeGenerateButton = new JButton("代码生成");

        topOperatePanel.add(codeGenerateButton);
        topPanel.add(topOperatePanel);

        //topDatabaseConfPanel
        JPanel topDatabaseConfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));
        topDatabaseConfPanel.add(new JLabel("数据库配置："));
        dataBaseTextField = new JTextField(30);
        topDatabaseConfPanel.add(dataBaseTextField);
        topDatabaseConfPanel.add(new JLabel("用户名:"));
        userTextFile = new JTextField(5);
        topDatabaseConfPanel.add(userTextFile);
        topDatabaseConfPanel.add(new JLabel("密码:"));
        passwordTextFile = new JTextField(5);
        topDatabaseConfPanel.add(passwordTextFile);
        topPanel.add(topDatabaseConfPanel);
    }
    private void initLeftPanel(JPanel leftPanel){
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("表");
        tableTree = new JTree(rootNode);
        tableTree.getSelectionModel().setSelectionMode(
                DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
        tableTree.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
        int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        JScrollPane jsp = new JScrollPane(tableTree,v,h);
        leftPanel.add(jsp);
    }
    private void initCenterPanel(JPanel centerPanel){
        //table column
        String[] headers = {"字段","类型", "长度","精度", "空", "主键", "自增"};
        Object[] datas = {"xxx","1","2","3",false, true, false};
        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;//column == 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4 || columnIndex == 5 || columnIndex == 6) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        tableModel.setColumnIdentifiers(headers);
//        tableModel.addRow(datas);
//        tableModel.addRow(datas);
//        tableModel.addRow(datas);
//        tableModel.addRow(datas);
//        tableModel.addRow(datas);
//        tableModel.addRow(datas);

        columnTable = new JTable(tableModel);

        DefaultTableCellHeaderRenderer hr = new DefaultTableCellHeaderRenderer();
        hr.setHorizontalAlignment(JLabel.CENTER);
        columnTable.getTableHeader().setDefaultRenderer(hr);
        //columnTable.setVisible(false);
        JScrollPane tableJSP= new JScrollPane(columnTable);
        centerPanel.add(tableJSP);
    }
    private void initColumnPanel(JPanel centerPanel){
        GridBagLayout columnLayout = new GridBagLayout();
        columnSetPanel = new JPanel(columnLayout);
        columnSetPanel.setVisible(false);
        columnSetPanel.setBorder(BorderFactory.createEmptyBorder(0, 0,0,150));
        //
        int gridy = 0;
        //名字
        columnAddCompoent(columnSetPanel, columnLayout, new JLabel("名字"),
                1, GridBagConstraints.NONE , 0 , gridy);
        nameLabel = new JLabel("xxx");
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        columnAddCompoent(columnSetPanel, columnLayout, nameLabel,
                1, GridBagConstraints.HORIZONTAL , 1 , gridy);
        gridy++;
        //是否生成html
        columnAddCompoent(columnSetPanel, columnLayout, new JLabel("是否生成(html)"),
                1, GridBagConstraints.NONE , 0 , gridy);
        generateCheckBox = new JCheckBox();
        columnAddCompoent(columnSetPanel, columnLayout, generateCheckBox,
                1, GridBagConstraints.HORIZONTAL , 1 , gridy);
        gridy++;
        //是否最为查询条件html
        columnAddCompoent(columnSetPanel, columnLayout, new JLabel("是否做为查询条件(html)"),
                1, GridBagConstraints.NONE , 0 , gridy);
        generateAsQueryCheckBox = new JCheckBox();
        columnAddCompoent(columnSetPanel, columnLayout, generateAsQueryCheckBox,
                1, GridBagConstraints.HORIZONTAL , 1 , gridy);
        gridy++;
        //类型
        columnAddCompoent(columnSetPanel, columnLayout, new JLabel("类型"),
                1, GridBagConstraints.NONE , 0 , gridy);
        //
        dataTypeComboBox = new JComboBox( DataTypeEnum.values());
        columnAddCompoent(columnSetPanel, columnLayout, dataTypeComboBox,
                3, GridBagConstraints.HORIZONTAL, 1, gridy);
        //标题
        gridy++;
        columnAddCompoent(columnSetPanel, columnLayout, new JLabel("标题"),
                1, GridBagConstraints.NONE , 0 , gridy);
        titleTextField = new JTextField();
        columnAddCompoent(columnSetPanel, columnLayout, titleTextField,
                1, GridBagConstraints.HORIZONTAL , 1 , gridy);
        //说明
        gridy++;
        JLabel memoLabel = new JLabel("说明");
        memoLabel.setToolTipText("adbcddefe");
        columnAddCompoent(columnSetPanel, columnLayout, memoLabel,
                1, GridBagConstraints.NONE , 0 , gridy);
        memoTextField = new JTextField();
        columnAddCompoent(columnSetPanel, columnLayout, memoTextField,
                1, GridBagConstraints.HORIZONTAL , 1 , gridy);
        //必填
        gridy++;
        columnAddCompoent(columnSetPanel, columnLayout, new JLabel("必填"),
                1, GridBagConstraints.NONE , 0 , gridy);
        requireCheckBox = new JCheckBox();
        columnAddCompoent(columnSetPanel, columnLayout, requireCheckBox,
                1, GridBagConstraints.HORIZONTAL , 1 , gridy);

        //最小值/最小字符数
        gridy++;
        columnAddCompoent(columnSetPanel, columnLayout, new JLabel("最小值/最小字符数"),
                1, GridBagConstraints.NONE , 0 , gridy);
        minTextField = new JTextField();
        columnAddCompoent(columnSetPanel, columnLayout, minTextField,
                1, GridBagConstraints.HORIZONTAL , 1 , gridy);
        //最大值/最大字符数
        gridy++;
        columnAddCompoent(columnSetPanel, columnLayout, new JLabel("最大值/最大字符数"),
                1, GridBagConstraints.NONE , 0 , gridy);
        maxTextField = new JTextField();
        columnAddCompoent(columnSetPanel, columnLayout, maxTextField,
                1, GridBagConstraints.HORIZONTAL , 1 , gridy);

        //精度
        gridy++;
        columnAddCompoent(columnSetPanel, columnLayout, new JLabel("精度"),
                1, GridBagConstraints.NONE , 0 , gridy);
        precisionTextField = new JTextField();
        columnAddCompoent(columnSetPanel, columnLayout, precisionTextField,
                1, GridBagConstraints.HORIZONTAL , 1 , gridy);

        gridy++;
        //保存
        saveButton = new JButton("保存");
        columnAddCompoent(columnSetPanel, columnLayout, saveButton,
                1, GridBagConstraints.NONE, 1 , gridy);

        columnSetPanel.setPreferredSize(new Dimension(0,300));
        centerPanel.add(columnSetPanel, BorderLayout.SOUTH);
    }

    private void columnAddCompoent(JPanel p , GridBagLayout l,  Component comp,
                                          int weightx, int fill, int gridx, int gridy){
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        c.weightx = weightx;
        c.anchor = GridBagConstraints.LINE_END;
        c.fill = fill;
        l.setConstraints(comp, c);
        p.add(comp);
    }

}
