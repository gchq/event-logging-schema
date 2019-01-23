package event.logging.transformer;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TestSchemaGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRealPipelines.class);

    private static final Path SOURCE_SCHEMA = Paths.get("../event-logging.xsd");
    private static final Path TEST_DATA_ROOT_DIR = Paths.get("src/test/resources/test-data");
    private static final Path PIPELINES_RELATIVE_PATH = Paths.get("pipelines");
    private static final Path GENERATED_RELATIVE_PATH = Paths.get("pipelines/generated");
    private static final Path UNCHANGED_FILE = GENERATED_RELATIVE_PATH.resolve("event-logging-v3.xsd");

    private static final String TEST_01 = "test-01";
    private static final List<String> TEST_CASE_NAMES = Arrays.asList(
            TEST_01
    );

    @Before
    public void setup() {

        // Clean out all the generated content
        TEST_CASE_NAMES.forEach(testCaseName -> {
           final Path testCaseDir = getTestCaseDir(testCaseName);
           final Path generatedDir = testCaseDir.resolve(GENERATED_RELATIVE_PATH);

            if (Files.exists(generatedDir)) {
                Assertions.assertThat(generatedDir).isDirectory();
                try {
                    SchemaGenerator.emptyDirectory(generatedDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void test01_versionChange() throws IOException {

        runTest(TEST_01);
    }

    private void runTest(final String name) throws IOException {

        final Path testCaseDir = getTestCaseDir(name);
        final Path pipelinesDir = testCaseDir.resolve(PIPELINES_RELATIVE_PATH);
        final Path generatedDir = testCaseDir.resolve(GENERATED_RELATIVE_PATH);

        SchemaGenerator.main(new String[]{
                pipelinesDir.toString(),
                SOURCE_SCHEMA.toString()});

        // Our test should have generated an unchanged file plus at least one more
        Assertions.assertThat(Files.list(generatedDir).count())
                .isGreaterThanOrEqualTo(2);

        final Path unchangedFile = testCaseDir.resolve(UNCHANGED_FILE);

        Files.list(generatedDir)
                .filter(file -> ! file.getFileName().equals(unchangedFile.getFileName()))
                .forEach(file -> {
                    Assertions.assertThat(file)
                            .isRegularFile();

                    diffFileAgainstSourceSchema(unchangedFile, file);
                });
    }

    private void diffFileAgainstSourceSchema(final Path originalFile, final Path revisedFile) {

        //build simple lists of the lines of the two testfiles
        final List<String> original = getFileLines(originalFile);
        final List<String> revised = getFileLines(revisedFile);

        //compute the patch: this is the diffutils part
        final Patch<String> patch;
        try {
            patch = DiffUtils.diff(original, revised);
        } catch (DiffException e) {
            throw new RuntimeException("Error diffing files");
        }

        LOGGER.info("Comparing {} to {}", originalFile.toString(), revisedFile.toString());
        //simple output the computed patch to console
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            System.out.println(String.format("Original [line %s]", delta.getSource().getPosition()));
            delta.getSource().getLines().forEach(System.out::println);
            System.out.println(String.format("Revised [line %s]", delta.getTarget().getPosition()));
            delta.getTarget().getLines().forEach(System.out::println);
        }
    }

    private List<String> getFileLines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error reading file %s", file));
        }
    }

    private Path getTestCaseDir(final String testCaseName) {
        Objects.requireNonNull(testCaseName);
        return TEST_DATA_ROOT_DIR.resolve(testCaseName);
    }
}
