package event.logging.transformer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/// Performs tests against the generated schemas that will actually be released
public class TestReleaseSchemas {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestReleaseSchemas.class);
    private static final Path GENERATED_PATH = Paths.get("pipelines/generated");
    private static final MockSystemService systemService = new MockSystemService();
    private static final Set<Path> schemaFiles = new HashSet<>();

    @BeforeAll
    public static void beforeAll() throws IOException {
        if (Files.exists(GENERATED_PATH)) {
            Assertions.assertThat(GENERATED_PATH).isDirectory();
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

    private Path getFileByPattern(final String regex) {
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

    @Nested
    class SafeSchemeTests {

        private final Path schemaFile;

        SafeSchemeTests() {
            schemaFile = getFileByPattern("event-logging-.*-safe");

            Assertions.assertThat(schemaFile)
                    .isRegularFile();
        }

        @Test
        void test() {

        }
    }
}
