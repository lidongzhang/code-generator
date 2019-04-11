package app.api.config;

import app.api.appProperties.AppProperties;
import com.sun.org.apache.xpath.internal.XPathAPI;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class MybatisUtil {

    //region saveDatabaseInfoToConfigFile
    public static void saveDatabaseInfoToConfigFile(String databaseType,String databaseUrl, String user, String password){
        AppProperties appProperties = AppProperties.getAppProperties();

        try{
            File f = new File(appProperties.getMybatisConfig());
            // 构造器
            SAXBuilder saxBuilder = new SAXBuilder();
            // 获取文档
            saxBuilder.setEntityResolver(new NoOpEntityResolver());
            org.jdom2.Document document = saxBuilder.build(f);

            Element element = document.getRootElement();
            Element entity = element.getChild("environments").getChild("environment").getChild("dataSource");
//            Node entity =  XPathAPI.selectSingleNode(element, "/");
//            String c = entity.getTextContent();
            ///environments/environment/dataSource
//            Element root = doc.getDocumentElement();
//            root.getElementsByTagName("environments").item(0).;

            //dataSource
            String driverClass = "";
            if(databaseType.equals("mysql"))
                setNodeAttribute(entity, "driver", "com.mysql.jdbc.Driver");
            if(databaseType.equals("mssql"))
                setNodeAttribute(entity, "driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");

            setNodeAttribute(entity, "userId", user);
            setNodeAttribute(entity, "password", password);
            setNodeAttribute(entity, "url", databaseUrl);

            //保存
            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat().setIndent("    "));
            out.output(document, new FileWriter(appProperties.getMybatisConfig()));

        }
        catch (Exception e){
            e.printStackTrace();

        }
    }

    private static void setNodeAttribute (Element node, String name, String value){
        List<Element> nodeList = node.getChildren();
        for(Element e : nodeList){
            if( e.getAttribute("name").getValue().equals(name) )
                e.getAttribute("value").setValue(value);
        }
    }
    //endregion


}
