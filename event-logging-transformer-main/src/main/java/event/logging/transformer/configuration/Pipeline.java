package event.logging.transformer.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Pipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);

    @JsonProperty(required = true)
    private String name;

    @JsonProperty
    private Optional<String> basePipelineName;

    @JsonProperty
    private Optional<String> suffix;

    @JsonProperty(required = true)
    private boolean hasOutput;

    @JsonProperty(required = true)
    private List<String> transformations;

    //no-args constructor for jackson
    public Pipeline() {
    }

    public Pipeline(String name,
                    Optional<String> suffix,
                    boolean hasOutput,
                    Optional<String> basePipelineName,
                    List<String> transformations) {
        this.name = name;
        this.suffix = suffix;
        this.hasOutput = hasOutput;
        this.basePipelineName = basePipelineName;
        this.transformations = transformations;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getSuffix() {
        return suffix;
    }

    public boolean hasOutput() {
        return hasOutput;
    }

    public Optional<String> getBasePipelineName() {
        return basePipelineName;
    }

    public List<String> getTransformations() {
        return transformations;
    }

    public Pipeline merge(final Pipeline basePipeline) {
        LOGGER.debug("Merging basePipeline {} into this {}", basePipeline, this);
        if (!this.basePipelineName.orElse("").equals(basePipeline.name)) {
            throw new RuntimeException(String.format("basePipelineName property [%s] of pipeline [%s] does not match " +
                            "the name of the passed basePipeline [%s]",
                    this.basePipelineName.orElse(""), this.name, basePipeline.name));
        }
        List<String> combinedTransformations = new ArrayList<>();
        combinedTransformations.addAll(basePipeline.getTransformations());
        combinedTransformations.addAll(this.getTransformations());

        return new Pipeline(this.getName(),
                this.getSuffix(),
                this.hasOutput(),
                basePipeline.getBasePipelineName(),
                combinedTransformations);
    }

    @Override
    public String toString() {
        return "Pipeline{" +
                "name='" + name + '\'' +
                ", basePipelineName=" + basePipelineName +
                ", suffix=" + suffix +
                ", hasOutput=" + hasOutput +
                '}';
    }
}
