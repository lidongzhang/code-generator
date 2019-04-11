package app.api.generator;

import app.api.appProperties.AppProperties;
import app.api.config.*;
import org.mybatis.generator.api.dom.java.*;


import java.util.List;
import java.util.Set;

public class Java {

    public static void test(String path, String pack, Set tables){
        FullyQualifiedJavaType f = new FullyQualifiedJavaType("abc.dong.TestEntity");
        TopLevelClass t = new TopLevelClass(f);

        t.addAnnotation("@abc");
        t.addImportedType("org.apache.ibatis.annotations.Param");


        Method m = new Method();
        m.setVisibility(JavaVisibility.PUBLIC);
        m.setReturnType(FullyQualifiedJavaType.getStringInstance());
        m.setName("method1");
        Parameter p = new Parameter(FullyQualifiedJavaType.getStringInstance(), "str");
        m.addParameter(p);
        m.addBodyLine("System.out.println(\"ok\");");
        m.addAnnotation("@method");

        t.addMethod(m);

        path = String.format("%s/%s/%s", path, pack.replace('.', '/'), "t.java");
        //File targetFile = new File(path);
        System.out.println(t.getFormattedContent());
    }

    public static void generator(String path, String pack, Set<String> tables) throws Exception{
        List<TableConfig> tableConfigList = CodeGeneratorConfigUtil.getCodeGeneratorConfig().getTableList();
//        String javaPack = AppProperties.getAppProperties().getGeneratorJavaPackage();
        AppProperties appProperties = AppProperties.getAppProperties();
        String templateServicePath = appProperties.getTemplatesServicePath();
        String templateControllerPath = appProperties.getTemplatesControllerPath();
        String interfaceTemplateServiceFileName = String.format("%s/table/TableInterface.java", templateServicePath);
        String implTemplateServiceFileName = String.format("%s/table/impl/TableImpl.java", templateServicePath);
        String controllerTemplateControllerFileName = String.format("%s/TableController.java", templateControllerPath);
        String mybatisPack = appProperties.getGeneratorJavaMybatisPackage();
        for(String t : tables){
            TableConfig tableConfig = CodeGeneratorConfigUtil.findTable(t);
            checkCreatePath(path, pack, t);
            String tableName = t;
            String uTableName = tableConfig.getJavaClassName();
            String tpath = String.format("%s/%s", path, pack.replace('.', '/'));

            String interfaceFileName = String.format("%s/service/%s/%sInterface.java", tpath, tableName, uTableName);
            generatorInterface(interfaceTemplateServiceFileName, interfaceFileName, tableName, uTableName, mybatisPack);

            String implFileName = String.format("%s/service/%s/impl/%sImpl.java", tpath, tableName, uTableName);
            generatorImpl(implTemplateServiceFileName, implFileName, tableName, uTableName, mybatisPack, tableConfig);

            String controllerFileName = String.format("%s/controller/%sController.java", tpath, uTableName);
            generatorController(controllerTemplateControllerFileName, controllerFileName, tableName, uTableName);
        }
    }

    private static void checkCreatePath(String path, String pack, String tableName) throws Exception{
        String packPath = pack.replace('.', '/');
        String p = String.format("%s/%s/service/%s/impl", path, packPath,Util.toLowerCaseFirstOne(tableName));
        String p1 = String.format("%s/%s/controller", path, packPath);
        Util.checkCreatePath(p);
        Util.checkCreatePath(p1);
    }

    private static void generatorController(String templateFileName, String fileName,
                                         String tableName, String uTableName)
    throws Exception
    {

        String templateContent = Util.readFile(templateFileName);
        templateContent = templateContent.replace("{t}", tableName);
        templateContent = templateContent.replace("{T}", uTableName);
        Util.writeFile(fileName, templateContent);
    }

    private static void generatorInterface(String templateFileName, String fileName,
                                           String tableName, String uTableName, String mybatisPack)
    throws Exception
    {
        String templateContent = Util.readFile(templateFileName);
        //{mybatisPack} {t} {T}
        templateContent = templateContent.replace("{t}", tableName);
        templateContent = templateContent.replace("{T}", uTableName);
        templateContent = templateContent.replace("{mybatisPack}", mybatisPack);
        Util.writeFile(fileName, templateContent);
    }

