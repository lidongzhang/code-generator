package app.api.config;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.List;

public class Util {

    //region ComparisonTableInfoResult
    public static ComparisonTableInfoResult comparisonTableInfo(List<IntrospectedTable> tableinfo, CodeGeneratorConfig config) {
        ComparisonTableInfoResult r = new ComparisonTableInfoResult();
        boolean more = false;
        boolean less = false;
        String moreTable = "";
        String moreColumn = "";
        String lessTable = "";
        String lessColumn = "";

        //find config
        for (IntrospectedTable t : tableinfo) {
            String t_moreColumn = "";
            String tableName = t.getFullyQualifiedTable().getIntrospectedTableName();
            TableConfig table = CodeGeneratorConfigUtil.findTable(tableName, config.getTableList());
            if (table == null) {
                more = true;
                moreTable = moreTable + " " + tableName;
            } else {
                for (IntrospectedColumn c : t.getAllColumns()) {
                    String columnName = c.getActualColumnName();
                    ColumnConfig tc = CodeGeneratorConfigUtil.findColumn(tableName, columnName, config.getTableList());
                    if (tc == null) {
                        more = true;
                        t_moreColumn += " " + columnName;
                    }
                }
            }
            if (!t_moreColumn.equals("")) {
                moreColumn += tableName + ":" + t_moreColumn + "\r\n";
            }
        }


        //find database
        for(TableConfig t : config.getTableList()){
            String t_lessColumn = "";
            IntrospectedTable table = MybatisGenerateUtil.findDatabaseTable(t.getName(), tableinfo);
            if(table == null){
                less = true;
                lessTable += " " + t.getName();
            }else{
                for(ColumnConfig c : t.getColumns()){
                    IntrospectedColumn column = MybatisGenerateUtil.findDatabaseColumn(t.getName(), c.getName(), tableinfo);
                    if(column == null){
                        less = true;
                        t_lessColumn += " " + c.getName();
                    }
                }
            }
            if(!t_lessColumn.equals(""))
                lessColumn += t.getName() + ":" + t_lessColumn + "\r\n";
        }

        r.setMoreInfo(moreTable);
        r.setLessInfo(lessTable);
        r.setMoreColumn(moreColumn);
        r.setLessColumn(lessColumn);

        if(more && less)
            r.setResult(ComparisonResultTypeEnum.MOREANDLESS);
        else if (more)
            r.setResult(ComparisonResultTypeEnum.MORE);
        else if (less)
            r.setResult(ComparisonResultTypeEnum.LESS);
        else
            r.setResult(ComparisonResultTypeEnum.EQUAL);

        return r;
    }
    //endregion

    public static boolean isInt(String s){
        boolean b = false;
        try{
            Integer.parseInt(s);
            b = true;
        }catch (Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }

    public static boolean isLong(String s){
        boolean b = false;
        try{
            Long.parseLong(s);
            b = true;
        }catch (Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }

    public static BigInteger generateLong(int numCount, char num){
        String t = new String(new char[numCount]).replace("\0", String.valueOf(num));
        return new BigInteger(t);
    }

    public static BigInteger generateLong(int numCount){
        return generateLong(numCount, '9');
    }

    public static void setComponentEnable(JComponent component, Color backgroundColor){
        component.setEnabled(true);
        component.setBackground(backgroundColor);
    }

    public static void setComponentDisable(JComponent component, Color backgroundColor){
        component.setEnabled(false);
        component.setBackground(backgroundColor);
    }
}

