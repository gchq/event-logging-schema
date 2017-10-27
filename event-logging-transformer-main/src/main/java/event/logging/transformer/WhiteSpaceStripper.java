package event.logging.transformer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

class WhiteSpaceStripper extends XMLFilterImpl implements LexicalHandler {
    private static final String[] ATT_ORDER = new String[] { "name", "ref", "type", "minOccurs", "maxOccurs" };

    private final StringBuilder content = new StringBuilder();
    private LexicalHandler lexicalHandler;

    @Override
    public void startElement(final String uri,
                             final String localName,
                             final String name,
                             final Attributes atts) throws SAXException {
        outputChars();

        final Attributes newAtts = rearrangeAtts(atts, ATT_ORDER);

        super.startElement(uri, localName, name, newAtts);
    }

    /**
     * Ensure certain attributes are in a consistent order. Any attributes matching those in the order argument
     * will be placed first, in the order they appear in 'order'. All others will appear in their original
     * order after that.
     */
    private Attributes rearrangeAtts(final Attributes atts,
                                     final String[] order) {
        final AttributesImpl newAtts = new AttributesImpl();

        // Copy atts that we are looking for in the correct order.
        for (final String name : order) {
            for (int i = 0; i < atts.getLength(); i++) {
                final String qName = atts.getQName(i);
                if (qName.equals(name)) {
                    newAtts.addAttribute(atts.getURI(i),
                            atts.getLocalName(i), atts.getQName(i),
                            atts.getType(i), atts.getValue(i));
                }
            }
        }

        // Copy other atts.
        for (int i = 0; i < atts.getLength(); i++) {
            boolean found = false;
            for (final String name : order) {
                final String qName = atts.getQName(i);
                if (qName.equals(name)) {
                    found = true;
                }
            }

            if (!found) {
                newAtts.addAttribute(atts.getURI(i), atts.getLocalName(i),
                        atts.getQName(i), atts.getType(i), atts.getValue(i));
            }
        }

        return newAtts;
    }

    @Override
    public void endElement(final String uri, final String localName, final String name)
            throws SAXException {
        outputChars();
        super.endElement(uri, localName, name);
    }

    @Override
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException {
        content.append(ch, start, length);
    }

    private void outputChars() throws SAXException {
        String str = content.toString();
        str = str.replaceAll("\\s+", " ");
        str = str.trim();
        final char[] ch = str.toCharArray();
        super.characters(ch, 0, ch.length);
        content.setLength(0);
    }

    @Override
    public void comment(final char[] ch, final int start, final int length)
            throws SAXException {
        lexicalHandler.comment(ch, start, length);
        final char[] cr = new char[]{'\n'};
        super.characters(cr, 0, cr.length);
    }

    @Override
    public void endCDATA() throws SAXException {
        lexicalHandler.endCDATA();
    }

    @Override
    public void endDTD() throws SAXException {
        lexicalHandler.endDTD();
    }

    @Override
    public void endEntity(final String name) throws SAXException {
        lexicalHandler.endEntity(name);
    }

    @Override
    public void startCDATA() throws SAXException {
        lexicalHandler.startCDATA();
    }

    @Override
    public void startDTD(final String name, final String publicId, final String systemId)
            throws SAXException {
        lexicalHandler.startDTD(name, publicId, systemId);
    }

    @Override
    public void startEntity(final String name) throws SAXException {
        lexicalHandler.startEntity(name);
    }

    public void setLexicalHandler(final LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }
}