    private static void generatorImpl(String templateFileName, String fileName,
                                      String tableName, String uTableName, String mybatisPack, TableConfig tableConfig)
    throws Exception
    {


        String templateContent = Util.readFile(templateFileName);
        String primaryKeyName = CodeGeneratorConfigUtil.getPrimaryKeyJavaFieldName(tableName);
        DataTypeEnum dataType = CodeGeneratorConfigUtil.getPrimaryKeyDataType(tableName);

        String idType = "";
        String parseType = "";
        switch (dataType){
            case INT:
                idType = "Integer";
                parseType = "Int";
                break;
            case LONGINT:
                idType = "Long";
                parseType = "Long";
                break;
        }

        String setlid = "{idType} lid = {idType}.parse{parseType}(id.toString());";
        setlid = setlid.replace("{idType}", idType);
        setlid = setlid.replace("{parseType}", parseType);
        templateContent = templateContent.replace("{setlid}", setlid);
        templateContent = templateContent.replace("{Id}", Util.toUpperCaseFirstOne(primaryKeyName));
        //{mybatisPack} {t} {T}
        templateContent = templateContent.replace("{t}", tableName);
        templateContent = templateContent.replace("{T}", uTableName);
        templateContent = templateContent.replace("{mybatisPack}", mybatisPack);
        templateContent = templateContent.replace("{SelectDataAddExampleColumns}", generatorImplSelectDataAddExample(tableConfig));
        templateContent = templateContent.replace("{checkDataColumns}", generatorImplCheckData(tableConfig));
        Util.writeFile(fileName, templateContent);
    }

//        Object c1 = map.get("c1");
//        r = Validation.check("c1", DataTypeEnum.STRING, c1, false, 0L, 100L, 0);
//        if(r.getCode().equals(Response.CODE_FAIL)){
//            b = false;
//            sb.append(r.getMsg());
//        }else{
//            if(c1 != null && !(c1.toString().isEmpty()) )
//                c.andC1Like(String.format("%%%s%%", c1.toString()));
//        }
//
//        Object c2 = map.get("c2");
//        r = Validation.check("c2", DataTypeEnum.INT, c2, false, 0L, 100L, 0);
//        if(r.getCode().equals(Response.CODE_FAIL)){
//            b = false;
//            sb.append(String.format("/r/n%s", r.getMsg()));
//        }else{
//            if(c2 != null && !c2.toString().isEmpty())
//                c.andC2EqualTo(Integer.parseInt(c2.toString()));
//        }
//    {SelectDataAddExampleColumns}

