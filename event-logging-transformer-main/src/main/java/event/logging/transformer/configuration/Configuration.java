package event.logging.transformer.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Configuration {

    @JsonProperty(required = true)
    private List<Pipeline> pipelines;

    //no-args constructor for jackson
    public Configuration() {
    }

    public List<Pipeline> getPipelines() {
        return pipelines;
    }
}
