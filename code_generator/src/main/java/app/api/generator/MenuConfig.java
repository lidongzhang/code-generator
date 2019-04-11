package app.api.generator;

import app.api.appProperties.AppProperties;

import java.util.Set;

public class MenuConfig {

    public static void generator(Set<String> tableNames)throws Exception{
        String str ="{\n" +
                "        \"title\": \"{t}\",\n" +
                "        \"href\": \"{t}/index.html\"\n" +
                "      },";

        StringBuilder sb = new StringBuilder();
        for(String tableName : tableNames){
            String temp = str.replace("{t}", tableName);
            sb.append(temp);
        }
        AppProperties appProperties = AppProperties.getAppProperties();
        String outPath = appProperties.getGeneratorMenuConfigPath();
        Util.checkCreatePath(outPath);
        String outFilename = String.format("%s/config.json", outPath);
        str = String.format("[%s]", Util.trimFirstAndLastChar(sb.toString(), ","));
        Util.writeFile(outFilename, str);
    }
}
