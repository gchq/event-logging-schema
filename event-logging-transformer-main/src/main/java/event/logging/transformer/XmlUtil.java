package event.logging.transformer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.nio.file.Path;

public class XmlUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);

    private XmlUtil() {
    }

    public static Schema createSchema(final Path schemaPath, final boolean enableSecureProcessing) {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schemaFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, enableSecureProcessing);
        } catch (final Exception e) {
            throw new RuntimeException("Unable to set Secure Processing feature on schema factory", e);
        }
        // It seems to ignore this property
        // System.setProperty("jdk.xml.maxOccurLimit", "10000");
        LOGGER.debug("Creating schema object for file " + schemaPath.toAbsolutePath().normalize());
        try {
            // attempt to construct a schema object from the file. Will fail if our schema
            // is not a valid w3c XML Schema. This will ensure the transformation chain
            // generates a valid schema
            return schemaFactory.newSchema(schemaPath.toFile());
        } catch (final SAXException e1) {
            throw new RuntimeException("Error initialising schema object", e1);
        }
    }
}
