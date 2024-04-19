package event.logging.transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Modulariser {
    private static final Logger LOGGER = LoggerFactory.getLogger(Modulariser.class);

    public void modularise(final Path schema, final Path outputDir) {
        try {
            String string = new String(Files.readAllBytes(schema), StandardCharsets.UTF_8);
            string = replaceName(string, "IPAddress", "IpAddress");
            string = replaceName(string, "MACAddress", "MacAddress");
            string = replaceName(string, "ICMPType", "IcmpType");
            string = replaceName(string, "HTTPMethod", "HttpMethod");
            string = replaceName(string, "HTTPVersion", "HttpVersion");
            string = replaceName(string, "URL", "Url");
            string = replaceName(string, "DeviceMACAddress", "DeviceMacAddress");
            string = replaceName(string, "VOIP", "Voip");
            string = string.replace(
                    "[\\d]{4}-[\\d]{2}-[\\d]{2}T[\\d]{2}:[\\d]{2}:[\\d]{2}.[\\d]{3}Z",
                    "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z");
            string = string.replace("(:[0-9a-f]{1,4}){1,1}", "(:[0-9a-f]{1,4})");
            string = string.replace("([0-9a-f]{1,4}:){1,1}", "([0-9a-f]{1,4}:)");


            Files.createDirectories(outputDir);
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            final Document doc = builder.parse(new InputSource(new StringReader(string)));
            final String version = doc.getDocumentElement().getAttribute("version");
            final Element rootElement = doc.getDocumentElement();
            final NodeList nodeList = rootElement.getChildNodes();

            // Write a version of the schema as the basis for comparison.
            writeDoc(doc, outputDir.resolve("__base.xsd"));

            // Remove redundant attributes.
            removeRedundantAttributes(rootElement);
            writeDoc(doc, outputDir.resolve("__cleaned.xsd"));

            // Flatten groups.
            final Map<String, Node> groupMap = new HashMap<>();
            getGroups(groupMap, rootElement);
            replaceGroups(groupMap, rootElement);

            // Write a version of the schema without groups.
            writeDoc(doc, outputDir.resolve("__no_groups.xsd"));

            // Get all types and remember them in a map.
            final Map<String, SchemaInfo> typeMap = new HashMap<>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                if (node instanceof Element) {
                    final Element element = (Element) node;
                    if (element.getTagName().equals("xs:simpleType") ||
                            element.getTagName().equals("xs:complexType")) {
                        final String typeName = element.getAttribute("name");
                        final String dashName = makeDashName(typeName);
                        final String schemaLocation = dashName + ".xsd";
                        final String targetNamespace = "http://event-logging/" + dashName;
                        typeMap.put(typeName,
                                new SchemaInfo(schemaLocation, targetNamespace, typeName, typeName, element));
                    }
                }
            }

            // Flatten extensions.
            replaceExtensions(typeMap, rootElement);

            // Write a version of the schema without extensions.
            writeDoc(doc, outputDir.resolve("__no_extensions.xsd"));

            // Break out types into separate modules.
            boolean complete = false;
            while (!complete) {
                complete = true;
                for (int i = 0; i < nodeList.getLength(); i++) {
                    final Node node = nodeList.item(i);
                    if (node instanceof Element) {
                        final Element element = (Element) node;
                        if (element.getTagName().equals("xs:simpleType") ||
                                element.getTagName().equals("xs:complexType")) {

                            if (hasEvt(node)) {
                                complete = false;

                            } else {
                                final String typeName = element.getAttribute("name");
                                final String dashName = makeDashName(typeName);
                                final String schemaLocation = dashName + ".xsd";
                                final String targetNamespace = "http://event-logging/" + dashName;

                                final Path outputFile = outputDir.resolve(schemaLocation);
                                try {
                                    Document newDoc = builder.newDocument();
                                    Element root = newDoc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:schema");
                                    root.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
                                    root.setAttribute("elementFormDefault", "qualified");
                                    root.setAttribute("id", dashName + "-v" + version);
                                    root.setAttribute("targetNamespace", targetNamespace);
                                    root.setAttribute("version", version);

                                    root.appendChild(newDoc.adoptNode(node.cloneNode(true)));
                                    newDoc.appendChild(root);

                                    addImports(typeMap, newDoc, root, schemaLocation);
                                    writeDoc(newDoc, outputFile);
                                    rootElement.removeChild(node);

                                } catch (final Exception e) {
                                    LOGGER.error("Error writing: " + outputFile.getFileName().toString());
                                    LOGGER.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                }

                // Replace type references.
                replaceTypes(typeMap, rootElement);
            }

            // Add imports.
            final String schemaLocation = "events.xsd";
            addImports(typeMap, doc, rootElement, schemaLocation);

            writeDoc(doc, outputDir.resolve(schemaLocation));

            // Validate all schema files.
            try (final Stream<Path> stream = Files.list(outputDir)) {
                stream.forEach(path -> {
                    if (path.getFileName().toString().endsWith(".xsd")) {
                        validateSchema(path);
                    }
                });
            }

        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void validateSchema(final Path path) {
        LOGGER.info("Validating: " + path.getFileName());
        try (final InputStream inputStream = Files.newInputStream(path)) {
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            if (schemaFactory == null) {
                throw new RuntimeException("Unable to create SchemaFactory for: '" + XMLConstants.W3C_XML_SCHEMA_NS_URI + "'");
            }

            schemaFactory.setErrorHandler(new ErrorHandlerImpl(path.getFileName().toString()));
            schemaFactory.setResourceResolver((type, namespaceURI, publicId, systemId, baseURI) -> {
                try {
                    final Path p = path.getParent().resolve(systemId);
                    if (!Files.isRegularFile(p)) {
                        LOGGER.error("Unable to find path: " + p.getFileName().toString());
                    }
                    return new LSInputImpl(new String(Files.readAllBytes(p), StandardCharsets.UTF_8), systemId, publicId, baseURI);
                } catch (final IOException | RuntimeException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return null;
            });

            Schema schema = schemaFactory.newSchema(new StreamSource(inputStream));

        } catch (final SAXException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void addImports(final Map<String, SchemaInfo> schemas,
                            final Document doc,
                            final Element rootElement,
                            final String currentLocation) {
        rootElement.removeAttribute("xmlns:evt");

        final Map<String, SchemaInfo> used = new HashMap<>();
        getUsedTypes(schemas, used, rootElement);
        used.keySet().stream().sorted(Comparator.reverseOrder()).forEach(type -> {
            final SchemaInfo schemaInfo = used.get(type);
            rootElement.setAttribute("xmlns:" + type, schemaInfo.namespace);
            if (!schemaInfo.schemaLocation.equals(currentLocation)) {
                Element imp = doc.createElementNS(
                        "http://www.w3.org/2001/XMLSchema",
                        "xs:import");
                imp.setAttribute("schemaLocation", schemaInfo.schemaLocation);
                imp.setAttribute("namespace", schemaInfo.namespace);

                final Node first = rootElement.getFirstChild();
                rootElement.insertBefore(imp, first);
            }
        });
    }

    private void replaceTypes(final Map<String, SchemaInfo> typeMap,
                              final Node node) {
        if (node instanceof Element) {
            final Element element = (Element) node;
            final String type = element.getAttribute("type");
            if (type.startsWith("evt:")) {
                final String typeName = type.substring("evt:".length());
                final SchemaInfo schemaInfo = typeMap.get(typeName);
                if (schemaInfo != null) {
                    element.setAttribute("type", schemaInfo.prefix + ":" + schemaInfo.name);
                }
            }
        }

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            replaceTypes(typeMap, nodeList.item(i));
        }
    }

    private void replaceExtensions(final Map<String, SchemaInfo> typeMap,
                                   final Node node) {

        // Flatten extensions.
        if (node instanceof Element) {
            final Element extensionElement = (Element) node;
            if (extensionElement.getTagName().equals("xs:extension")) {
                final String base = extensionElement.getAttribute("base");
                if (base.startsWith("evt:")) {
                    final String typeName = base.substring("evt:".length());
                    final SchemaInfo schemaInfo = typeMap.get(typeName);
                    if (schemaInfo != null) {
                        final Node complexContentNode = extensionElement.getParentNode();
                        if (complexContentNode instanceof Element) {
                            final Element complexContentElement = (Element) complexContentNode;
                            if (!complexContentElement.getTagName().equals("xs:complexContent")) {
                                throw new RuntimeException("Expected complexContent: " + complexContentElement.getTagName());
                            }

                            final Node parentNode = complexContentElement.getParentNode();
                            if (parentNode instanceof Element) {
                                final Element parentElement = (Element) parentNode;

                                // Get the type that we are going to replace the extension with.
                                final Element typeNode = (Element) schemaInfo.element.cloneNode(true);

                                // Append extensions to type.
                                copyChildrenToDest(extensionElement, typeNode);

                                // Add the type info to the parent element.
                                final NodeList nodeList = typeNode.getChildNodes();
                                for (int i = 0; i < nodeList.getLength(); i++) {
                                    final Node child = nodeList.item(i);
                                    parentElement.insertBefore(child.cloneNode(true), complexContentElement);
                                }

                                // Remove duplicate annotations.
                                removeDuplicateAnnotations(parentElement);

                                complexContentElement.removeChild(extensionElement);
                                for (int i = 0; i < complexContentElement.getChildNodes().getLength(); i++) {
                                    if (complexContentElement.getChildNodes().item(i) instanceof Element) {
                                        throw new RuntimeException("Unexpected child element: " + complexContentElement);
                                    }
                                }

                                parentElement.removeChild(complexContentElement);

                                // Collapse sequences.
                                collapseSequences(parentElement);

                            } else {
                                throw new RuntimeException("Expected element: " + parentNode);
                            }
                        } else {
                            throw new RuntimeException("Expected element: " + complexContentNode);
                        }
                    }
                }
            }
        }

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            replaceExtensions(typeMap, nodeList.item(i));
        }
    }

    private void removeDuplicateAnnotations(final Node parentElement) {
        // Remove duplicate annotations.
        int count = 0;
        NodeList nodeList = parentElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node child = nodeList.item(i);
            if (child instanceof Element) {
                final Element el = (Element) child;
                if (el.getTagName().equals("xs:annotation")) {
                    count++;
                    if (count > 1) {
                        parentElement.removeChild(el);
                    }
                }
            }
        }
    }

    private void copyChildrenToDest(final Node src, final Node dest) {
        for (int i = 0; i < src.getChildNodes().getLength(); i++) {
            dest.appendChild(src.getChildNodes().item(i).cloneNode(true));
        }
    }

    private void getUsedTypes(final Map<String, SchemaInfo> typeMap,
                              final Map<String, SchemaInfo> usedTypes,
                              final Node node) {
        if (node instanceof Element) {
            final Element element = (Element) node;
            final String type = element.getAttribute("type");
            if (!type.isEmpty()) {
                final String[] parts = type.split(":");
                if (parts.length != 2) {
                    throw new RuntimeException("Expected 2 parts: " + type);
                }
                final SchemaInfo schemaInfo = typeMap.get(parts[1]);
                if (schemaInfo != null) {
                    element.setAttribute("type", schemaInfo.prefix + ":" + schemaInfo.name);
                    usedTypes.put(parts[1], schemaInfo);
                }
            }
        }

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            getUsedTypes(typeMap, usedTypes, nodeList.item(i));
        }
    }

    private void collapseSequences(final Node node) {
        if (node instanceof Element) {
            final NodeList nodeList = node.getChildNodes();
            Element currentSequence = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node child = nodeList.item(i);
                if (child instanceof Element) {
                    Element element = (Element) child;
                    if (element.getTagName().equals("xs:sequence") ||
                            element.getTagName().equals("xs:choice") ||
                            element.getTagName().equals("xs:group") ||
                            element.getTagName().equals("xs:any")) {
                        if (currentSequence == null) {
                            currentSequence = element;
                        } else if (!currentSequence.getTagName().equals(element.getTagName())) {
                            throw new RuntimeException("Unexpected change of type");
                        } else {
                            String minOccurs = currentSequence.getAttribute("minOccurs");
                            if (minOccurs.isEmpty()) {
                                minOccurs = "1";
                            }
                            String maxOccurs = currentSequence.getAttribute("minOccurs");
                            if (maxOccurs.isEmpty()) {
                                maxOccurs = "1";
                            }

                            String newMinOccurs = element.getAttribute("minOccurs");
                            if (newMinOccurs.isEmpty()) {
                                newMinOccurs = "1";
                            }
                            String newMaxOccurs = element.getAttribute("minOccurs");
                            if (newMaxOccurs.isEmpty()) {
                                newMaxOccurs = "1";
                            }

                            if (!minOccurs.equals(newMinOccurs)) {
                                throw new RuntimeException("Unexpected change of newMinOccurs");
                            }
                            if (!maxOccurs.equals(newMaxOccurs)) {
                                throw new RuntimeException("Unexpected change of newMaxOccurs");
                            }

                            copyChildrenToDest(element, currentSequence);
                            node.removeChild(element);
                        }
                    }
                }
            }
        }

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            collapseSequences(nodeList.item(i));
        }
    }

    private boolean hasEvt(final Node node) {
        if (node instanceof Element) {
            final Element element = (Element) node;
            final String type = element.getAttribute("type");
            if (type.startsWith("evt:")) {
                return true;
            }
        }

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (hasEvt(nodeList.item(i))) {
                return true;
            }
        }

        return false;
    }

    private void removeRedundantAttributes(final Node node) {
        if (node instanceof Element) {
            final Element element = (Element) node;
            final String minOccurs = element.getAttribute("minOccurs");
            if (minOccurs.equals("1")) {
                element.removeAttribute("minOccurs");
            }
            final String maxOccurs = element.getAttribute("maxOccurs");
            if (maxOccurs.equals("1")) {
                element.removeAttribute("maxOccurs");
            }
            final String attributeFormDefault = element.getAttribute("attributeFormDefault");
            if (attributeFormDefault.equals("unqualified")) {
                element.removeAttribute("attributeFormDefault");
            }
            final String use = element.getAttribute("use");
            if (use.equals("optional")) {
                element.removeAttribute("use");
            }
        }

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            removeRedundantAttributes(nodeList.item(i));
        }
    }

    private void getGroups(final Map<String, Node> groups, final Node node) {
        if (node instanceof Element) {
            final Element element = (Element) node;
            if (element.getTagName().equals("xs:group")) {
                final String name = element.getAttribute("name");
                final String ref = element.getAttribute("ref");
                if (ref.isEmpty() && !name.isEmpty()) {
                    groups.put(name, node);
                    node.getParentNode().removeChild(node);
                }
            }
        }

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            getGroups(groups, nodeList.item(i));
        }
    }

    private void replaceGroups(final Map<String, Node> groups, final Node node) {
        if (node instanceof Element) {
            final Element element = (Element) node;
            if (element.getTagName().equals("xs:group")) {
                final String name = element.getAttribute("name");
                final String ref = element.getAttribute("ref");
                if (!ref.isEmpty() && name.isEmpty()) {
                    if (ref.startsWith("evt:")) {
                        final String groupName = ref.substring("evt:".length());
                        final Node group = groups.get(groupName);
                        if (group == null) {
                            throw new RuntimeException("Unable to find group: " + groupName);
                        }

                        final NodeList groupChildren = group.getChildNodes();
                        for (int i = 0; i < groupChildren.getLength(); i++) {
                            final Node child = groupChildren.item(i).cloneNode(true);

                            // Copy group attributes onto element.
                            if (child instanceof Element) {
                                final Element childElement = (Element) child;
                                for (int j = 0; j < element.getAttributes().getLength(); j++) {
                                    final Node att = element.getAttributes().item(j);
                                    if (att instanceof Attr) {
                                        final Attr attr = (Attr) att;
                                        if (!attr.getName().equals("ref") && !attr.getName().equals("name")) {
                                            childElement.setAttribute(attr.getName(), attr.getValue());
                                        }
                                    }
                                }
                            }

                            element.getParentNode().insertBefore(child, element);
                        }

                        node.getParentNode().removeChild(node);
                    }
                }
            }
        }

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            replaceGroups(groups, nodeList.item(i));
        }
    }

    private void writeDoc(final Document document, final Path path) throws Exception {
        DOMSource dom = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        if (Files.isRegularFile(path)) {
            throw new RuntimeException("File exists: " + path);
        }

        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            final StreamResult result = new StreamResult(outputStream);
            transformer.transform(dom, result);
            outputStream.flush();
            String xml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            String newXml = xml + " ";
            while (!newXml.equals(xml)) {
                xml = newXml;
                newXml = xml.replaceAll("\n[ \t]*\n", "\n");
            }
            Files.write(path, xml.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String makeDashName(final String string) {
        String newName = string.replaceAll("([A-Z])", "-$1");
        newName = newName.toLowerCase(Locale.ROOT);
        newName = newName.replaceAll("^-", "");
        return newName;
    }

    private String replaceName(final String string, final String regex, final String replacement) {
        return string
                .replaceAll("name=\"" + regex, "name=\"" + replacement)
                .replaceAll("type=\"evt:" + regex, "type=\"evt:" + replacement);
    }

    private void debug(final Node node) {
        System.out.println("DEBUG");
        System.out.println(toXml(node));
    }

    private String toXml(final Node node) {
        try {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            StringWriter buffer = new StringWriter();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(node),
                    new StreamResult(buffer));
            return buffer.toString();
        } catch (final TransformerException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "";
    }

    private static class SchemaInfo {
        private final String schemaLocation;
        private final String namespace;
        private final String prefix;
        private final String name;
        private final Element element;

        public SchemaInfo(final String schemaLocation,
                          final String namespace,
                          final String prefix,
                          final String name,
                          final Element element) {
            this.schemaLocation = schemaLocation;
            this.namespace = namespace;
            this.prefix = prefix;
            this.name = name;
            this.element = element;
        }
    }

    private static class ErrorHandlerImpl implements ErrorHandler {
        private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlerImpl.class);

        private final String file;

        public ErrorHandlerImpl(String file) {
            this.file = file;
        }

        @Override
        public void warning(SAXParseException exception) {
            LOGGER.error(file + ": " + exception.getMessage(), exception);
        }

        @Override
        public void error(SAXParseException exception) {
            LOGGER.error(file + ": " + exception.getMessage(), exception);
        }

        @Override
        public void fatalError(SAXParseException exception) {
            LOGGER.error(file + ": " + exception.getMessage(), exception);
        }
    }
}
