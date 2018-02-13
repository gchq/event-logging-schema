package event.logging.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import event.logging.transformer.configuration.Configuration;
import event.logging.transformer.configuration.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchemaGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaGenerator.class);

    // set this value to the version that you want to build the schemas for

    private static final String CONFIG_FILE = "configuration.yml";
    private static final String XSL_SUB_DIR = "transformations";
    private static final String GENERATED_FILES_SUB_DIR = "generated";
    private static final String UNFORMATTED_SUFFIX = "-unformatted.xsd";
    private static final String ID_ATTR_REGEX = "(id=\"event-logging-v[^\"]+)\"";
    private static final SAXParserFactory PARSER_FACTORY;

    static {
        PARSER_FACTORY = SAXParserFactory.newInstance();
        PARSER_FACTORY.setNamespaceAware(true);
    }

    private final Path basePath;
    private final Path sourceSchema;
    private final Configuration configuration;

    public SchemaGenerator(final Path basePath,
                           final Path sourceSchema,
                           final Configuration configuration) {
        this.basePath = basePath;
        this.configuration = configuration;
        this.sourceSchema = sourceSchema;
    }

    public static void main(final String[] args) {

        if (args.length == 2 &&
                args[0].length() > 1 &&
                args[1].length() > 1) {

            String basePathStr = args[0];
            Path basePath = Paths.get(basePathStr).toAbsolutePath().normalize();
            LOGGER.info("Using basePath [{}]", basePath.toString());

            if (!Files.isDirectory(basePath)) {
                LOGGER.info("basePath [{}] is not a valid directory", basePath);
                LOGGER.info("Supplied arguments: {}", Arrays.toString(args));
                displayUsageAndExit();
            }

            String sourceSchemaPathStr = args[1];
            Path sourceSchema = Paths.get(sourceSchemaPathStr).toAbsolutePath().normalize();

            if (!Files.isReadable(basePath)) {
                LOGGER.info("sourceSchema [{}] is not a readable file", sourceSchema);
                LOGGER.info("Supplied arguments: {}", Arrays.toString(args));
                displayUsageAndExit();
            }

            try {
                Configuration configuration = loadConfiguration(basePath);
                new SchemaGenerator(basePath, sourceSchema, configuration).build();
            } catch (SchemaTransformerException ste) {
                LOGGER.error("Error - {}", ste.getMessage());
                System.exit(1);
            } catch (Exception e) {
                LOGGER.error("Error transforming schema", e);
                System.exit(1);
            }

            LOGGER.info("Finished!");
        } else {
            LOGGER.error("ERROR - Invalid arguments");
            LOGGER.info("Supplied arguments: {}", Arrays.toString(args));
            displayUsageAndExit();
        }
    }

    private static void displayUsageAndExit() {
        String jarName = new java.io.File(
                SchemaGenerator.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath()
        ).getName();

        System.out.println();
        System.out.println(String.format("Usage: java -jar %s BASE_PATH SOURCE_SCHEMA_PATH", jarName));
        System.out.println("BASE_PATH - the path where the configuration file 'configuration.yml' lives \n" +
                "            and all generated output will be created");
        System.out.println("SOURCE_SCHEMA_PATH - Path to the source XMLSchema");
        System.out.println("An example configuration file can be found inside this jar file [example.configuration.yml]");
        System.exit(1);
    }

    private static Configuration loadConfiguration(Path basePath) throws IOException {
        Path configFile = basePath.resolve(CONFIG_FILE);
        if (!Files.isReadable(configFile)) {
            throw new SchemaTransformerException(String.format(
                    "Cannot read configuration file %s",
                    configFile.toAbsolutePath().toString()));
        }
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
                .registerModule(new Jdk8Module());
        Configuration configuration = null;
        try {
            configuration = objectMapper.readValue(configFile.toFile(), Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException("Error reading YAML configuration in " +
                    configFile.toAbsolutePath().toString(), e);
        }
        validateConfiguration(basePath, configuration);
        return configuration;
    }

    /**
     * Recursively deletes everything inside dir without deleting dir itself
     */
    static void emptyDirectory(Path dir) throws IOException {
        LOGGER.info("Clearing directory {}", dir.toAbsolutePath().toString());

        try (Stream<Path> pathStream = Files.walk(dir)) {
            pathStream
                    .sorted(Comparator.reverseOrder())
                    .filter(path -> !path.equals(dir))
                    .peek(path -> LOGGER.info("  Deleting {}", path.toAbsolutePath().toString()))
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    private static void validateConfiguration(final Path basePath,
                                              final Configuration configuration) {

        try {
            long distinctPipelineNames = configuration.getPipelines().stream()
                    .map(Pipeline::getName)
                    .distinct()
                    .count();

            long distinctPipelineSuffixes = configuration.getPipelines().stream()
                    .map(Pipeline::getSuffix)
                    .distinct()
                    .count();

            boolean allTransformersExist = configuration.getPipelines().stream()
                    .flatMap(pipeline -> pipeline.getTransformations().stream())
                    .distinct()
                    .map(Paths::get)
                    .allMatch(path -> !Files.isReadable(path));


            if (distinctPipelineNames != configuration.getPipelines().size()) {
                throw new SchemaTransformerException("Duplicate pipeline names in configuration");
            }
            if (distinctPipelineSuffixes != configuration.getPipelines().size()) {
                throw new SchemaTransformerException("Duplicate pipeline suffixes in configuration");
            }
            if (!allTransformersExist) {
                throw new SchemaTransformerException("Transformers defined in configuration do not exist as files");
            }
        } catch (SchemaTransformerException ste) {
            throw ste;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error validating configuration", e);
        }
    }

    private void build() throws IOException {

        LOGGER.info("Using source schema file {}", sourceSchema.toAbsolutePath().toString());

        Path generatedPath = getGeneratedPath();
        if (!Files.exists(generatedPath)) {
            Files.createDirectories(generatedPath);
        }

        emptyDirectory(getGeneratedPath());

        List<Pipeline> effectivePipelines = configuration.getEffectiveOutputPipelines();

        effectivePipelines.forEach(this::buildPipeline);
    }


    private void buildPipeline(final Pipeline pipeline) {


        final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactoryFactory
                .newInstance();

        if (!pipeline.getTransformations().isEmpty()) {
            LOGGER.info("Transforming schema with pipeline {}", pipeline.getName());

            Path xsltsPath = getXsltsPath();

            if (!Files.isDirectory(xsltsPath)) {
                throw new SchemaTransformerException(String.format(
                        "Cannot find transformations directory [%s]. " +
                                "This directory should contain all the XSLT files required by the configured pipelines",
                        xsltsPath.toAbsolutePath().toString()));
            }

            final List<TransformerHandler> handlers = pipeline.getTransformations().stream()
                    .map(xsltsPath::resolve)
                    .peek(path -> {
                        if (!Files.isReadable(path)) {
                            throw new SchemaTransformerException(String.format(
                                    "Cannot read transformation file %s " +
                                            "as configured in %s",
                                    path.toAbsolutePath().toString(),
                                    CONFIG_FILE));
                        }
                    })
                    .map(path -> {
                        TransformerHandler handler;
                        LOGGER.info("  Adding handler for file " + path.getFileName().toString());
                        try {
                            final Templates templates = transformerFactory.newTemplates(new StreamSource(path.toFile()));
                            handler = transformerFactory.newTransformerHandler(templates);
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                        return handler;
                    })
                    .collect(Collectors.toList());

            //build a replacement for the file end of the source schema
            StringBuilder replacement = new StringBuilder()
                    .append("-v")
                    .append(getNamespaceVersion(sourceSchema));

            String suffix = "";
            if (pipeline.getSuffix() != null && pipeline.getSuffix().isPresent()) {
                suffix = "-" + pipeline.getSuffix().get();
                replacement.append(suffix);
            }
            replacement.append(UNFORMATTED_SUFFIX);

            //add the suffix to the output file
            String outputFileName = sourceSchema.getFileName().toString()
                    .replaceAll("\\.xsd$", replacement.toString());

            final Path outputFile = getGeneratedPath().resolve(outputFileName);

            int i = 0;
            for (final TransformerHandler handler : handlers) {
                if (i > 0) {
                    if (i < (handlers.size() - 1)) {
                        // pass the result the next handler in the chain
                        handler.setResult(new SAXResult(handlers.get(i + 1)));

                    } else {
                        // no more handlers in the chain so set the final output
                        handler.setResult(new StreamResult(outputFile.toFile()));
                    }
                }
                i++;
            }

            try {
                if (handlers.size() > 1) {
                    handlers.get(0).getTransformer().transform(
                            new StreamSource(sourceSchema.toFile()),
                            new SAXResult(handlers.get(1)));
                } else {
                    handlers.get(0).getTransformer().transform(
                            new StreamSource(sourceSchema.toFile()),
                            new StreamResult(outputFile.toFile()));
                }
            } catch (TransformerException e) {
                throw new RuntimeException(String.format("Error transforming pipeline %s: %s",
                        pipeline.getName(), e.getMessageAndLocation()), e);
            }

            String formattedFileName = outputFile.getFileName()
                    .toString()
                    .replaceAll(UNFORMATTED_SUFFIX, ".xsd");
            Path formattedFile = getGeneratedPath().resolve(formattedFileName);

            LOGGER.info("Formatting the file");
            formatFile(outputFile, formattedFile, suffix);

            try {
                Files.deleteIfExists(outputFile);
            } catch (IOException e) {
                throw new RuntimeException(String.format("Error deleting un-formatted file %s",
                        outputFile.toAbsolutePath().toString()), e);
            }

            validateSchema(Paths.get(formattedFile.toUri()));
        } else {
            LOGGER.info("Pipeline {} does not have any transformations configured",
                    pipeline.getName());
        }
    }

    private String getNamespaceVersion(final Path schemaPath) {
//        final String linePattern = "targetNamespace=\"event-logging:(.*?)\"";
        final Pattern linePattern = Pattern.compile("targetNamespace=\"event-logging:(?<version>.*?)\"");

        String targetNameSpaceLine;
        try (Stream<String> lines = Files.lines(schemaPath)) {
            targetNameSpaceLine = lines
                    .filter(linePattern.asPredicate())
                    .findFirst()
                    .orElseThrow(() -> new SchemaTransformerException(String.format(
                            "Could not find pattern [%s] in file %s",
                            linePattern,
                            schemaPath.toAbsolutePath().toString())));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error reading file %S",
                    schemaPath.toAbsolutePath().toString()), e);
        }
        Matcher matcher = linePattern.matcher(targetNameSpaceLine);
        String version = null;
        while (matcher.find()) {
            //extract the version group from the match
            version = matcher.group("version");
            break;
        }
        if (version == null) {
            throw new SchemaTransformerException(String.format(
                    "Something has gone wrong, could not find version in line [%s] using pattern[%s]",
                    targetNameSpaceLine,
                    linePattern.toString()));
        }
        return version;
    }

    private void validateSchema(final Path safeSchemaPath) {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            // schemaFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        } catch (final Exception e) {
            throw new RuntimeException("Unable to set Secure Processing feature on schema factory", e);
        }
        // It seems to ignore this property
        // System.setProperty("jdk.xml.maxOccurLimit", "10000");
        LOGGER.info("Validating file " + safeSchemaPath.toAbsolutePath().toString());
        final Instant startTime = Instant.now();
        try {
            // attempt to construct a schema object from the file. Will fail if our schema
            // is not a valid w3c XML Schema. This will ensure the transformation chain
            //generates a valid schema
            schemaFactory.newSchema(safeSchemaPath.toFile());
        } catch (final SAXException e1) {
            throw new RuntimeException("Error initialising schema object", e1);
        }
        LOGGER.info("Finished schema validation in {}",
                Duration.between(startTime, Instant.now()).toString());
    }

    private void formatFile(final Path in, final Path out, String idSuffix) {
        try {
            final SAXTransformerFactory stf = (SAXTransformerFactory) TransformerFactoryFactory.newInstance();
            final TransformerHandler transformerHandler = stf.newTransformerHandler();
            final Transformer serializer = transformerHandler.getTransformer();

            serializer.setOutputProperty(OutputKeys.METHOD, "xml");
            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");

            transformerHandler.setResult(new StreamResult(Files.newOutputStream(out)));

            final SAXParser parser = PARSER_FACTORY.newSAXParser();

            final WhiteSpaceStripper stripper = new WhiteSpaceStripper();
            stripper.setLexicalHandler(transformerHandler);
            stripper.setContentHandler(transformerHandler);

            final XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(stripper);
            xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", stripper);

            // Do some final replacement on some text.
            String xsd = new String(Files.readAllBytes(in));

            // Strip out empty annotations.
            xsd = xsd.replaceAll("<xs:annotation\\s*/>", "");
            if (idSuffix != null && !idSuffix.isEmpty()) {
                Pattern idAttrPattern = Pattern.compile(ID_ATTR_REGEX);
                Matcher matcher = idAttrPattern.matcher(xsd);
                if (matcher.find()) {
                    //add our suffix to the id attribute on the schema element
                    xsd = idAttrPattern.matcher(xsd).replaceFirst("$1" + idSuffix + "\"");
                } else {
                    throw new RuntimeException(String.format("Could not find pattern [%s] in schema %s",
                            ID_ATTR_REGEX, in.toAbsolutePath().toString()));
                }
            }
            xmlReader.parse(new InputSource(new StringReader(xsd)));

        } catch (final Exception e) {
            System.out.println("Error processing file: " + in.toAbsolutePath().toString());
            e.printStackTrace();
        }
    }

    Path getGeneratedPath() {
        return basePath.resolve(GENERATED_FILES_SUB_DIR);
    }

    Path getXsltsPath() {
        return basePath.resolve(XSL_SUB_DIR);
    }
}
