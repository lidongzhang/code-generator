package app.api.appProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import app.api.appProperties.SafeProperties;

public class AppProperties {

    private String version;
    private String databaseType;
    private String databaseUrl;
    private String mybatisGeneratorConfig;
    private String mybatisConfig;
    private String codeGeneratorConfig;
    private String databaseUser;
    private String databasePassword;

    private String generatorMenuConfigPath;
    private String generatorJavaPath;
    private String generatorMybatisPath;
    private String generatorHtmlPath;
    private String generatorJavaPackage;

    private String generatorJavaMybatisPackage;
    private String generatorResourcesMybatisPackage;
    private String generatorResourcesMybatisPath;

    private String templatesServicePath;
    private String templatesControllerPath;
    private String templatesHtmlPath;

    private static AppProperties appProperties = null;
    private static String appPropertiesFileName = "conf/app.properties";


    public String getGeneratorMenuConfigPath() {
        return generatorMenuConfigPath;
    }

    public void setGeneratorMenuConfigPath(String generatorMenuConfigPath) {
        this.changeValueByPropertyName("app.generator-menu-config-path", generatorMenuConfigPath);
        this.generatorMenuConfigPath = generatorMenuConfigPath;
    }

    public String getTemplatesServicePath() {
        return templatesServicePath;
    }

    public void setTemplatesServicePath(String templatesServicePath) {
        this.changeValueByPropertyName("app.templates-service-path", templatesServicePath);
        this.templatesServicePath = templatesServicePath;
    }

    public String getTemplatesControllerPath() {
        return templatesControllerPath;
    }

    public void setTemplatesControllerPath(String templatesControllerPath) {
        this.changeValueByPropertyName("app.templates-controller-path", templatesControllerPath);
        this.templatesControllerPath = templatesControllerPath;
    }

    public String getTemplatesHtmlPath() {
        return templatesHtmlPath;
    }

    public void setTemplatesHtmlPath(String templatesHtmlPath) {
        this.changeValueByPropertyName("app.templates-html-path", templatesHtmlPath);
        this.templatesHtmlPath = templatesHtmlPath;
    }

    public String getGeneratorResourcesMybatisPath() {
        return generatorResourcesMybatisPath;
    }

    public void setGeneratorResourcesMybatisPath(String generatorResourcesMybatisPath) {
        this.changeValueByPropertyName("app.generator-resources-mybatis-path", generatorResourcesMybatisPath);
        this.generatorResourcesMybatisPath = generatorResourcesMybatisPath;
    }


    public String getGeneratorResourcesMybatisPackage() {
        return generatorResourcesMybatisPackage;
    }

    public void setGeneratorResourcesMybatisPackage(String generatorResourcesMybatisPackage) {
        this.changeValueByPropertyName("app.generator-resources-mybatis-package", generatorResourcesMybatisPackage);
        this.generatorResourcesMybatisPackage = generatorResourcesMybatisPackage;
    }

    public String getGeneratorJavaMybatisPackage() {
        return generatorJavaMybatisPackage;
    }

    public void setGeneratorJavaMybatisPackage(String generatorJavaMybatisPackage) {
        this.changeValueByPropertyName("app.generator-java-mybatis-package", generatorJavaMybatisPackage);
        this.generatorJavaMybatisPackage = generatorJavaMybatisPackage;
    }

    public String getGeneratorJavaPackage() {
        return generatorJavaPackage;
    }

    public void setGeneratorJavaPackage(String generatorJavaPackage) {
        this.changeValueByPropertyName("app.generator-java-package", generatorJavaPackage);
        this.generatorJavaPackage = generatorJavaPackage;
    }

    public String getGeneratorMybatisPath() {
        return generatorMybatisPath;
    }

    public void setGeneratorMybatisPath(String generatorMybatisPath) {
        this.changeValueByPropertyName("app.generator-mybatis-path", generatorMybatisPath);
        this.generatorMybatisPath = generatorMybatisPath;
    }

    public String getGeneratorHtmlPath() {
        return generatorHtmlPath;
    }

    public void setGeneratorHtmlPath(String generatorHtmlPath) {
        this.changeValueByPropertyName("app.generator-html-path", generatorHtmlPath);
        this.generatorHtmlPath = generatorHtmlPath;
    }

    public String getGeneratorJavaPath() {
        return generatorJavaPath;
    }

