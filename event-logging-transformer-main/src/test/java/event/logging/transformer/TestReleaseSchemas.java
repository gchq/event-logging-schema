package event.logging.transformer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/// Performs tests against the generated schemas that will actually be released
public class TestReleaseSchemas {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestReleaseSchemas.class);
    private static final Path GENERATED_PATH = Paths.get("pipelines/generated");
    private static final MockSystemService systemService = new MockSystemService();
    private static final Set<Path> schemaFiles = new HashSet<>();
    private static final Pattern STRIP_WHITESPACE_PATTERN = Pattern.compile("(\\s\\s+|\n)");
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

    @BeforeAll
    public static void beforeAll() throws IOException {
        if (Files.exists(GENERATED_PATH)) {
            assertThat(GENERATED_PATH).isDirectory();
            SchemaGenerator.emptyDirectory(GENERATED_PATH);
        }

        // Gen the schemas for ALL tests in this class to use as we
        // only need to gen them once
        TestUtil.runGenerator(
                systemService,
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

    // --------------------------------------------------------------------------------

    static abstract class AbstractSchemaTest {
        private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSchemaTest.class);
        private static final Path XML_BASE_PATH = Paths.get("src/test/resources/test-data/release-schemas");

        protected final Path schemaFile;
        protected final Validator validator;

        AbstractSchemaTest(final String schemaFilePattern) {
            this.schemaFile = getFileByPattern(schemaFilePattern);

            assertThat(schemaFile)
                    .isRegularFile();

            final Schema schema = XmlUtil.createSchema(schemaFile, true);
            this.validator = schema.newValidator();
            LOGGER.debug("Created validator for schema: {}", schemaFile);
        }

        abstract String getSubDirectoryName();

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

        protected MyErrorHandler validateXmlFile(final String fileName, final boolean shouldPass) {
            final Path xmlFilePath = getXmlFilePath(fileName);
            assertThat(xmlFilePath)
                    .isReadable()
                    .isRegularFile();
            final MyErrorHandler errorHandler = validate(new StreamSource(xmlFilePath.toFile()), shouldPass);
            LOGGER.debug("Validation summary for {} - fatal errors: {}, errors: {}, warnings: {}",
                    fileName,
                    errorHandler.getFatalErrorCount(),
                    errorHandler.getErrorCount(),
                    errorHandler.getWarningCount());
            return errorHandler;
        }

        protected MyErrorHandler validateXmlString(final String xml, final boolean shouldPass) {
            Objects.requireNonNull(xml);
            final Reader reader = new StringReader(xml);
            LOGGER.debug("Validating XML:\n{}", truncateString(xml, 40));
            final Instant start = Instant.now();
            final MyErrorHandler errorHandler = validate(new StreamSource(reader), shouldPass);
            LOGGER.debug("Validation summary - fatal errors: {}, errors: {}, warnings: {}, duration: {}",
                    errorHandler.getFatalErrorCount(),
                    errorHandler.getErrorCount(),
                    errorHandler.getWarningCount(),
                    Duration.between(start, Instant.now()));
            return errorHandler;
        }

        protected MyErrorHandler validate(final StreamSource streamSource, final boolean shouldPass) {
            Objects.requireNonNull(streamSource);
            final MyErrorHandler errorHandler = new MyErrorHandler();
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


    // --------------------------------------------------------------------------------


    @Nested
    class SafeSchemeTests extends AbstractSchemaTest {

        public static final String SCHEMA_FILE_PATTERN_STR = "event-logging-.*-safe";

        SafeSchemeTests() {
            super(SCHEMA_FILE_PATTERN_STR);

        }

        @Test
        void test01() {
            validateXmlFile("test-01.xml", true);
        }

        @TestFactory
        Stream<DynamicTest> test02_pass() {
            return Stream.of(
                            "abcdefghijklmnopqrstuvwxyz",
                            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                            "0123456789",
                            "-",
                            "_",
                            "/",
                            " ",
                            ":",
                            ".",
                            "~123~456~789" // These are replacements for unsupported chars
                    )
                    .map(replacement ->
                            DynamicTest.dynamicTest("str: '" + replacement + "'", () -> {
                                validateXmlString(
                                        getTemplatedXml("test-02-template.xml", replacement),
                                        true);
                            }));
        }

        /// Tests chars that will be rejected by SafeString
        @TestFactory
        Stream<DynamicTest> test02_fail_unSupportedChars() {
            return Stream.of(
                            ";",
                            "{",
                            "}",
                            "[",
                            "]",
                            "\\",
                            "\n",
                            "\t",
                            "â",
                            "ê",
                            "î",
                            "ô",
                            "û",
                            "👍" // an emoji
                    )
                    .map(replacement -> {
                        String strName = replacement;
                        if (replacement.codePoints().count() == 1) {
                            final int codePoint = replacement.codePointAt(0);
                            strName = Character.getName(codePoint);
                        }
                        return DynamicTest.dynamicTest("str: '" + strName + "'", () -> {
                            final MyErrorHandler errorHandler = validateXmlString(
                                    getTemplatedXml("test-02-template.xml", replacement),
                                    false);

                            errorHandler.assertErrorsContainString("Name");
                            errorHandler.assertErrorsContainString("SafeString");
                            errorHandler.assertErrorsContainString(replacement);
                        });
                    });
        }

        /// Tests chars that will be rejected by the XML parser as not being valid in XML
        @TestFactory
        Stream<DynamicTest> test02_fail_xmlUnSupportedChars() {
            // Test all the ascii control characters
            return IntStream.rangeClosed(0, 31)
                    // 9/10/13 are allowed in XML but not by SafeString
                    .filter(codePoint -> codePoint != 9
                            && codePoint != 10
                            && codePoint != 13)
                    .boxed()
                    .map(Character::toChars)
                    .map(String::new)
                    .map(replacement -> {

                        Assertions.assertThat(replacement.codePoints().count())
                                .isEqualTo(1);
                        final int codePoint = replacement.codePointAt(0);
                        final String strName = Character.getName(codePoint);

                        return DynamicTest.dynamicTest("str: '" + strName + "'", () -> {
                            final MyErrorHandler errorHandler = validateXmlString(
                                    getTemplatedXml("test-02-template.xml", replacement),
                                    false);

                            // Example exception msg:
                            // An invalid XML character (Unicode: 0x0) was found in the element content of the document.
                            errorHandler.assertErrorsContainString("An invalid XML character");
                            errorHandler.assertErrorsContainString(Integer.toHexString(codePoint));
                        });
                    });
        }

        /// Tests the longest string that SafeString can handle
        @Test
        void test02_pass_longString() {
            final String longString = "a".repeat(500);
            validateXmlString(
                    getTemplatedXml("test-02-template.xml", longString),
                    true);
        }

        /// Tests the longest string that SafeString can handle
        @Test
        void test02_pass_longStringTruncated() {
            // ... is allowed on top of the max 500 chars
            final String longString = "a".repeat(500) + "...";
            validateXmlString(
                    getTemplatedXml("test-02-template.xml", longString),
                    true);
        }

        /// Tests strings that are too long for SafeString
        @Test
        void test02_fail_longString() {
            final String longString = "a".repeat(501);
            final MyErrorHandler errorHandler = validateXmlString(
                    getTemplatedXml("test-02-template.xml", longString),
                    false);
            errorHandler.assertErrorsContainString("Name");
            errorHandler.assertErrorsContainString("SafeString");
            errorHandler.assertErrorsContainString(longString);
        }

        @Test
        void test03_fail_unknownElement() {
            final MyErrorHandler errorHandler = validateXmlFile("test-03.xml", false);
            errorHandler.assertErrorsContainString("unknownElement");
            errorHandler.assertErrorsContainString("Invalid content");
            errorHandler.assertErrorsContainString("is expected");
        }

        @Test
        void test04_fail_elementsOutOfOrder() {
            final MyErrorHandler errorHandler = validateXmlFile("test-04.xml", false);
            errorHandler.assertErrorsContainString("SessionId");
            errorHandler.assertErrorsContainString("Invalid content");
            errorHandler.assertErrorsContainString("is expected");
        }

        @Test
        void test05_fail_tooManyEvents() {
            // Strip all the whitespace so we don't use a tonne of memory
            final String eventLine = STRIP_WHITESPACE_PATTERN.matcher(EVENT_BLOCK)
                    .replaceAll("");
            // Make a document with 100,001 events in it, which is one too many.
            final String events = IntStream.rangeClosed(1, 100_001)
                    .boxed()
                    .map(i -> eventLine)
                    .collect(Collectors.joining("\n"));

            final MyErrorHandler errorHandler = validateXmlString(
                    getTemplatedXml("test-05-template.xml", events),
                    false);
            errorHandler.assertErrorsContainString("Event");
            errorHandler.assertErrorsContainString("a maximum of");
            errorHandler.assertErrorsContainString("100000");
        }

        @Test
        void test05_pass_tooManyEvents() {
            final String eventLine = STRIP_WHITESPACE_PATTERN.matcher(EVENT_BLOCK)
                    .replaceAll("");
            // Make a document with 100,000 events in it, which is allowed.
            final String events = IntStream.rangeClosed(1, 100_000)
                    .boxed()
                    .map(i -> eventLine)
                    .collect(Collectors.joining("\n"));

            validateXmlString(getTemplatedXml("test-05-template.xml", events), true);
        }

        @Test
        void test06_fail_sql_injection() {
            final String sql = "delete from my_table;";
            final MyErrorHandler errorHandler = validateXmlString(
                    getTemplatedXml("test-06-template.xml", sql),
                    false);
            errorHandler.assertErrorsContainString("Name");
            errorHandler.assertErrorsContainString("SafeString");
            errorHandler.assertErrorsContainString(sql);
        }

        @Test
        void test07_fail_invalidVersion() {
            final String version = "xyz";
            final MyErrorHandler errorHandler = validateXmlString(
                    getTemplatedXml("test-07-template.xml", version),
                    false);
            errorHandler.assertErrorsContainString("'Version' on element 'Events'");
            errorHandler.assertErrorsContainString("VersionSimpleType");
            errorHandler.assertErrorsContainString(version);
        }

        @Test
        void test08_pass_manyDataElements() {
            // Strip all the whitespace so we don't use a tonne of memory
            final String dataElmTemplate = """
                    <Data Name="{}" Value="{}"></Data>""";
            // Make a document with 500 data elements in it, which is fine
            final String dataElms = IntStream.rangeClosed(1, 500)
                    .boxed()
                    .map(i -> MessageFormatter.basicArrayFormat(
                            dataElmTemplate,
                            new String[]{"data-" + i, "value-" + i}))
                    .collect(Collectors.joining("\n"));

            validateXmlString(
                    getTemplatedXml("test-08-template.xml", "myData", dataElms),
                    true);
        }

        @Test
        void test08_fail_tooManyDataElements() {
            // Strip all the whitespace so we don't use a tonne of memory
            final String dataElmTemplate = """
                    <Data Name="{}" Value="{}"></Data>""";
            // Make a document with 501 data elements in it, which is fine
            final String dataElms = IntStream.rangeClosed(1, 501)
                    .boxed()
                    .map(i -> MessageFormatter.basicArrayFormat(
                            dataElmTemplate,
                            new String[]{"data-" + i, "value-" + i}))
                    .collect(Collectors.joining("\n"));

            final MyErrorHandler errorHandler = validateXmlString(
                    getTemplatedXml("test-08-template.xml", "myData", dataElms),
                    false);
            errorHandler.assertErrorsContainString("Data");
            errorHandler.assertErrorsContainString("a maximum of");
            errorHandler.assertErrorsContainString("500");
        }

        @Override
        String getSubDirectoryName() {
            return "safe";
        }
    }


    // --------------------------------------------------------------------------------


    static class MyErrorHandler implements ErrorHandler {

        private final List<String> warnings = new ArrayList<>();
        private final List<String> errors = new ArrayList<>();
        private final List<String> fatalErrors = new ArrayList<>();

        public void handleSaxException(final SAXException exception) {
            if (exception != null) {
                final String message = exception.getMessage();
                LOGGER.debug("SAX Exception - {}", message);
                errors.add(message);
            }
        }

        @Override
        public void warning(final SAXParseException exception) {
            if (exception != null) {
                final String message = exception.getMessage();
                LOGGER.debug("SAX WARNING - {}", message);
                warnings.add(message);
            }
        }

        @Override
        public void error(final SAXParseException exception) {
            if (exception != null) {
                final String message = exception.getMessage();
                LOGGER.debug("SAX ERROR - {}", message);
                errors.add(exception.getMessage());
            }
        }

        @Override
        public void fatalError(final SAXParseException exception) {
            if (exception != null) {
                final String message = exception.getMessage();
                LOGGER.debug("SAX FATAL - {}", message);
                fatalErrors.add(exception.getMessage());
            }
        }

        int getWarningCount() {
            return warnings.size();
        }

        int getErrorCount() {
            return errors.size();
        }

        int getFatalErrorCount() {
            return fatalErrors.size();
        }

        boolean hasAnyErrors() {
            return !errors.isEmpty() || !fatalErrors.isEmpty();
        }

        boolean errorsContainString(final String string) {
            Objects.requireNonNull(string);
            final String lowerStr = string.toLowerCase();
            return errors.stream()
                    .map(String::toLowerCase)
                    .anyMatch(error -> error.contains(lowerStr));
        }

        void assertErrorsContainString(final String string) {
            Objects.requireNonNull(string);
            final String lowerStr = string.toLowerCase();
            assertThat(errors.stream()
                    .map(String::toLowerCase)
                    .anyMatch(error -> error.contains(lowerStr)))
                    .withFailMessage("Expected errors to contain string '%s'", string)
                    .isTrue();
        }
    }
}
