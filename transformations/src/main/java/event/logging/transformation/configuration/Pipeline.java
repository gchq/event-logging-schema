package event.logging.transformation.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Pipeline {

    @JsonProperty
    private String name;

    @JsonProperty
    private String suffix;

    @JsonProperty
    private List<String> transformations;

    //no-args constructor for jackson
    public Pipeline() {
    }

    public Pipeline(String name, String suffix, List<String> transformations) {
        this.name = name;
        this.suffix = suffix;
        this.transformations = transformations;
    }

    public String getName() {
        return name;
    }

    public String getSuffix() {
        return suffix;
    }

    public List<String> getTransformations() {
        return transformations;
    }
}
