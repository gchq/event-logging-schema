package event.logging.transformer.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Configuration {

    @JsonProperty(required = true)
    private List<Pipeline> pipelines;

    //no-args constructor for jackson
    public Configuration() {
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

    private Pipeline getEffectivePipeline(final Pipeline parentPipeline, Map<String, Pipeline> pipelineMap) {

        if (parentPipeline.getBasePipelineName() != null && parentPipeline.getBasePipelineName().isPresent()) {
            Pipeline basePipeline = pipelineMap.get(parentPipeline.getBasePipelineName().get());
            if (basePipeline == null) {
                throw new RuntimeException(String.format("Unable to find basePipeline [%s] when parsing parent pipeline [%s]",
                        parentPipeline.getBasePipelineName(), parentPipeline.getName()));
            }
            Pipeline combinedPipeline = parentPipeline.merge(basePipeline);

            //recursive call to continue walking the hierarchy
            return getEffectivePipeline(combinedPipeline, pipelineMap);
        } else {
            //no base pipeline so just use this
            return parentPipeline;
        }
    }
}
