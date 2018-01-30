package event.logging.transformer.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    @JsonProperty(required = true)
    private List<Pipeline> pipelines;

    //no-args constructor for jackson
    public Configuration() {
    }

    public Configuration(final List<Pipeline> pipelines) {
        this.pipelines = pipelines;
    }

    public List<Pipeline> getPipelines() {
        return pipelines;
    }

    /**
     * For each pipeline that hasOuput, i.e. is a terminal pipeline, resolve any inherited pipelines
     */
    public List<Pipeline> getEffectiveOutputPipelines() {
        final Map<String, Pipeline> pipelineMap = pipelines.stream()
                .collect(Collectors.toMap(Pipeline::getName, Function.identity()));

        final List<Pipeline> effectivePipelines = pipelines.stream()
                .filter(Pipeline::hasOutput)
                .map(pipeline -> getEffectivePipeline(pipeline, pipelineMap))
                .collect(Collectors.toList());
        return effectivePipelines;
    }

    private Pipeline getEffectivePipeline(final Pipeline parentPipeline, final Map<String, Pipeline> pipelineMap) {

        final Pipeline effectivePipeline;
        if (parentPipeline.getBasePipelineName() != null && parentPipeline.getBasePipelineName().isPresent()) {
            Pipeline basePipeline = pipelineMap.get(parentPipeline.getBasePipelineName().get());
            if (basePipeline == null) {
                throw new RuntimeException(String.format("Unable to find basePipeline [%s] when parsing parent pipeline [%s]",
                        parentPipeline.getBasePipelineName(), parentPipeline.getName()));
            }
            Pipeline combinedPipeline = parentPipeline.merge(basePipeline);

            //recursive call to continue walking the hierarchy
            effectivePipeline = getEffectivePipeline(combinedPipeline, pipelineMap);
        } else {
            //no base pipeline so just use this
            effectivePipeline = parentPipeline;
        }
        if (LOGGER.isDebugEnabled()) {
            String transformations = effectivePipeline.getTransformations().stream()
                    .map(val -> "  " + val)
                    .collect(Collectors.joining("\n"));
            LOGGER.debug("Returning effective pipeline {} with transformations\n{}",
                    effectivePipeline, transformations);
        }
        return effectivePipeline;
    }
}
