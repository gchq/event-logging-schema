package event.logging.transformer;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestRealPipelines {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRealPipelines.class);

    private static final Path GENERATED_PATH = Paths.get("./pipelines/generated");

    @Before
    public void setup() throws IOException {

        if (Files.exists(GENERATED_PATH)) {
            Assertions.assertThat(GENERATED_PATH).isDirectory();
            SchemaGenerator.emptyDirectory(GENERATED_PATH);
        }
    }

    @Test
    public void testMain() throws IOException {
        SchemaGenerator.main(new String[]{
                "pipelines",
                "../event-logging.xsd"});

        //two pipelines in config so should result in 2 files
        Assertions.assertThat(Files.list(GENERATED_PATH).count())
                .isGreaterThanOrEqualTo(2);

        Files.list(GENERATED_PATH)
                .forEach(file -> {
                    Assertions.assertThat(file)
                            .isRegularFile();
                    Assertions.assertThat(file.getFileName().toString())
                            .endsWith(".xsd");
                });
    }
}