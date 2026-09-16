package event.logging.transformer.schemas;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class TestSafeScheme extends AbstractReleaseSchemaTest {

    public static final String SCHEMA_FILE_PATTERN_STR = "event-logging-.*-safe";

    TestSafeScheme() {
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
                        final ListErrorHandler errorHandler = validateXmlString(
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
                        final ListErrorHandler errorHandler = validateXmlString(
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
        final ListErrorHandler errorHandler = validateXmlString(
                getTemplatedXml("test-02-template.xml", longString),
                false);
        errorHandler.assertErrorsContainString("Name");
        errorHandler.assertErrorsContainString("SafeString");
        errorHandler.assertErrorsContainString(longString);
    }

    @Test
    void test03_fail_unknownElement() {
        final ListErrorHandler errorHandler = validateXmlFile("test-03.xml", false);
        errorHandler.assertErrorsContainString("unknownElement");
        errorHandler.assertErrorsContainString("Invalid content");
        errorHandler.assertErrorsContainString("is expected");
    }

    @Test
    void test04_fail_elementsOutOfOrder() {
        final ListErrorHandler errorHandler = validateXmlFile("test-04.xml", false);
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

        final ListErrorHandler errorHandler = validateXmlString(
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
        final ListErrorHandler errorHandler = validateXmlString(
                getTemplatedXml("test-06-template.xml", sql),
                false);
        errorHandler.assertErrorsContainString("Name");
        errorHandler.assertErrorsContainString("SafeString");
        errorHandler.assertErrorsContainString(sql);
    }

    @Test
    void test07_fail_invalidVersion() {
        final String version = "xyz";
        final ListErrorHandler errorHandler = validateXmlString(
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

        final ListErrorHandler errorHandler = validateXmlString(
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
