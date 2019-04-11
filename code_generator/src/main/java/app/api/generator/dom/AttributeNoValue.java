package app.api.generator.dom;

public class AttributeNoValue extends org.mybatis.generator.api.dom.xml.Attribute {

    public AttributeNoValue(String name, String value){
        super(name, value);
    }

    public AttributeNoValue(String name){
        super(name, "");
    }

    @Override
    public String getFormattedContent() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        return sb.toString();
    }
}
