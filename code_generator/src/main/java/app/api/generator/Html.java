package app.api.generator;

import java.util.List;
import java.util.Set;

import app.api.appProperties.AppProperties;
import app.api.config.CodeGeneratorConfigUtil;
import app.api.config.ColumnConfig;
import app.api.config.DataTypeEnum;
import app.api.generator.dom.Document;
import app.api.generator.dom.AttributeNoValue;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class Html {

    public static void test(String path, Set tables){

        Document document = new Document();
        TextElement text = new TextElement("<#include \"../base/html/layout.ftl\">");
        document.addElement(text);

        XmlElement xml = new XmlElement("#macro");
        AttributeNoValue a = new AttributeNoValue("a");
        xml.addAttribute(a);
        document.addElement(xml);


        System.out.println(document.getFormattedContentNoHeaderNoDoctype());
    }

    public static void generator(String htmlPath, Set<String> tables)throws Exception{

        AppProperties appProperties = AppProperties.getAppProperties();
        appProperties.setGeneratorHtmlPath(htmlPath);
        String templatePath = appProperties.getTemplatesHtmlPath();

        for(String tableName : tables){
            String outPath = String.format("%s/%s", htmlPath, Util.toLowerCaseFirstOne(tableName));
            Util.checkCreatePath(outPath);

            generatorIndex(String.format("%s/index.ftl", templatePath), outPath, tableName);
            generatorView(String.format("%s/view.ftl", templatePath), outPath, tableName);
            generatorSave(String.format("%s/save.ftl", templatePath), outPath, tableName);
        }
    }

    //region index
    private static void generatorIndex(String templateFilename, String outPath, String tableName)throws Exception{
        //{t} {id} {selectContent} {tableColumnConfig}
        String outFilename = String.format("%s/index.ftl", outPath);
        String templateContent = Util.readFile(templateFilename);
        templateContent = templateContent.replace("{t}", tableName);
        String idColumnName = CodeGeneratorConfigUtil.getPrimaryKeyJavaFieldName(tableName);
        templateContent = templateContent.replace("{id}", idColumnName);
        templateContent = templateContent.replace("{selectContent}", getIndexSelectContent(tableName));
        templateContent = templateContent.replace("{tableColumnConfig}", getIndexTableColumnConfig(tableName));
        templateContent = templateContent.replace("{tableColumnEditAfterConfig}", getIndexEditAfterToTableConfig(tableName));

        Util.writeFile(outFilename, templateContent);
    }
    private static String getIndexSelectContent(String tableName) {
        //    <tr>
        //        <td>
        //            <div class="layui-form-item">
        //                <label class="layui-form-label">c1</label>
        //                <div class="layui-input-block">
        //                    <input type="text" id="c1" name="c1"  lay-verify-min="1" lay-verify-max="10" lay-verify="string"  autocomplete="off"  class="layui-input">
        //                </div>
        //            </div>
        //        </td>
        //    </tr>
        //    <tr>
        //        <td>
        //            <div class="layui-form-item">
        //                <label class="layui-form-label">c2</label>
        //                <div class="layui-input-inline">
        //                    <input type="text" id="c2" name="c2"  lay-verify-min="1" lay-verify-max="100" lay-verify="int"  autocomplete="off" class="layui-input">
        //                </div>
        //                <div class="layui-form-mid layui-word-aux">辅助文字</div>
        //            </div>
        //        </td>
        //    </tr>
        //{selectContent}

        String str="<tr>\n" +
                "    <td>\n" +
                "        <div class=\"layui-form-item\">\n" +
                "            <label class=\"layui-form-label\">{title}</label>\n" +
                "            <div class=\"layui-input-block\">\n" +
                "                <input type=\"text\" id=\"{jc}\" name=\"{jc}\"  lay-verify-min=\"{min}\" lay-verify-max=\"{max}\" lay-verify=\"{datatype}\"  autocomplete=\"off\"  class=\"layui-input\"\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </td>\n" +
                "</tr>\n" +
                "<tr>\n";
        //{c} {title} {min} {max} {per} {datatype}
        return replaceContent(str, tableName, ColumnType.SELECT);
    }

    private static String getIndexTableColumnConfig(String tableName){
//        {field: 'ID', title: 'ID'},
//        {field: 'c1', title: 'c1'},
//        {tableColumnConfig}

        //{c} {title}
        String str = ",{field: '{jc}', title: '{title}'}\n";

        return replaceContent(str, tableName, ColumnType.COMMON);
    }

    private static String getIndexEditAfterToTableConfig(String tableName){
        //{c1: field.c1}
        //{tableColumnEditAfterConfig}

        String str = "\n{jc}: field.{jc},";
        return Util.trimFirstAndLastChar(replaceContent(str, tableName, ColumnType.COMMON), ",");
    }
    //endregion

    //region view
    private static void generatorView(String templateFilename, String outPath, String tableName)throws Exception{
        String str = "<tr>\n" +
                "                    <td style=\"width:50px;\">\n" +
                "                        <label class=\"admin-form-label\" >{title}</label>\n" +
                "                    </td>\n" +
                "                    <td >\n" +
                "                        <label id=\"{jc}\" class=\"admin-form-content\"></label>\n" +
                "                    </td>\n" +
                "                </tr>\n";

        String outFilename = String.format("%s/view.ftl", outPath);
        String templateContent = Util.readFile(templateFilename);

        //{t}
        templateContent = templateContent.replace("{t}", tableName);
        //{columnTr}
        String columnTr = replaceContent(str, tableName, ColumnType.COMMON);
        templateContent = templateContent.replace("{columnTr}", columnTr);

        String str1 = "$('#{jc}').text(d.result.data.{jc} || '');\n";
        //{columnSetText}
        String columnSetText = replaceContent(str1, tableName, ColumnType.COMMON);
        templateContent = templateContent.replace("{columnSetText}", columnSetText);
        Util.writeFile(outFilename, templateContent);
    }
    //endregion

    //region save
    private static void generatorSave(String templateFilename, String outPath, String tableName)throws Exception{
        String str = "<tr>\n" +
                "                    <td style=\"width:50px;\">\n" +
                "                        <label class=\"admin-form-label\" >{title}</label>\n" +
                "                    </td>\n" +
                "                    <td >\n" +
                "                        <div class=\"layui-input-inline\">\n" +
                "                            <input type=\"text\" id=\"{jc}\" name=\"{jc}\"  lay-verify-min=\"{min}\" lay-verify-max=\"{max}\" lay-verify=\"{verify}\"  autocomplete=\"off\" class=\"layui-input\">\n" +
                "                        </div>\n" +
                "                    </td>\n" +
                "                </tr>\n";
        String outFilename = String.format("%s/save.ftl", outPath);
        String templateContent = Util.readFile(templateFilename);
        //{columnTr}
        String columnTr = replaceContent(str, tableName, ColumnType.COMMON);
        templateContent = templateContent.replace("{columnTr}", columnTr);

        //{laydate}
        str = "laydate.render({\n" +
                "                elem: '#{jc}', type:'{datatype}',trigger: 'click'\n" +
                "            });\n";
        String laydate = replaceContent(str, tableName, ColumnType.COMMON, GeneratorColumnType.DATEANDTIMEANDDATETIME);
        templateContent = templateContent.replace("{laydate}", laydate);

        //{columnSetVal}
        str = "$('#{jc}').val(d.result.data.{jc} || '');\n";
        String columnSetVal = replaceContent(str, tableName, ColumnType.COMMON);
        templateContent = templateContent.replace("{columnSetVal}", columnSetVal);

        //整体 {id} {t}
        String id = CodeGeneratorConfigUtil.getPrimaryKeyJavaFieldName(tableName);
        templateContent = templateContent.replace("{id}", id);
        templateContent = templateContent.replace("{t}", tableName);

        Util.writeFile(outFilename, templateContent);
    }
    //endregion

    //region helper

    private enum ColumnType {COMMON, SELECT, ALL};
    private enum GeneratorColumnType {COMMON, DATEANDTIMEANDDATETIME};

    private static String replaceContent(String templateContent, String tableName, ColumnType columnType, GeneratorColumnType generatorColumnType){
        List<ColumnConfig> columnConfigList = CodeGeneratorConfigUtil.findTable(tableName).getColumns();
        StringBuilder sb = new StringBuilder();
        for(ColumnConfig column : columnConfigList){
            if(!(columnType == ColumnType.ALL) && column.getPrimaryKey())
                continue;
            if(columnType == ColumnType.SELECT && !(column.getGen_generateAsQuery()))
                continue;
            if(columnType == ColumnType.COMMON && !(column.isGen_generate()))
                continue;
            if(generatorColumnType == GeneratorColumnType.DATEANDTIMEANDDATETIME && !(column.isGen_generate()))
                continue;
            if(generatorColumnType == GeneratorColumnType.DATEANDTIMEANDDATETIME &&
                column.getGen_dataType() != DataTypeEnum.DATE &&
                    column.getGen_dataType() != DataTypeEnum.DATETIME &&
                    column.getGen_dataType() != DataTypeEnum.TIME
                )
                continue;

            String c = column.getName();
            String jc = column.getJavaFieldName();
            String min = column.getGen_min() == null ? "" : column.getGen_min().toString();
            String max = column.getGen_max() == null ? "" : column.getGen_max().toString();
            String per = column.getGen_precision() == null ? "" : column.getGen_precision().toString();
            String title = column.getGen_title();
            String datatype = getDatatype(column.getGen_dataType());
            String verify = "";
            if(column.isGen_require())
                verify = String.format("required|%s", datatype);
            else
                verify = datatype;

            String content = templateContent.replace("{c}", c);
            content = content.replace("{jc}", jc);
            content = content.replace("{min}", min);
            content = content.replace("{max}", max);
            content = content.replace("{per}", per);
            content = content.replace("{title}", title);
            content = content.replace("{datatype}", datatype);
            //{verify}
            content = content.replace("{verify}", verify);
            sb.append(content);
        }
        return sb.toString();
    }

    private static String replaceContent(String templateContent, String tableName, ColumnType columnType){
        return replaceContent(templateContent, tableName, columnType, GeneratorColumnType.COMMON);
    }

    private static String getDatatype(DataTypeEnum dataTypeEnum){
        String str = "";
        switch (dataTypeEnum){
            case INT:
                str = "int";
                break;
            case STRING:
                str = "string";
                break;
            case DATE:
                str = "date";
                break;
            case DATETIME:
                str = "datetime";
                break;
            case TIME:
                str = "time";
                break;
            case LONGINT:
                str = "int";
                break;
            case DECIMAL:
                str = "decimal";
                break;

        }
        return str;
    }
    //endregion
}