    public void setGeneratorJavaPath(String generatorJavaPath) {
        this.changeValueByPropertyName("app.generator-java-path", generatorJavaPath);
        this.generatorJavaPath = generatorJavaPath;
    }



    private AppProperties(){
        String tfileName = new File(appPropertiesFileName).getAbsolutePath();
        SafeProperties p = new SafeProperties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(tfileName);
            p.load(in);
            in.close();
            version = p.getProperty("app.version");
            databaseType = p.getProperty("app.database-type");
            databaseUrl = p.getProperty("app.database-url");
            mybatisConfig = p.getProperty("app.mybatis-config");
            mybatisGeneratorConfig = p.getProperty("app.mybatis-generator-config");
            codeGeneratorConfig = p.getProperty("app.code-generator-config");
            databaseUser = p.getProperty("app.database-user");
            databasePassword = p.getProperty("app.database-password");

            generatorMenuConfigPath = p.getProperty("app.generator-menu-config-path");
            generatorHtmlPath = p.getProperty("app.generator-html-path");
            generatorJavaPackage = p.getProperty("app.generator-java-package");
            generatorJavaPath = p.getProperty("app.generator-java-path");
            generatorJavaMybatisPackage = p.getProperty("app.generator-java-mybatis-package");
            generatorMybatisPath = p.getProperty("app.generator-mybatis-path");
            generatorResourcesMybatisPackage = p.getProperty("app.generator-resources-mybatis-package");
            generatorResourcesMybatisPath = p.getProperty("app.generator-resources-mybatis-path");

            templatesControllerPath = p.getProperty("app.templates-controller-path");
            templatesHtmlPath = p.getProperty("app.templates-html-path");
            templatesServicePath = p.getProperty("app.templates-service-path");


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static AppProperties getAppProperties(){
        if ( appProperties == null)
            appProperties = new AppProperties();
        return appProperties;
    }

    private  boolean changeValueByPropertyName(String propertyName, String propertyValue) {
        // 获取src下的文件路径
        String propertiesFileName = new File(appPropertiesFileName).getAbsolutePath();
        Boolean writeOK = true;
        SafeProperties p = new SafeProperties();
        FileInputStream in;
        try {
            in = new FileInputStream(propertiesFileName);
            p.load(in);//
            in.close();
            p.setProperty(propertyName, propertyValue);// 设置属性值，如不属性不存在新建
            FileOutputStream out = new FileOutputStream(propertiesFileName);// 输出流
            p.store(out, null);// 设置属性头，如不想设置，请把后面一个用""替换掉
            out.flush();// 清空缓存，写入磁盘
            out.close();// 关闭输出流
        } catch (Exception e) {
            e.printStackTrace();
            writeOK = false;
        }
        return writeOK;
    }

    //region 属性

    public String getDatabaseUser() {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser) {
        this.changeValueByPropertyName("app.database-user", databaseUser);
        this.databaseUser = databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.changeValueByPropertyName("app.database-password", databasePassword);
        this.databasePassword = databasePassword;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.changeValueByPropertyName("app.version", version);
        this.version = version;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.changeValueByPropertyName("app.database-type", databaseType);
        this.databaseType = databaseType;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.changeValueByPropertyName("app.database-url", databaseUrl);
        this.databaseUrl = databaseUrl;
    }

    public String getMybatisGeneratorConfig() {
        return mybatisGeneratorConfig;
    }

    public void setMybatisGeneratorConfig(String mybatisGeneratorConfig) {
        this.changeValueByPropertyName("app.mybatis-generator-config", mybatisGeneratorConfig);
        this.mybatisGeneratorConfig = mybatisGeneratorConfig;
    }

    public String getMybatisConfig() {
        return mybatisConfig;
    }

    public void setMybatisConfig(String mybatisConfig) {
        this.changeValueByPropertyName("app.mybatis-config", mybatisConfig);
        this.mybatisConfig = mybatisConfig;
    }

    public String getCodeGeneratorConfig() {
        return codeGeneratorConfig;
    }

    public void setCodeGeneratorConfig(String codeGeneratorConfig) {
        this.changeValueByPropertyName("app.code-generator-config", codeGeneratorConfig);
        this.codeGeneratorConfig = codeGeneratorConfig;
    }
    //endregion
}
