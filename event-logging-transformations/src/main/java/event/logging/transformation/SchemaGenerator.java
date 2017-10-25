package event.logging.transformation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import event.logging.transformation.configuration.Configuration;
import event.logging.transformation.configuration.Pipeline;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SchemaGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaGenerator.class);

    // set this value to the version that you want to build the schemas for

    private static final String CONFIG_FILE = "configuration.yml";
    private static final String XSL_SUB_DIR = "transformations";
    private static final String GENERATED_FILES_SUB_DIR = "generated";
    private static final String UNFORMATTED_SUFFIX = "-unformatted.xsd";
    private static final SAXParserFactory PARSER_FACTORY;

    static {
        PARSER_FACTORY = SAXParserFactory.newInstance();
        PARSER_FACTORY.setNamespaceAware(true);
    }

    private final Path basePath;
    private final Configuration configuration;

    public SchemaGenerator(final Path basePath, final Configuration configuration) {
        this.basePath = basePath;
        this.configuration = configuration;
    }

    public static void main(final String[] args) throws IOException, TransformerException {

        if (args.length == 1 && args[0].length() > 1) {
            String basePathStr = args[0];
            Path basePath = Paths.get(basePathStr);
            System.out.println(String.format("Using basePath [%s]", basePath.toAbsolutePath().toString()));

            if (!Files.isDirectory(basePath)) {
                System.out.println(String.format("basePath [%s] is not a valid directory", basePath));
                displayUsageAndExit();
            }

            try {
                Configuration configuration = loadConfiguration(basePath);
                new SchemaGenerator(basePath, configuration).build();
            } catch (IOException e) {
                LOGGER.error("Error transforming schema", e);
                System.exit(1);
            }

            System.out.println("Finished!");
            System.exit(0);
        } else {
            System.out.println("ERROR - Invalid arguments");
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

        System.out.println(String.format("Usage: java -jar %s basePath", jarName));
        System.out.println("Where basePath is the path where the configuration.yml lives \n" +
                "and all output will be created");
        System.exit(1);
    }

    private static Configuration loadConfiguration(Path basePath) throws IOException {
        Path configFile = basePath.resolve(CONFIG_FILE);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
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
    private static void emptyDirectory(Path dir) throws IOException {
        System.out.println(String.format("Clearing directory %s", dir.toAbsolutePath().toString()));
        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .filter(path -> !path.equals(dir))
                .peek(path -> System.out.println(String.format("  Deleting %s", path.toAbsolutePath().toString())))
                .map(Path::toFile)
                .forEach(File::delete);
    }

    private static void validateConfiguration(final Path basePath, final Configuration configuration) {

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


            Path sourceSchemaAbsPath = configuration.getSourceSchemaPath(basePath);

            if (!Files.isReadable(sourceSchemaAbsPath)) {
                throw new RuntimeException(String.format("sourceSchemaPath [%s] is not a readable rile",
                        sourceSchemaAbsPath.toAbsolutePath()));
            }
            if (distinctPipelineNames != configuration.getPipelines().size()) {
                throw new RuntimeException("Duplicate pipeline names in configuration");
            }
            if (distinctPipelineSuffixes != configuration.getPipelines().size()) {
                throw new RuntimeException("Duplicate pipeline suffixes in configuration");
            }
            if (!allTransformersExist) {
                throw new RuntimeException("Transformers defined in configuration do not exist as files");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error validation configuration", e);
        }
    }

    private void build() throws IOException {

        Path generatedPath = getGeneratedPath();
        if (!Files.exists(generatedPath)) {
            Files.createDirectories(generatedPath);
        }

        emptyDirectory(getGeneratedPath());

        configuration.getPipelines().forEach(this::buildPipeline);
    }

    private void buildPipeline(final Pipeline pipeline) {

        System.out.println();
        System.out.println(String.format("Transforming schema with pipeline %s", pipeline.getName()));

        final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactoryFactory
                .newInstance();

        Path xsltsPath = getXsltsPath();
        final List<TransformerHandler> handlers = pipeline.getTransformations().stream()
                .map(xsltsPath::resolve)
                .peek(path -> {
                    if (!Files.isReadable(path)) {
                        throw new RuntimeException(String.format("Cannot read transformation file %s",
                                path.toAbsolutePath().toString()));
                    }
                })
                .map(path -> {
                    TransformerHandler handler;
                    System.out.println("  Adding handler for file " + path.getFileName().toString());
                    try {
                        final Templates templates = transformerFactory.newTemplates(new StreamSource(path.toFile()));
                        handler = transformerFactory.newTransformerHandler(templates);
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                    return handler;
                })
                .collect(Collectors.toList());

        final Path sourceSchema = configuration.getSourceSchemaPath(basePath);

        String replacement = (pipeline.getSuffix() == null || pipeline.getSuffix().isEmpty()) ?
                UNFORMATTED_SUFFIX :
                "-" + pipeline.getSuffix() + UNFORMATTED_SUFFIX;
        //add the suffix to the output file
        String outputFileName = sourceSchema.getFileName().toString().replaceAll( "\\.xsd$", replacement);
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

        System.out.println("Formatting the file");
        formatFile(outputFile, formattedFile);

        try {
            Files.deleteIfExists(outputFile);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error deleting un-formatted file %s",
                    outputFile.toAbsolutePath().toString()), e);
        }

        validateSchema(Paths.get(formattedFile.toUri()));
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
        System.out.println("Validating file " + safeSchemaPath.toAbsolutePath().toString());
        final Instant startTime = Instant.now();
        try {
            // attempt to construct a schema object from the file. Will fail if our schema
            // is not a valid w3c XML Schema. This will ensure the transformation chain
            //generates a valid schema
            schemaFactory.newSchema(safeSchemaPath.toFile());
        } catch (final SAXException e1) {
            throw new RuntimeException("Error initialising schema object", e1);
        }
        System.out.println("Finished schema validation in " + Duration.between(startTime, Instant.now()).toString());
    }

    private void formatFile(final Path in, final Path out) {
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
