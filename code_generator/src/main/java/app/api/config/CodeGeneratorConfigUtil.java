package app.api.config;

import app.api.appProperties.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeGeneratorConfigUtil {

    private static CodeGeneratorConfig codeGeneratorConfig = null;

    public static CodeGeneratorConfig getCodeGeneratorConfig(){
        if(codeGeneratorConfig == null)
            codeGeneratorConfig = getCodeGeneratorConfigFromFile();

        return codeGeneratorConfig;
    }

    private static CodeGeneratorConfig  getCodeGeneratorConfigFromFile(){
        AppProperties appProperties = AppProperties.getAppProperties();
        File tfile = new File(appProperties.getCodeGeneratorConfig());
        StringBuilder stringBuilder = new StringBuilder();
        if ( !tfile.exists() )
            return new CodeGeneratorConfig();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(tfile));
            String s = null;
            while((s = bufferedReader.readLine())!=null){//使用readLine方法，一次读一行
                stringBuilder.append(s);
            }
            bufferedReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        CodeGeneratorConfig config = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            config = objectMapper.readValue(stringBuilder.toString(), CodeGeneratorConfig.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return config;
    }

    public static void saveCodeGeneratorConfigToFile(CodeGeneratorConfig config) {
        ObjectMapper objectMapper = new ObjectMapper();
        String s = null;
        try {
            s = objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File f = new File(AppProperties.getAppProperties().getCodeGeneratorConfig());
            BufferedWriter os = new BufferedWriter(new FileWriter(f));
            os.write(s);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TableConfig findTable(String tableName){
        List<TableConfig> tableConfigList = getCodeGeneratorConfig().getTableList();
        return  findTable(tableName, tableConfigList);
    }
    public static TableConfig findTable(String tableName, List<TableConfig> tableList){
        for(TableConfig t : tableList){
            if(tableName.equals(t.getName()))
                return t;
        }
        return null;
    }

    public static ColumnConfig findColumn(String tableName, String columnName, List<TableConfig> tableList){
        TableConfig t = null;
        if((t = findTable(tableName, tableList)) == null)
            return null;
        if(t.getColumns() == null)
            return null;
        for(ColumnConfig c : t.getColumns()){
            if(columnName.equals(c.getName()))
                return c;
        }
        return null;
    }

    public static ColumnConfig findColumn(String tableName, String columnName){
        List<TableConfig> tableList = CodeGeneratorConfigUtil.getCodeGeneratorConfig().getTableList();
        return findColumn(tableName, columnName, tableList);
    }

    public static void deleteTable(String tableName, List<TableConfig> tableList){
        for(int i = tableList.size() - 1; i >= 0 ; i--){
            if ( tableName.equals(tableList.get(i).getName()))
                tableList.remove(i);
        }
    }

    public static void deleteColumn(String tableName, String columnName, List<TableConfig> tableList){
        TableConfig t = null;
        t = findTable(tableName, tableList);
        List<ColumnConfig> c = t.getColumns();
        for(int i = c.size() - 1 ; i >= 0; i-- )
            if(columnName.equals(c.get(i).getName()))
                c.remove(i);
    }

    public static String configCheck(){
        return configCheck(null);
    }

    public static String configCheck(Set<String> tables){
        List<TableConfig> tableConfigs = CodeGeneratorConfigUtil.getCodeGeneratorConfig().getTableList();
        if(tables == null) {
            tables = new HashSet<String>();
            for (TableConfig tableConfig : tableConfigs)
                tables.add(tableConfig.getName());
        }
        StringBuilder sb = new StringBuilder();

        for(TableConfig tableConfig : tableConfigs){
            for(String tableName : tables) {
                if(tableName.equals(tableConfig.getName())) {
                    boolean addTable = false;
                    for (ColumnConfig columnConfig : tableConfig.getColumns()) {
                        if (columnConfig.getGen_dataType() == null) {
                            if (!addTable) {
                                sb.append(String.format("\r\n%s表中的字段没有保存配置信息:\r\n", tableConfig.getName()));
                                addTable = true;
                            }
                            sb.append(String.format("%s列 ", columnConfig.getName()));
                        }
                    }
                }
            }
        }

        return sb.toString();
    }

    public static String getPrimaryKeyJavaFieldName(String tableName){
        TableConfig tableConfig = findTable(tableName);
        for(ColumnConfig columnConfig : tableConfig.getColumns()){
            if(columnConfig.getPrimaryKey())
                return columnConfig.getJavaFieldName();
        }
        return null;
    }

    public static DataTypeEnum getPrimaryKeyDataType(String tableName){
        TableConfig tableConfig = findTable(tableName);
        for(ColumnConfig columnConfig : tableConfig.getColumns()){
            if(columnConfig.getPrimaryKey())
                return columnConfig.getGen_dataType();
        }
        return null;
    }
//    public static void addTable(String tableName,List<ColumnConfig> columns, List<TableConfig> tableList){
//        TableConfig t = new TableConfig();
//        t.setName(tableName);
//        t.setColumns(columns);
//        tableList.add(t);
//    }


}
