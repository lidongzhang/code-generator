package app.api.generator.dom;


import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.xml.Element;

import java.util.HashSet;
import java.util.Set;

public class Document extends org.mybatis.generator.api.dom.xml.Document  {

    private Set<Element> elements  = new HashSet<Element>();

    public void addElement(Element element){
        elements.add(element);
    }

    public String getFormattedContentNoHeaderNoDoctype() {
        StringBuilder sb = new StringBuilder();
        for(Element element: elements){
            sb.append(element.getFormattedContent(0));
            OutputUtilities.newLine(sb);
        }
        return sb.toString();
    }

}
