package app.api.config;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;


public class NoOpEntityResolver implements EntityResolver {

    public InputSource resolveEntity(String publicId, String systemId) {

        try {
            String str = "";
            return new InputSource(new ByteArrayInputStream(str.getBytes("UTF-8")));
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
}
