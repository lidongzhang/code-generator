package com.adong.mybatis.generator.plugins.core;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

public class PaginationPlugin extends PluginAdapter {

    public boolean validate(List<String> list) {
        return true;
    }


    //首字母转大写
    private String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    private void addField(TopLevelClass topLevelClass, String fieldName) {
        PrimitiveTypeWrapper integerWrapper = FullyQualifiedJavaType.getIntInstance().getPrimitiveTypeWrapper();

        String uFieldName = toUpperCaseFirstOne(fieldName);

        Field field = new Field();
        field.setName(fieldName);
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(integerWrapper);
        topLevelClass.addField(field);

        Method setField = new Method();
        setField.setVisibility(JavaVisibility.PUBLIC);
        setField.setName(String.format("set%s", uFieldName));
        setField.addParameter(new Parameter(integerWrapper, fieldName));
        setField.addBodyLine(String.format("this.%s = %s;", fieldName, fieldName));
        topLevelClass.addMethod(setField);

        Method getField = new Method();
        getField.setVisibility(JavaVisibility.PUBLIC);
        getField.setReturnType(integerWrapper);
        getField.setName(String.format("get%s", uFieldName));
        getField.addBodyLine(String.format("return %s;", fieldName));
        topLevelClass.addMethod(getField);
    }

    /**
     * 为每个Example类添加limit和offset属性已经set、get方法
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        PrimitiveTypeWrapper integerWrapper = FullyQualifiedJavaType.getIntInstance().getPrimitiveTypeWrapper();

        addField(topLevelClass, "limit");
        addField(topLevelClass, "offset");
        addField(topLevelClass, "rowidStart");
        addField(topLevelClass, "rowidEnd");

        return true;
    }

    private void addSelectSqlserverByExample(Document document, IntrospectedTable introspectedTable) {
        String pack = this.context.getJavaModelGeneratorConfiguration().getTargetPackage();
        String tableName = introspectedTable.getFullyQualifiedTable().toString();
        String type = String.format("%s.%s", pack, toUpperCaseFirstOne(tableName));

        //select
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", "selectSqlserverByExample"));
        select.addAttribute(new Attribute("parameterType", type));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        select.addElement(new TextElement("<!--" +
                "                \"      WARNING - @mbg.generated\n" +
                "                \"      This element is automatically generated by MyBatis Generator, do not modify.\n" +
                "                \"    -->"));
        select.addElement(new TextElement(" select * from (\n" +
                "    select *, ROW_NUMBER() OVER(Order by"));
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "orderByClause != null"));
        ifElement.addElement(new TextElement("${orderByClause}"));
        select.addElement(ifElement);
        select.addElement(new TextElement(" ) AS RowId from\n" +
                "    (\n" +
                "\n" +
                "    select"));
        ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "distinct"));
        ifElement.addElement(new TextElement("distinct"));
        select.addElement(ifElement);
        XmlElement includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", "Base_Column_List"));
        select.addElement(includeElement);
        select.addElement(new TextElement(String.format("from %s", tableName)));
        ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "_parameter != null"));
        includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", "Example_Where_Clause"));
        ifElement.addElement(includeElement);
        select.addElement(ifElement);
        select.addElement(new TextElement(") __tmp__a__\n" +
                "    ) as __tmp__b__\n" +
                "    where RowId  between"));
        ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "rowidStart != null"));
        ifElement.addElement(new TextElement("${rowidStart}"));
        select.addElement(ifElement);
        select.addElement(new TextElement("and"));
        ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "rowidEnd != null"));
        ifElement.addElement(new TextElement("${rowidEnd}"));
        select.addElement(ifElement);

        document.getRootElement().addElement(select);

    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        //add selectSqlserverByExample
        addSelectSqlserverByExample(document, introspectedTable);
        return true;
    }

    /**
     * 为 Mapper.xml 的 selectByExample 添加 limit
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        addPageElement(element);
        return true;
    }

    /**
     * 为 Mapper.xml 的 selectByExampleWithBLOBs 添加 limit
     */
    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element,
                                                                  IntrospectedTable introspectedTable) {
        addPageElement(element);
        return true;
    }

    private void addPageElement(XmlElement element) {
        XmlElement ifLimitNotNullElement = new XmlElement("if");
        ifLimitNotNullElement.addAttribute(new Attribute("test", "limit != null"));

        XmlElement ifOffsetNotNullElement = new XmlElement("if");
        ifOffsetNotNullElement.addAttribute(new Attribute("test", "offset != null"));
        ifOffsetNotNullElement.addElement(new TextElement("limit ${offset}, ${limit}"));
        ifLimitNotNullElement.addElement(ifOffsetNotNullElement);

        XmlElement ifOffsetNullElement = new XmlElement("if");
        ifOffsetNullElement.addAttribute(new Attribute("test", "offset == null"));
        ifOffsetNullElement.addElement(new TextElement("limit ${limit}"));
        ifLimitNotNullElement.addElement(ifOffsetNullElement);

        element.addElement(ifLimitNotNullElement);
    }


    @Override
    public boolean clientGenerated(Interface inter, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Method method = new Method();
        method.setName("selectSqlserverByExample");
        String table = introspectedTable.getBaseRecordType();
        FullyQualifiedJavaType rType = new FullyQualifiedJavaType(String.format("List<%s>", table));
        method.setReturnType(rType);

        String example = introspectedTable.getExampleType();
        FullyQualifiedJavaType eType = new FullyQualifiedJavaType(example);
        Parameter p = new Parameter(eType, "example");
        method.addParameter(p);
        this.context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        inter.addMethod(method);

        return true;
    }
}


