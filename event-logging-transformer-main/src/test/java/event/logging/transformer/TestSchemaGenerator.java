package event.logging.transformer;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSchemaGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestReleaseSchemas.class);

    private static final Path SOURCE_SCHEMA = Paths.get("../event-logging.xsd");
    private static final Path TEST_DATA_ROOT_DIR = Paths.get("src/test/resources/test-data");
    private static final Path PIPELINES_RELATIVE_PATH = Paths.get("pipelines");
    private static final Path GENERATED_RELATIVE_PATH = Paths.get("pipelines/generated");
//    private static final Path UNCHANGED_FILE = GENERATED_RELATIVE_PATH.resolve("event-logging-v3-unchanged.xsd");

    private static final String TEST_01 = "test-01";
    private static final String TEST_02 = "test-02";
    private static final String TEST_03 = "test-03";
    private static final List<String> TEST_CASE_NAMES = List.of(
            TEST_01,
            TEST_02,
            TEST_03);

    private MockSystemService systemService = new MockSystemService();

    @BeforeEach
    public void setup() {
        systemService.reset();

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
    public void test01_versionChange() throws Exception {


        final List<Path> generatedFiles = runTest(TEST_01, 0);

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

        assertThat(TestUtil.getFileText(generatedFiles.get(0)))
                .contains(expectedId1);
        assertThat(TestUtil.getFileText(generatedFiles.get(1)))
                .contains(expectedId2);
    }

    @Test
    public void test02_noChanges() throws Exception {
        List<Path> generatedFiles = runTest(TEST_02, 0);

        assertThat(generatedFiles).hasSize(1);

        assertThat(generatedFiles.getFirst().getFileName().toString())
                .matches("event-logging-v([0-9]|SNAPSHOT)-unchanged.xsd");
    }

    @Test
    public void test03_addRegexSimpleType() throws Exception {

        List<Path> generatedFiles = runTest(TEST_03, 0);

        assertThat(generatedFiles).hasSize(1);

        assertThat(generatedFiles.getFirst().getFileName().toString())
                .matches("event-logging-v([0-9]|SNAPSHOT)-changed.xsd");

        // make sure the regex is correct in the generated file
        assertThat(TestUtil.getFileText(generatedFiles.getFirst()))
                .contains("[0-9]{1,3}[A-Z]{3}$");
    }

    private void assertExitStatus(final int status) {
        assertThat(systemService.getExitStatus())
                .isNotNull()
                .isEqualTo(status);
    }

    private void assertGeneratedFilenames(final List<Path> generatedFiles,
                                          final String... expectedFileNames) {
        assertThat(generatedFiles.stream()
                .map(path -> path.getFileName().toString())
                .toList()
        ).containsExactlyInAnyOrder(expectedFileNames);
    }

    private List<Path> runTest(final String name, final int expectedExitStatus) {

        final Path testCaseDir = getTestCaseDir(name);
        final Path pipelinesDir = testCaseDir.resolve(PIPELINES_RELATIVE_PATH);
        final Path generatedDir = testCaseDir.resolve(GENERATED_RELATIVE_PATH);

        // Do the schema generation and assert the exit status
        TestUtil.runGenerator(
                systemService,
                expectedExitStatus,
                pipelinesDir.toString(),
                SOURCE_SCHEMA.toString());

        // Our test should have generated an unchanged file plus at least one more
        assertThat((long) TestUtil.listFiles(generatedDir, Stream::count))
                .isGreaterThanOrEqualTo(2L);

        final Path identitySchemaFile = TestUtil.listFiles(
                testCaseDir.resolve(GENERATED_RELATIVE_PATH),
                fileStream -> fileStream
                        .peek(file ->
                                LOGGER.debug("Generated file: {}", file))
                        .filter(path ->
                                path.toString().endsWith("identity.xsd"))
                        .findFirst()
                        .orElseThrow());

        LOGGER.debug("identitySchemaFile: {}", identitySchemaFile);

        // collect the paths of all generated files bar the special unchanged one
        List<Path> generatedFiles = TestUtil.listFiles(generatedDir, fileStream -> fileStream
                .filter(file ->
                        !file.getFileName().equals(identitySchemaFile.getFileName()))
                .sorted()
                .toList());

        generatedFiles.forEach(file -> {
            assertThat(file)
                    .isRegularFile();

            diffFileAgainstSourceSchema(identitySchemaFile, file);
        });

        return generatedFiles;
    }

    private void diffFileAgainstSourceSchema(final Path originalFile, final Path revisedFile) {

        //build simple lists of the lines of the two testfiles
        final List<String> original = TestUtil.getFileLines(originalFile);
        final List<String> revised = TestUtil.getFileLines(revisedFile);

        //compute the patch: this is the diffutils part
        final Patch<String> patch;
        try {
            patch = DiffUtils.diff(original, revised);
        } catch (final RuntimeException e) {
            throw new RuntimeException("Error diffing files '"
                    + originalFile.getFileName() + "' and '"
                    + revisedFile.getFileName() + "'", e);
        }

        LOGGER.info("Comparing {} to {}",
                originalFile.getFileName().toString(),
                revisedFile.getFileName().toString());

        //simple output the computed patch to console
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            System.out.printf("Original [line %s]%n", delta.getSource().getPosition());
            delta.getSource().getLines().forEach(System.out::println);
            System.out.printf("Revised [line %s]%n", delta.getTarget().getPosition());
            delta.getTarget().getLines().forEach(System.out::println);
        }
    }


    private Path getTestCaseDir(final String testCaseName) {
        Objects.requireNonNull(testCaseName);
        return TEST_DATA_ROOT_DIR.resolve(testCaseName);
    }
}
