package app.api.config;

import app.api.appProperties.AppProperties;
import app.api.generator.Util;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mybatis.generator.api.*;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.JDBCConnectionFactory;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.db.DatabaseIntrospector;
import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class MybatisGenerateUtil {

    public static boolean testDatabaseConnection(String databaseType, String databaseUrl, String user, String password){
        Connection connection = null;

        String driverClass = "";
        if(databaseType.equals("mysql"))
            driverClass = "com.mysql.jdbc.Driver";
        if(databaseType.equals("mssql"))
            driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

        try {
            // 加载驱动
            Class.forName(driverClass);
            connection = DriverManager.getConnection(databaseUrl, user, password);
            connection.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //region savePathAndPackToConfigFileAndCheckCreatePath
    public static void savePathAndPackToConfigFileAndCheckCreatePath(String javaPath,String javaPack,String mapperPath ,String mapperPack) throws Exception{
        String modelPath = String.format("%s", javaPath);
        String modelPack = String.format("%s.entity", javaPack);
        String clientPath = String.format("%s", javaPath);
        String clientPack = String.format("%s.dao", javaPack);

        Util.checkCreatePath(modelPath);
        Util.checkCreatePath(clientPath);
        Util.checkCreatePath(mapperPath);

        AppProperties appProperties = AppProperties.getAppProperties();
        // 构造器
        SAXBuilder saxBuilder = new SAXBuilder();
        // 获取文档
        saxBuilder.setEntityResolver(new NoOpEntityResolver());
        Document document = saxBuilder.build(new File(appProperties.getMybatisGeneratorConfig()));
        // 得到根元素
        Element element = document.getRootElement();

        Element model = element.getChild("context").getChild("javaModelGenerator");
        model.getAttribute("targetPackage").setValue(modelPack);
        model.getAttribute("targetProject").setValue(modelPath);

        Element client = element.getChild("context").getChild("javaClientGenerator");
        client.getAttribute("targetPackage").setValue(clientPack);
        client.getAttribute("targetProject").setValue(clientPath);

        Element mapper = element.getChild("context").getChild("sqlMapGenerator");
        mapper.getAttribute("targetPackage").setValue(mapperPack);
        mapper.getAttribute("targetProject").setValue(mapperPath);

        //保存
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat().setIndent("    "));
        out.output(document, new FileWriter(appProperties.getMybatisGeneratorConfig()));
    }
    //endregion

    private static String getDatabasetType(){
        String dataType = "";
        //sqlStatement  SqlServer MySql
        AppProperties appProperties = AppProperties.getAppProperties();
        if(appProperties.getDatabaseType().equalsIgnoreCase("mysql"))
            dataType = "MySql";
        if(appProperties.getDatabaseType().equalsIgnoreCase("mssql"))
            dataType = "SqlServer";
        return dataType;
    }
    private static Document getConfigDocument() throws  Exception{
        AppProperties appProperties = AppProperties.getAppProperties();
        String dataType = getDatabasetType();
        // 构造器
        SAXBuilder saxBuilder = new SAXBuilder();
        // 获取文档
        saxBuilder.setEntityResolver(new NoOpEntityResolver());
        Document document = saxBuilder.build(new File(appProperties.getMybatisGeneratorConfig()));

        return document;
    }

    private static void saveConfig(Document document) throws Exception{
        AppProperties appProperties = AppProperties.getAppProperties();
        //保存
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat().setIndent("    "));
        out.output(document, new FileWriter(appProperties.getMybatisGeneratorConfig()));
    }

    public static void initTableToConfig()throws Exception{
        String dataType = getDatabasetType();
        Document document = getConfigDocument();
        // 得到根元素
        Element element = document.getRootElement().getChild("context");
        element.removeChildren("table");
//        <table tableName="%">
//            <generatedKey column="id" identity="true" sqlStatement="MySql"/>
//        </table>
        Element t = new Element("table");
        t.setAttribute("tableName", "%");

        Element p = new Element("property");
        p.setAttribute("name", "useActualColumnNames");
        p.setAttribute("value", "true" );
        t.addContent(p);

        p = new Element("generatedKey");
        p.setAttribute("column", "id");
        p.setAttribute("identity", "true");
        p.setAttribute("sqlStatement", dataType);
        t.addContent(p);
        element.addContent(t);
        saveConfig(document);
    }

    public static void saveTableToConfig(Set<String> tables) throws Exception{
        String dataType = getDatabasetType();
        Document document = getConfigDocument();
        // 得到根元素
        Element element = document.getRootElement().getChild("context");
        element.removeChildren("table");

//        <table tableName="user">
//            <generatedKey column="id" identity="true" sqlStatement="MySql" />
//            <property name="useActualColumnNames" value="true"/>
//        </table>

        for(String table : tables){
            Element t = new Element("table");
            t.setAttribute("tableName", table);
            t.setAttribute("domainObjectName", app.api.generator.Util.toUpperCaseFirstOne(table));
            //catalog="" schema=""


            Element p = new Element("property");
            p.setAttribute("name", "useActualColumnNames");
            p.setAttribute("value", "true" );
            t.addContent(p);

            Element g = new Element("generatedKey");
            g.setAttribute("column", "id");
            g.setAttribute("identity", "true");
            g.setAttribute("sqlStatement", dataType);
            t.addContent(g);

            TableConfig tableConfig = CodeGeneratorConfigUtil.findTable(table);
            for(ColumnConfig columnConfig : tableConfig.getColumns()) {
                //String jdbcType = columnConfig.getJdbcType();
                //String columnName = columnConfig.getName();
                if(columnConfig.getJdbcType().equals("LONGNVARCHAR")) {
                    //解决ntext不支持example的like
                    //<columnOverride column="LONG_VARCHAR_FIELD" javaType="java.lang.String" jdbcType="VARCHAR" />
                    Element g1 = new Element("columnOverride");
                    g1.setAttribute("column", columnConfig.getName());
                    g1.setAttribute("javaType", "java.lang.String");
                    g1.setAttribute("jdbcType", "VARCHAR");
                    t.addContent(g1);
                }
            }

            element.addContent(t);
        }
        saveConfig(document);
    }

    public static void generate(Set tables) throws  Exception {
        AppProperties appProperties = AppProperties.getAppProperties();
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File(appProperties.getMybatisGeneratorConfig());
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        //myBatisGenerator.generate(null,null, tables);
    }

    public static void saveDatabaseInfoToConfigFile(String databaseType,String databaseUrl, String user, String password)
    throws  Exception
    {
        AppProperties appProperties = AppProperties.getAppProperties();
        // 构造器
        SAXBuilder saxBuilder = new SAXBuilder();
        // 获取文档
        saxBuilder.setEntityResolver(new NoOpEntityResolver());
        Document document = saxBuilder.build(new File(appProperties.getMybatisGeneratorConfig()));
        // 得到根元素
        Element element = document.getRootElement();

        Element jdbcConnection = element.getChild("context").getChild("jdbcConnection");
        Attribute url = jdbcConnection.getAttribute("connectionURL");
        url.setValue(databaseUrl);
        String driverClass = "";
        if(databaseType.equals("mysql"))
            driverClass = "com.mysql.jdbc.Driver";
        if(databaseType.equals("mssql"))
            driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        Attribute driverClassAttr = jdbcConnection.getAttribute("driverClass");
        driverClassAttr.setValue(driverClass);
        Attribute userIdAttr = jdbcConnection.getAttribute("userId");
        userIdAttr.setValue(user);
        Attribute passwordAttr = jdbcConnection.getAttribute("password");
        passwordAttr.setValue(password);

        //保存
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat().setIndent("    "));
        out.output(document, new FileWriter(appProperties.getMybatisGeneratorConfig()));
    }

    public static IntrospectedTable findDatabaseTable(String tableName, List<IntrospectedTable> tableList) {
        for(IntrospectedTable t : tableList){
            if (tableName.equals(t.getFullyQualifiedTable().getIntrospectedTableName())){
                return t;
            }
        }
        return null;
    }

    public static IntrospectedColumn findDatabaseColumn(String tableName, String columnName, List<IntrospectedTable> tableList){
        IntrospectedTable t = null;

        if( (t = findDatabaseTable(tableName, tableList)) == null){
            return null;
        }

        for(IntrospectedColumn c : t.getAllColumns()){
            if(columnName.equals(c.getActualColumnName()))
                return c;
        }

        return null;
    }

    //region getDatabaseTableInfo
    public static List<IntrospectedTable> getDatabaseTableInfo() throws Exception{
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        AppProperties appProperties = AppProperties.getAppProperties();
        File configFile = new File(appProperties.getMybatisGeneratorConfig());
        //System.out.println(configFile.getAbsolutePath());
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);

        List<Context> contextsToRun = config.getContexts();
        if (config.getClassPathEntries().size() > 0) {
            ClassLoader classLoader = ClassloaderUtility.getCustomClassloader(config.getClassPathEntries());
            ObjectFactory.addExternalClassLoader(classLoader);
        }

        int totalSteps = 0;

        Context context;
        Iterator var11;
        for(var11 = ((List)contextsToRun).iterator(); var11.hasNext(); totalSteps += context.getIntrospectionSteps()) {
            context = (Context)var11.next();
        }

        var11 = ((List)contextsToRun).iterator();

        Set<String> fullyQualifiedTableNames = null;

        ProgressCallback callback1 = new NullProgressCallback();

        List<IntrospectedTable> ts = null ;
        while(var11.hasNext()) {
            context = (Context)var11.next();
            ts = introspectTables(context, callback1, warnings, fullyQualifiedTableNames);
        }

        if( ts == null){
            throw  new Exception("没有获取到表信息");
        }

        return getTables(ts);
    }

    private static List<IntrospectedTable> getTables(List<IntrospectedTable> list){
        List<IntrospectedTable> rlist = new ArrayList<IntrospectedTable>();
        for (IntrospectedTable t : list){
            if(t.getTableType() != null) {
                if (t.getTableType().equals("TABLE") &&
                        !t.getFullyQualifiedTable().toString().trim().equals("trace_xe_action_map") &&
                        !t.getFullyQualifiedTable().toString().trim().equals("trace_xe_event_map"))
                    rlist.add(t);
            }
        }
        return rlist;
    }

    private static List<IntrospectedTable> introspectTables(Context context ,ProgressCallback callback, List<String> warnings, Set<String> fullyQualifiedTableNames) throws SQLException, InterruptedException {
        List<IntrospectedTable> introspectedTables = new ArrayList();
        JavaTypeResolver javaTypeResolver = ObjectFactory.createJavaTypeResolver(context, warnings);
        Connection connection = null;

        try {
            callback.startTask(Messages.getString("Progress.0"));
            connection = getConnection(context);
            DatabaseIntrospector databaseIntrospector = new DatabaseIntrospector(context, connection.getMetaData(), javaTypeResolver, warnings);
            Iterator var7 = context.getTableConfigurations().iterator();

            while(true) {
                TableConfiguration tc;
                String tableName;
                do {
                    if (!var7.hasNext()) {
                        return  introspectedTables;
                    }

                    tc = (TableConfiguration)var7.next();
                    tableName = StringUtility.composeFullyQualifiedTableName(tc.getCatalog(), tc.getSchema(), tc.getTableName(), '.');
                } while(fullyQualifiedTableNames != null && fullyQualifiedTableNames.size() > 0 && !fullyQualifiedTableNames.contains(tableName));

                if (!tc.areAnyStatementsEnabled()) {
                    warnings.add(Messages.getString("Warning.0", tableName));
                } else {
                    callback.startTask(Messages.getString("Progress.1", tableName));
                    List<IntrospectedTable> tables = databaseIntrospector.introspectTables(tc);
                    if (tables != null) {
                        introspectedTables.addAll(tables);
                    }

                    callback.checkCancel();
                }
            }
        } finally {
            closeConnection(connection);
        }
    }

    private static   Connection getConnection(Context context) throws SQLException {
        Object connectionFactory;
        if (context.getJdbcConnectionConfiguration() != null) {
            connectionFactory = new JDBCConnectionFactory(context.getJdbcConnectionConfiguration());
        } else {
            connectionFactory = ObjectFactory.createConnectionFactory(context);
        }

        return ((ConnectionFactory)connectionFactory).getConnection();
    }

    private static void closeConnection( Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException var3) {
            }
        }

    }
    //endregion
}
