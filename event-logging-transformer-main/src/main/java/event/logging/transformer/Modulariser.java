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
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Modulariser {
    private static final Logger LOGGER = LoggerFactory.getLogger(Modulariser.class);

    public void modularise(final Path schema, final Path outputDir) {
        try {
            String string = new String(Files.readAllBytes(schema), StandardCharsets.UTF_8);
            string = string.replace("event-logging:3", "http://event-logging");
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
            final TypeMap typeMap = new TypeMap();
            getTypes(rootElement, typeMap);

            // Flatten extensions.
            replaceExtensions(typeMap, rootElement);

            // Write a version of the schema without extensions.
            writeDoc(doc, outputDir.resolve("__no_extensions.xsd"));

            final Set<SchemaInfo> remaining = typeMap.getValues();

            // Break out types into separate modules.
            boolean complete = false;
            while (!complete) {
                complete = true;
                for (final SchemaInfo schemaInfo : remaining) {
                    final boolean hasEvt = schemaInfo
                            .types.values().stream()
                            .map(TypeInfo::getElement)
                            .anyMatch(this::hasEvt);

                    if (hasEvt) {
                        complete = false;

                    } else {
                        final String typeName = schemaInfo.prefix;
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

                            schemaInfo.types.values().stream().map(TypeInfo::getElement).forEach(element -> {
                                root.appendChild(newDoc.adoptNode(element.cloneNode(true)));
                                element.getParentNode().removeChild(element);
                            });
                            newDoc.appendChild(root);

                            addImports(typeMap, newDoc, root, schemaLocation);
                            writeDoc(newDoc, outputFile);
                            remaining.remove(schemaInfo);

                        } catch (final Exception e) {
                            LOGGER.error("Error writing: " + outputFile.getFileName().toString());
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                }

                // Replace type references.
                replaceTypes(typeMap, rootElement);
            }

            // Add imports.
            final String schemaLocation = "events.xsd";
            final Path outputEventsSchema = outputDir.resolve(schemaLocation);
            addImports(typeMap, doc, rootElement, schemaLocation);

            writeDoc(doc, outputEventsSchema);

            // Validate all schema files.
            try (final Stream<Path> stream = Files.list(outputDir)) {
                stream.forEach(path -> {
                    if (path.getFileName().toString().endsWith(".xsd")) {
                        validateSchema(path);
                    }
                });
            }

            // Find used schemas
            final Set<Path> used = new HashSet<>();
            findUsed(outputEventsSchema, used);

            // Delete unused.
            try (final Stream<Path> stream = Files.list(outputDir)) {
                stream.forEach(child -> {
                    try {
                        if (Files.isRegularFile(child) &&
                                !child.getFileName().toString().startsWith("__") &&
                                !used.contains(child)) {
                            Files.delete(child);
                        }
                    } catch (final IOException e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                });
            }

        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void findUsed(final Path schema, final Set<Path> used) {
        if (used.add(schema)) {
            final Map<String, String> imports = findImports(schema);
            imports.values().forEach(schemaLocation -> {
                final Path child = schema.getParent().resolve(schemaLocation);
                findUsed(child, used);
            });
        }
    }

    private Map<String, String> findImports(final Path schema) {
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            String string = new String(Files.readAllBytes(schema), StandardCharsets.UTF_8);
            final Document doc = builder.parse(new InputSource(new StringReader(string)));
            final Element rootElement = doc.getDocumentElement();
            final Map<String, String> imports = new HashMap<>();
            addImports(rootElement, imports);
            return imports;
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * <xs:import namespace="http://event-logging/version-simple-type" schemaLocation="version-simple-type.xsd"/>
     **/
    private void addImports(final Node parent, final Map<String, String> imports) {
        final NodeList nodeList = parent.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            addImports(node, imports);
            if (node instanceof Element) {
                final Element element = (Element) node;
                if (element.getTagName().equals("xs:import")) {
                    final String namespace = element.getAttribute("namespace");
                    final String schemaLocation = element.getAttribute("schemaLocation");
                    imports.put(namespace, schemaLocation);
                }
            }
        }
    }

    private void getTypes(final Node parent, final TypeMap typeMap) {
        final NodeList nodeList = parent.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            getTypes(node, typeMap);
            if (node instanceof Element) {
                final Element element = (Element) node;
                if (element.getTagName().equals("xs:simpleType") ||
                        element.getTagName().equals("xs:complexType")) {
                    typeMap.put(element);
                }
            }
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

            schemaFactory.newSchema(new StreamSource(inputStream));

        } catch (final SAXException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void addImports(final TypeMap schemas,
                            final Document doc,
                            final Element rootElement,
                            final String currentLocation) {
        rootElement.removeAttribute("xmlns:evt");

        final Set<SchemaInfo> used = new HashSet<>();
        getUsedTypes(schemas, used, rootElement);
        used.stream().sorted(Comparator.reverseOrder()).forEach(schemaInfo -> {
            rootElement.setAttribute("xmlns:" + schemaInfo.prefix, schemaInfo.namespace);
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

    private void replaceTypes(final TypeMap typeMap,
                              final Node node) {
        if (node instanceof Element) {
            final Element element = (Element) node;
            final String type = element.getAttribute("type");
            if (type.startsWith("evt:")) {
                final String typeName = type.substring("evt:".length());
                final SchemaInfo schemaInfo = typeMap.get(typeName);
                if (schemaInfo != null) {
                    final TypeInfo typeInfo = schemaInfo.getType(typeName);
                    if (typeInfo != null) {
                        element.setAttribute("type", schemaInfo.prefix + ":" + typeInfo.name);
                    }
                }
            }
        }

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            replaceTypes(typeMap, nodeList.item(i));
        }
    }

    private void replaceExtensions(final TypeMap typeMap,
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
                        final TypeInfo typeInfo = schemaInfo.getType(typeName);
                        if (typeInfo != null) {
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
                                    final Element typeNode = (Element) typeInfo.element.cloneNode(true);

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

    private void getUsedTypes(final TypeMap typeMap,
                              final Set<SchemaInfo> usedTypes,
                              final Node node) {
        if (node instanceof Element) {
            final Element element = (Element) node;
            final String type = element.getAttribute("type");
            if (!type.isEmpty()) {
                final String[] parts = type.split(":");
                if (parts.length != 2) {
                    throw new RuntimeException("Expected 2 parts: " + type);
                }
                final String typeName = parts[1];
                final SchemaInfo schemaInfo = typeMap.get(typeName);
                if (schemaInfo != null) {
                    final TypeInfo typeInfo = schemaInfo.getType(typeName);
                    if (typeInfo != null) {
                        element.setAttribute("type", schemaInfo.prefix + ":" + typeInfo.name);
                        usedTypes.add(schemaInfo);
                    }
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

    private static String makeDashName(final String string) {
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

    private static String toXml(final Node node) {
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

    private static class TypeMap {

        private static final Map<String, String> GROUP_NAME_REPLACEMENTS = new HashMap<>();

        static {
            GROUP_NAME_REPLACEMENTS.put("GroupsComplexType", "UserComplexType");
            GROUP_NAME_REPLACEMENTS.put("GroupComplexType", "UserComplexType");
            GROUP_NAME_REPLACEMENTS.put("UserComplexType", "UserComplexType");
            GROUP_NAME_REPLACEMENTS.put("AndComplexType", "LogicComplexType");
            GROUP_NAME_REPLACEMENTS.put("OrComplexType", "LogicComplexType");
            GROUP_NAME_REPLACEMENTS.put("NotComplexType", "LogicComplexType");
            GROUP_NAME_REPLACEMENTS.put("AssociationComplexType", "MultiObjectComplexType");
            GROUP_NAME_REPLACEMENTS.put("CriteriaComplexType", "MultiObjectComplexType");
            GROUP_NAME_REPLACEMENTS.put("MultiObjectComplexType", "MultiObjectComplexType");
            GROUP_NAME_REPLACEMENTS.put("SearchResultsComplexType", "MultiObjectComplexType");
        }

        private final Map<String, SchemaInfo> schemaMap = new HashMap<>();

        public void put(final Element element) {
            final String typeName = element.getAttribute("name");
            if (!typeName.isEmpty()) {
                final String groupName = getGroupName(typeName);
                final String dashName = makeDashName(groupName);
                final String schemaLocation = dashName + ".xsd";
                final String targetNamespace = "http://event-logging/" + dashName;
                schemaMap.computeIfAbsent(groupName, k ->
                                new SchemaInfo(schemaLocation, targetNamespace, groupName))
                        .addType(new TypeInfo(typeName, element));
            } else {
                LOGGER.debug("Anonymous type: " + toXml(element));
            }
        }

        public SchemaInfo get(final String name) {
            return schemaMap.get(getGroupName(name));
        }

        public Set<SchemaInfo> getValues() {
            final Set<SchemaInfo> remaining = Collections.newSetFromMap(new ConcurrentHashMap<>());
            remaining.addAll(schemaMap.values());
            return remaining;
        }

        private String getGroupName(final String name) {
            String group = GROUP_NAME_REPLACEMENTS.get(name);
            if (group == null) {
                return name;
            }
            return group;
        }

    }

    private static class SchemaInfo implements Comparable<SchemaInfo> {
        private final String schemaLocation;
        private final String namespace;
        private final String prefix;
        private final Map<String, TypeInfo> types = new HashMap<>();

        public SchemaInfo(final String schemaLocation,
                          final String namespace,
                          final String prefix) {
            this.schemaLocation = schemaLocation;
            this.namespace = namespace;
            this.prefix = prefix;
        }

        public void addType(final TypeInfo typeInfo) {
            if (types.put(typeInfo.name, typeInfo) != null) {
                throw new RuntimeException("Unexpected duplicate: " + typeInfo.name);
            }
        }

        public TypeInfo getType(final String typeName) {
            return types.get(typeName);
        }

        @Override
        public String toString() {
            return prefix;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SchemaInfo that = (SchemaInfo) o;
            return Objects.equals(prefix, that.prefix);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prefix);
        }

        @Override
        public int compareTo(SchemaInfo o) {
            return prefix.compareTo(o.prefix);
        }
    }

    private static class TypeInfo {
        private final String name;
        private final Element element;

        public TypeInfo(final String name,
                        final Element element) {
            this.name = name;
            this.element = element;
        }

        public String getName() {
            return name;
        }

        public Element getElement() {
            return element;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypeInfo typeInfo = (TypeInfo) o;
            return Objects.equals(name, typeInfo.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
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
