package event.logging.transformer.schemas;


import org.junit.jupiter.api.Test;

class TestClientSchema extends AbstractReleaseSchemaTest {

    public static final String SUB_DIR_NAME = "client";
    public static final String SCHEMA_FILE_PATTERN_STR = "event-logging-.*-client";

    @Test
    void test01() {
        validateXmlFile("test-01.xml", true);
    }

    @Override
    String getSchemaFilePattern() {
        return SCHEMA_FILE_PATTERN_STR;
    }

    @Override
    String getSubDirectoryName() {
        return SUB_DIR_NAME;
    }
}
