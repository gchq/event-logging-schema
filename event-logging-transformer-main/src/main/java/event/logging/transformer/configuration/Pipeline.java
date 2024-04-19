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
    private String pipelineName;

    @JsonProperty
    private String basePipelineName;

    @JsonProperty
    private String outputBaseName;

    @JsonProperty
    private String outputSuffix;

    @JsonProperty(required = true)
    private boolean hasOutput;

    @JsonProperty(required = true)
    private List<String> transformations;

    @JsonProperty
    private boolean modularise;

    //no-args constructor for jackson
    public Pipeline() {
    }

    public Pipeline(String pipelineName,
                    String outputBaseName,
                    String outputSuffix,
                    boolean hasOutput,
                    String basePipelineName,
                    List<String> transformations) {
        this(pipelineName,
                outputBaseName,
                outputSuffix,
                hasOutput,
                basePipelineName,
                transformations,
                false);
    }

    public Pipeline(String pipelineName,
                    String outputBaseName,
                    String outputSuffix,
                    boolean hasOutput,
                    String basePipelineName,
                    List<String> transformations,
                    boolean modularise) {
        this.pipelineName = pipelineName;
        this.outputBaseName = outputBaseName;
        this.outputSuffix = outputSuffix;
        this.hasOutput = hasOutput;
        this.basePipelineName = basePipelineName;
        this.transformations = transformations;
        this.modularise = modularise;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public Optional<String> getOutputSuffix() {
        return Optional.ofNullable(outputSuffix);
    }

    public Optional<String> getOutputBaseName() {
        return Optional.ofNullable(outputBaseName);
    }

    public boolean hasOutput() {
        return hasOutput;
    }

    public Optional<String> getBasePipelineName() {
        return Optional.ofNullable(basePipelineName);
    }

    public List<String> getTransformations() {
        return transformations;
    }

    public boolean isModularise() {
        return modularise;
    }

    public Pipeline merge(final Pipeline basePipeline) {
        LOGGER.debug("Merging basePipeline {} into this {}", basePipeline, this);
        if (!this.getBasePipelineName().orElse("").equals(basePipeline.pipelineName)) {
            throw new RuntimeException(String.format("basePipelineName property [%s] of pipeline [%s] does not match " +
                            "the pipelineName of the passed basePipeline [%s]",
                    this.getBasePipelineName().orElse(""), this.pipelineName, basePipeline.pipelineName));
        }
        List<String> combinedTransformations = new ArrayList<>();
        combinedTransformations.addAll(basePipeline.getTransformations());
        combinedTransformations.addAll(this.getTransformations());

        return new Pipeline(this.getPipelineName(),
                this.outputBaseName,
                this.outputSuffix,
                this.hasOutput(),
                basePipeline.getBasePipelineName().orElse(null),
                combinedTransformations,
                this.modularise);
    }

    @Override
    public String toString() {
        return "Pipeline{" +
                "pipelineName='" + pipelineName + '\'' +
                ", basePipelineName=" + basePipelineName +
                ", outputBaseName=" + outputBaseName +
                ", outputSuffix=" + outputSuffix +
                ", hasOutput=" + hasOutput +
                '}';
    }
}
