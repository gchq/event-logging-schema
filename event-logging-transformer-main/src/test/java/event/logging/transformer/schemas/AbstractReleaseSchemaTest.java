package event.logging.transformer.schemas;


import event.logging.transformer.SchemaGenerator;
import event.logging.transformer.XmlUtil;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/// Abstract class for testing schemas produced by {@link SchemaGenerator} that will be released.
abstract class AbstractReleaseSchemaTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReleaseSchemaTest.class);
    private static final Path XML_BASE_PATH = Paths.get("src/test/resources/test-data/release-schemas");

    private static final Path GENERATED_PATH = Paths.get("pipelines/generated");
    private static final MockSystemService MOCK_SYSTEM_SERVICE = new MockSystemService();
    private static final Set<Path> schemaFiles = new HashSet<>();
    protected static final Pattern STRIP_WHITESPACE_PATTERN = Pattern.compile("(\\s\\s+|\n)");

    public static final String EVENT_BLOCK = """
            <Event>
                <EventTime>
                    <TimeCreated>2026-09-04T09:00:00.000Z</TimeCreated>
                </EventTime>
                <EventSource>
                    <System>
                        <Name>Some System</Name>
                        <Environment>Operational</Environment>
                    </System>
                    <Generator>Some-Event-Log-Provider</Generator>
                    <Device>
                        <HostName>someserver.someorg.org</HostName>
                    </Device>
                </EventSource>
                <EventDetail>
                    <TypeId>InteractiveLogon</TypeId>
                    <Description>A user has logged on.</Description>
                    <Authenticate>
                        <Action>Logon</Action>
                        <LogonType>Interactive</LogonType>
                        <User>
                            <Id>jboggs</Id>
                        </User>
                    </Authenticate>
                </EventDetail>
            </Event>""";

    protected final Path schemaFile;
    protected final Validator validator;

    AbstractReleaseSchemaTest() {
        this.schemaFile = getFileByPattern(getSchemaFilePattern());

        assertThat(schemaFile)
                .isRegularFile();

        final Schema schema = XmlUtil.createSchema(schemaFile, true);
        this.validator = schema.newValidator();
        LOGGER.debug("Created validator for schema: {}", schemaFile);
    }

    abstract String getSchemaFilePattern();

    abstract String getSubDirectoryName();

    @BeforeAll
    public static void beforeAll() throws IOException {
        if (Files.exists(GENERATED_PATH)) {
            assertThat(GENERATED_PATH).isDirectory();
            SchemaGenerator.emptyDirectory(GENERATED_PATH);
        }

        // Gen the schemas for ALL tests in this class to use as we
        // only need to gen them once
        TestUtil.runGenerator(
                MOCK_SYSTEM_SERVICE,
                0,
                "pipelines",
                "../event-logging.xsd");

        TestUtil.forEachFile(GENERATED_PATH, schemaFiles::add);

        final String filesStr = schemaFiles.stream()
                .map(Path::getFileName)
                .map(Path::toString)
                .map(str -> "  " + str)
                .sorted()
                .collect(Collectors.joining("\n"));
        LOGGER.debug("Found files:\n{}", filesStr);
    }

    private static Path getFileByPattern(final String regex) {
        Objects.requireNonNull(regex);
        final Pattern pattern = Pattern.compile(regex);
        final Predicate<String> patternPredicate = pattern.asPredicate();
        final List<Path> files = schemaFiles.stream()
                .filter(path -> {
                    final String fileNameStr = path.getFileName().toString();
                    return patternPredicate.test(fileNameStr);
                })
                .toList();

        if (files.isEmpty()) {
            throw new RuntimeException("No file found with pattern: '" + pattern + "'");
        } else if (files.size() > 1) {
            throw new RuntimeException("More than one file found with pattern: '" + pattern + "'");
        } else {
            return files.getFirst();
        }
    }

    protected Path getXmlFilePath(final String fileName) {
        Objects.requireNonNull(fileName);
        return XML_BASE_PATH.resolve(getSubDirectoryName())
                .resolve(fileName);
    }

    protected String getTemplatedXml(final String fileName, final String... replacements) {
        Objects.requireNonNull(fileName);
        Objects.requireNonNull(replacements);
        try {
            final String templatedXml = Files.readString(getXmlFilePath(fileName));
            return MessageFormatter.basicArrayFormat(templatedXml, replacements);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected ListErrorHandler validateXmlFile(final String fileName, final boolean shouldPass) {
        final Path xmlFilePath = getXmlFilePath(fileName);
        assertThat(xmlFilePath)
                .isReadable()
                .isRegularFile();
        final ListErrorHandler errorHandler = validate(new StreamSource(xmlFilePath.toFile()), shouldPass);
        LOGGER.debug("Validation summary for {} - fatal errors: {}, errors: {}, warnings: {}",
                fileName,
                errorHandler.getFatalErrorCount(),
                errorHandler.getErrorCount(),
                errorHandler.getWarningCount());
        return errorHandler;
    }

    protected ListErrorHandler validateXmlString(final String xml, final boolean shouldPass) {
        Objects.requireNonNull(xml);
        final Reader reader = new StringReader(xml);
        LOGGER.debug("Validating XML:\n{}", truncateString(xml, 40));
        final Instant start = Instant.now();
        final ListErrorHandler errorHandler = validate(new StreamSource(reader), shouldPass);
        LOGGER.debug("Validation summary - fatal errors: {}, errors: {}, warnings: {}, duration: {}",
                errorHandler.getFatalErrorCount(),
                errorHandler.getErrorCount(),
                errorHandler.getWarningCount(),
                Duration.between(start, Instant.now()));
        return errorHandler;
    }

    protected ListErrorHandler validate(final StreamSource streamSource, final boolean shouldPass) {
        Objects.requireNonNull(streamSource);
        final ListErrorHandler errorHandler = new ListErrorHandler();
        try {
            validator.setErrorHandler(errorHandler);
            validator.validate(streamSource);
        } catch (SAXException e) {
            errorHandler.handleSaxException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThat(errorHandler.hasAnyErrors())
                .isEqualTo(!shouldPass);
        return errorHandler;
    }

    protected String truncateString(final String str, final int maxLines) {
        Objects.requireNonNull(str);
        final String truncated = str.lines()
                .limit(maxLines)
                .collect(Collectors.joining("\n"));
        if (Objects.equals(str, truncated)) {
            return truncated + "\n...";
        } else {
            return truncated;
        }
    }
}
