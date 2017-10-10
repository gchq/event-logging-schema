package event.logging.transformation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import event.logging.transformation.configuration.Configuration;
import event.logging.transformation.configuration.Pipeline;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SchemaGenerator {

	// set this value to the version that you want to build the schemas for
	private static final String VERSION_BASE_PATH = "xsd/v3.1.1/";

	private static final String CONFIG_FILE = VERSION_BASE_PATH + "configuration.yml";
	private static final String XSL_PATH = VERSION_BASE_PATH + "transformations";
	private static final String SOURCE_SCHEMA = VERSION_BASE_PATH + "source_schema/event-logging-v3.xsd";
    private static final String OUTPUT_PATH = VERSION_BASE_PATH + "generated";
    private static final SAXParserFactory PARSER_FACTORY;

    static {
        PARSER_FACTORY = SAXParserFactory.newInstance();
        PARSER_FACTORY.setNamespaceAware(true);
    }

    public static void main(final String[] args) throws IOException, TransformerException {

		Configuration configuration = loadConfiguration();
		new SchemaGenerator().build(configuration);

		System.out.println("Finished!");
	}

	private static Configuration loadConfiguration() throws IOException {
		Path configFile = Paths.get(CONFIG_FILE);
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		Configuration configuration = objectMapper.readValue(configFile.toFile(), Configuration.class);
		validateConfiguration(configuration);
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

	private static void validateConfiguration(final Configuration configuration) {

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
            throw new RuntimeException("Duplicate pipeline names in configuration");
        }
        if (distinctPipelineSuffixes != configuration.getPipelines().size()) {
            throw new RuntimeException("Duplicate pipeline suffixes in configuration");
        }
        if (!allTransformersExist) {
            throw new RuntimeException("Transformers defined in configuration do not exist as files");
        }
    }

    private void build(final Configuration configuration) throws IOException {

	    emptyDirectory(Paths.get(OUTPUT_PATH));

	    configuration.getPipelines().forEach(this::buildPipeline);
    }

	private void buildPipeline(final Pipeline pipeline) {
//		File dir = new File(".");
//		dir = dir.getCanonicalFile();
//		final File xslDir = new File(dir, XSL_PATH);

        System.out.println();
        System.out.println(String.format("Transforming schema with pipeline %s", pipeline.getName()));

		final SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactoryFactory
				.newInstance();

        final List<TransformerHandler> handlers = pipeline.getTransformations().stream()
                .map(transformer ->
                        Paths.get(XSL_PATH, transformer))
                .peek(path -> {
                    if (!Files.isReadable(path)) {
                        throw new RuntimeException(String.format("Cannot read transformation file %s", path.toAbsolutePath().toString()));
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


		final Path sourceSchema = Paths.get(SOURCE_SCHEMA);

		final String unformatedSuffix = "-unformatted.xsd";
		String replacement = pipeline.getSuffix() == null || pipeline.getSuffix().isEmpty() ?
                unformatedSuffix :
                "-" + pipeline.getSuffix() + unformatedSuffix;
		//add the suffix to the output file
		final Path outputFile = Paths.get(
		        OUTPUT_PATH,
                sourceSchema.getFileName().toString().replaceAll(
                        "\\.xsd$",
                        replacement));

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

        Path formattedFile = Paths.get(
                OUTPUT_PATH,
                outputFile.getFileName()
                        .toString()
                        .replaceAll(unformatedSuffix, ".xsd"));

        System.out.println("Formatting the file");
        formatFile(outputFile, formattedFile);

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
		System.out.println("Finished validation in " + Duration.between(startTime, Instant.now()).toString());
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

}
