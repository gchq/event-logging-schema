package event.logging.transformer.schemas;


import event.logging.transformer.SchemaGenerator;
import event.logging.transformer.SystemService;
import event.logging.transformer.schemas.MockSystemService.SystemExitException;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtil {
    private TestUtil() {
    }

    /// List all files in a directory. streamFunction is used to transform the stream of paths
    /// into the desired result.
    ///
    /// @return The result of applying streamFunction to the stream of paths
    public static <T> T listFiles(final Path dir,
                                  final Function<Stream<Path>, T> streamFunction) {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(streamFunction);
        try (final Stream<Path> stream = Files.list(dir)) {
            return streamFunction.apply(stream);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error listing files in directory %s", dir));
        }
    }

    /// Calls streamConsumer for each file in the directory.
    public static void forEachFile(final Path dir,
                                   final Consumer<Path> streamConsumer) {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(streamConsumer);
        try (final Stream<Path> stream = Files.list(dir)) {
            stream.forEach(streamConsumer);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error listing files in directory %s", dir));
        }
    }

    public static String getFileText(final Path file) {
        return String.join("\n", getFileLines(file));
    }

    public static List<String> getFileLines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error reading file %s", file));
        }
    }

    /// Call {@link SchemaGenerator#run(SystemService, String...)} and assert the exit status
    public static void runGenerator(final MockSystemService systemService,
                                    final int expectedExitStatus,
                                    final String... args) {
        Assertions.assertThatThrownBy(() -> {
                    // Run the generator
                    SchemaGenerator.run(systemService, args);
                })
                .isInstanceOf(SystemExitException.class)
                .satisfies(e -> {
                    final SystemExitException systemExitException = (SystemExitException) e;
                    assertThat(systemExitException.getStatus())
                            .isEqualTo(expectedExitStatus);
                });
    }
}
