package app.api.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Set;

public class Util {

    public static Integer processIndex =0;

    public static Integer getProcessIndex() {
        return processIndex;
    }

    public static void setProcessIndex(Integer processIndex) {
        Util.processIndex = processIndex;
    }

    public static void generator(
            boolean generatorMybatis,
            boolean generatorHtml,
            boolean generatorJava,
            String mapperPack,
            String htmlPath,
            String javaPath,
            String mybatisPack,
            String javaPack,
            Set<String> tables
    ) throws  Exception
    {

        if(generatorMybatis)
            Mybatis.generate(mapperPack, mybatisPack, tables);

        if(generatorJava)
            Java.generator(javaPath, javaPack, tables);

        if(generatorHtml) {
            Html.generator(htmlPath, tables);
            MenuConfig.generator(tables);
        }

        processIndex = 0;
        while(processIndex < 100){
            processIndex++;
            Thread.sleep(10);
            //System.out.println(processIndex);
        }


    }

    //region helper
    public static void checkCreatePath(String path) throws Exception{
        File f = new File(path);
        if(f.exists() && f.isFile())
            throw new Exception("%s 是文件,不是目录!");
        if(!f.exists())
            f.mkdirs();
    }

    //首字母转大写
    public static  String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    //首字母转小写
    public static  String toLowerCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    public static String readFile(String fileName) throws  Exception {
        StringBuilder sb = new StringBuilder();
        FileReader reader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(String.format("%s\r\n", line));
        }
        bufferedReader.close();
        reader.close();
        return sb.toString();
    }

    public static void writeFile(String fileName, String content) throws  Exception{
        File file = new File(fileName);
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content);
        fileWriter.flush();
        fileWriter.close();

    }

    /**
     * 去除首尾指定字符
     * @param str 字符串
     * @param element 指定字符
     * @return
     */
    public static String trimFirstAndLastChar(String str, String element){
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        do{
            int beginIndex = str.indexOf(element) == 0 ? 1 : 0;
            int endIndex = str.lastIndexOf(element) + 1 == str.length() ? str.lastIndexOf(element) : str.length();
            str = str.substring(beginIndex, endIndex);
            beginIndexFlag = (str.indexOf(element) == 0);
            endIndexFlag = (str.lastIndexOf(element) + 1 == str.length());
        } while (beginIndexFlag || endIndexFlag);
        return str;
    }
    //endregion
}
