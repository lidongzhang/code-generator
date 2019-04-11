package app.gui;

import app.api.generator.Util;

import java.util.Set;

public class GenerateTask implements  Runnable {

    private boolean generatorMybatis;
    private boolean generatorHtml;
    private boolean generatorJava;
    private String mapperPack;
    private String htmlPath;
    private String javaPath;
    private String mybatisPack;
    private String javaPack;
    private Set<String> tables;

    public GenerateTask( boolean generatorMybatis,
                         boolean generatorHtml,
                         boolean generatorJava,
                         String mapperPack,
                         String htmlPath,
                         String javaPath,
                         String mybatisPack,
                         String javaPack,
                         Set<String> tables ){
        this.generatorMybatis = generatorMybatis;
        this.generatorHtml = generatorHtml;
        this.generatorJava = generatorJava;
        this.mapperPack = mapperPack;
        this.htmlPath = htmlPath;
        this.javaPath = javaPath;
        this.mybatisPack = mybatisPack;
        this.javaPack = javaPack;
        this.tables = tables;

    }

    @Override
    public void run(){
        try {
            Util.generator(generatorMybatis, generatorHtml, generatorJava,
                    mapperPack, htmlPath, javaPath,
                    mybatisPack, javaPack, tables
            );
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
