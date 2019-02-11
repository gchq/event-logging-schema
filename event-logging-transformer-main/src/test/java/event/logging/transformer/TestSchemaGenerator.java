package event.logging.transformer;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSchemaGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRealPipelines.class);

    private static final Path SOURCE_SCHEMA = Paths.get("../event-logging.xsd");
    private static final Path TEST_DATA_ROOT_DIR = Paths.get("src/test/resources/test-data");
    private static final Path PIPELINES_RELATIVE_PATH = Paths.get("pipelines");
    private static final Path GENERATED_RELATIVE_PATH = Paths.get("pipelines/generated");
    private static final Path UNCHANGED_FILE = GENERATED_RELATIVE_PATH.resolve("event-logging-v3-unchanged.xsd");

    private static final String TEST_01 = "test-01";
    private static final String TEST_02 = "test-02";
    private static final String TEST_03 = "test-03";
    private static final List<String> TEST_CASE_NAMES = Arrays.asList(
            TEST_01,
            TEST_02,
            TEST_03
    );

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Before
    public void setup() {

        // Clean out all the generated content
        TEST_CASE_NAMES.forEach(testCaseName -> {
           final Path testCaseDir = getTestCaseDir(testCaseName);
           final Path generatedDir = testCaseDir.resolve(GENERATED_RELATIVE_PATH);

            if (Files.exists(generatedDir)) {
                assertThat(generatedDir).isDirectory();
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

        exit.expectSystemExitWithStatus(0);
        final List<Path> generatedFiles = runTest(TEST_01);

        assertThat(generatedFiles)
                .hasSize(2);

        String majorVersion = "v7";
        String fullVersion = majorVersion + ".8.9";
        String baseName = "my-new-schema";
        String suffix1 = "-variantOne";
        String suffix2 = "-variantTwo";
        String expectedFile1 = baseName + "-" + majorVersion + suffix1 + ".xsd";
        String expectedFile2 = baseName + "-" + majorVersion + suffix2 + ".xsd";
        String expectedId1 = baseName + "-" + fullVersion + suffix1;
        String expectedId2 = baseName + "-" + fullVersion + suffix2;

        assertGeneratedFilenames(generatedFiles, expectedFile1, expectedFile2);

        assertThat(getFileText(generatedFiles.get(0)))
                .contains(expectedId1);
        assertThat(getFileText(generatedFiles.get(1)))
                .contains(expectedId2);
    }

    @Test
    public void test02_noChanges() throws IOException {

        exit.expectSystemExitWithStatus(0);
        List<Path> generatedFiles = runTest(TEST_02);

        assertThat(generatedFiles).hasSize(1);

        assertThat(generatedFiles.get(0).getFileName().toString())
                .matches("event-logging-v([0-9]|SNAPSHOT).xsd");
    }

    @Test
    public void test03_addRegexSimpleType() throws IOException {

        exit.expectSystemExitWithStatus(0);
        List<Path> generatedFiles = runTest(TEST_03);

        assertThat(generatedFiles).hasSize(1);

        assertThat(generatedFiles.get(0).getFileName().toString())
                .matches("event-logging-v([0-9]|SNAPSHOT).xsd");

        // make sure the regex is correct in the generated file
        assertThat(getFileText(generatedFiles.get(1)))
                .contains("[0-9]{91,99}[A-Z]{3}$");
    }

    private void assertGeneratedFilenames(final List<Path> generatedFiles, final String... expectedFileNames) {
        assertThat(generatedFiles.stream()
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList())
        ).containsExactlyInAnyOrder(expectedFileNames);
    }

    private List<Path> runTest(final String name) throws IOException {

        final Path testCaseDir = getTestCaseDir(name);
        final Path pipelinesDir = testCaseDir.resolve(PIPELINES_RELATIVE_PATH);
        final Path generatedDir = testCaseDir.resolve(GENERATED_RELATIVE_PATH);

        SchemaGenerator.main(new String[]{
                pipelinesDir.toString(),
                SOURCE_SCHEMA.toString()});

        // Our test should have generated an unchanged file plus at least one more
        assertThat(Files.list(generatedDir).count())
                .isGreaterThanOrEqualTo(2);

        final Path unchangedFile = Files.list(testCaseDir.resolve(GENERATED_RELATIVE_PATH))
                .peek(System.out::println)
                .filter(path ->
                        path.toString().endsWith("unchanged.xsd"))
                .findFirst()
                .get();

        // collect the paths of all generated files bar the special unchanged one
        List<Path> generatedFiles = Files.list(generatedDir)
                .filter(file -> ! file.getFileName().equals(unchangedFile.getFileName()))
                .sorted()
                .collect(Collectors.toList());

        generatedFiles
                .forEach(file -> {
                    assertThat(file)
                            .isRegularFile();

                    diffFileAgainstSourceSchema(unchangedFile, file);
                });

        return generatedFiles;
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

        LOGGER.info("Comparing {} to {}",
                originalFile.getFileName().toString(),
                revisedFile.getFileName().toString());

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

    private String getFileText(final Path file) throws IOException {
        return String.join("\n", Files.readAllLines(file));
    }
}
