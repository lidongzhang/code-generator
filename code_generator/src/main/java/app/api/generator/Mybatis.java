package app.api.generator;

import app.api.appProperties.AppProperties;
import app.api.config.MybatisGenerateUtil;

import java.util.Set;

public class Mybatis {

    public static void generate(String mapperPack, String javaPack, Set tables) throws  Exception{
        AppProperties appProperties = AppProperties.getAppProperties();
        appProperties.setGeneratorJavaMybatisPackage(javaPack);
        String javaPath = appProperties.getGeneratorMybatisPath();
        String mapperPath = appProperties.getGeneratorResourcesMybatisPath();
        MybatisGenerateUtil.savePathAndPackToConfigFileAndCheckCreatePath(javaPath, javaPack, mapperPath, mapperPack);
        MybatisGenerateUtil.saveTableToConfig(tables);
        MybatisGenerateUtil.generate(tables);
    }
}