    private static String generatorImplSelectDataAddExample(TableConfig tableConfig){
        String strTmp = "Object {c} = map.get(\"{c}\");\n" +
                "r = Validation.check(\"{c}\", DataTypeEnum.{TYPE}, {c}, false, {min}L, {max}L, {per});\n" +
                "if(r.getCode().equals(Response.CODE_FAIL)){\n" +
                "    b = false;\n" +
                "    sb.append(r.getMsg());\n" +
                "}else{\n" +
                "    if({c} != null && !({c}.toString().isEmpty()) )\n" +
                "        c.and{C}{fun}({value});\n" +
                "}\n";

        String str_value = "String.format(\"%%%s%%\", {c}.toString())";
        String int_value = "Integer.parseInt({c}.toString())";
        String byte_value = "Byte.parseByte({c}.toString())";
        String short_value = "Short.parseShort({c}.toString())";
        String long_value = "Long.parseLong({c}.toString())";
        String date_value = "new SimpleDateFormat(\"yyyy-MM-dd\").parse({c}.toString())";
        String datetime_value = "new SimpleDateFormat(\"yyyy-MM-dd hh:mm:ss\").parse({c}.toString())";
        String time_value = "new SimpleDateFormat(\"hh:mm:ss\").parse({c}.toString())";
        String decimal_value = "new BigDecimal({c}.toString())";
        String boolean_value = "Boolean.parseBoolean({c}.toString())";

        String str = "";
        StringBuilder sb = new StringBuilder();
        for(ColumnConfig column : tableConfig.getColumns()){
            String c = column.getJavaFieldName();
            String C = Util.toUpperCaseFirstOne(c);
            String TYPE = column.getGen_dataType().toString();
            String min = column.getGen_min() == null ? "0" : column.getGen_min().toString();
            String max = column.getGen_max() == null ? "0" : column.getGen_max().toString();
            String per = column.getGen_precision() == null ? "0" : column.getGen_precision().toString();
            String value = "";
            //String: Like ; Other:  EqualTo
            String fun = "";
            switch (column.getGen_dataType()){
                case STRING:
                    value = str_value;
                    fun = "Like";
                    break;
                case INT:
                    value = int_value;
                    if(column.getJdbcType().equals("TINYINT"))
                        value = byte_value;
                    if(column.getJdbcType().equals("SMALLINT"))
                        value = short_value;

                    fun = "EqualTo";
                    break;
                case DATETIME:
                    value = datetime_value;
                    fun = "EqualTo";
                    break;
                case DATE:
                    value = date_value;
                    fun = "EqualTo";
                    break;
                case TIME:
                    value = time_value;
                    fun = "EqualTo";
                    break;
                case LONGINT:
                    value = long_value;
                    fun = "EqualTo";
                    break;
                case DECIMAL:
                    value = decimal_value;
                    fun = "EqualTo";
                    break;
                case BOOLEAN:
                    value = boolean_value;
                    fun = "EqualTo";
                    break;
            }
            value = value.replace("{c}", c);
            str = strTmp.replace("{value}", value);
            str = str.replace("{c}", c);
            str = str.replace("{C}", C);
            str = str.replace("{TYPE}", TYPE);
            str = str.replace("{min}", min);
            str = str.replace("{max}", max);
            str = str.replace("{per}", per);
            str = str.replace("{fun}", fun);
            sb.append(str);
        }
        return  sb.toString();
    }

//        if(t1Changed.getC1_changed()){
//            r = Validation.check("c1", DataTypeEnum.STRING, t1Changed.getC1(),
//                    true, 1L,10L, 0);
//            if(r.getCode().equals(Response.CODE_FAIL)){
//                b = false;
//                sb.append(String.format("%s\r\n", r.getMsg()));
//            }
//        }
//        if(t1Changed.getC2_changed()){
//            r = Validation.check("c1", DataTypeEnum.STRING, t1Changed.getC2(),
//                    true, 1L,10L, 0);
//            if(r.getCode().equals(Response.CODE_FAIL)){
//                b = false;
//                sb.append(String.format("%s\r\n", r.getMsg()));
//            }
//        }
//    {checkDataColumns}

    private static String generatorImplCheckData(TableConfig tableConfig){
        String strTmp = "if({t}Changed.get{C}_changed()){\n" +
                "    r = Validation.check(\"{c}\", DataTypeEnum.{TYPE}, {t}Changed.get{C}(),\n" +
                "            {required}, {min}L, {max}L, {per});\n" +
                "    if(r.getCode().equals(Response.CODE_FAIL)){\n" +
                "        b = false;\n" +
                "        sb.append(String.format(\"%s\\r\\n\", r.getMsg()));\n" +
                "    }\n" +
                "}\n";
        StringBuilder sb = new StringBuilder();
        //{t} {C} {c} {TYPE} {min} {max}
        String t = tableConfig.getName();
        for (ColumnConfig columnConfig : tableConfig.getColumns()){
            String C = Util.toUpperCaseFirstOne(columnConfig.getJavaFieldName());
            String c = columnConfig.getJavaFieldName();
            String TYPE = columnConfig.getGen_dataType().toString();
            String min = columnConfig.getGen_min() == null ? "0" : columnConfig.getGen_min().toString();
            String max = columnConfig.getGen_max() == null ? "0" : columnConfig.getGen_max().toString();
            String per = columnConfig.getGen_precision() == null ? "0" : columnConfig.getGen_precision().toString();
            String required = columnConfig.isGen_require() ? "true" : "false";
            String str = strTmp.replace("{C}", C);
            str = str.replace("{t}", t);
            str = str.replace("{c}", c);
            str = str.replace("{TYPE}", TYPE);
            str = str.replace("{min}", min);
            str = str.replace("{max}", max);
            str = str.replace("{per}", per);
            //{required}
            str = str.replace("{required}", required);
            sb.append(str);
        }
        return sb.toString();
    }
}
