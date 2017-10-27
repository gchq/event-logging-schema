package event.logging.transformer.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Configuration {

    @JsonProperty(required = true)
    private String sourceSchemaPath;

    @JsonProperty(required = true)
    private List<Pipeline> pipelines;

    //no-args constructor for jackson
    public Configuration() {
    }

    public Path getRelativeSourceSchemaPath() {
        return Paths.get(sourceSchemaPath);
    }

    public Path getSourceSchemaPath(final Path basePath) {
        return basePath.resolve(sourceSchemaPath);
    }

    public List<Pipeline> getPipelines() {
        return pipelines;
    }
}
