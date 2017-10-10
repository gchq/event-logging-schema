package event.logging.transformation.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Configuration {

    @JsonProperty
    private List<Pipeline> pipelines;

    //no-args constructor for jackson
    public Configuration() {
    }

    public Configuration(List<Pipeline> pipelines) {
        this.pipelines = pipelines;
    }


    public List<Pipeline> getPipelines() {
        return pipelines;
    }
}
