package event.logging.transformer.configuration;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TestConfiguration {

    @Test
    public void getEffectiveOutputPipelines() {

        //pipe1 (no output)
        Pipeline pipe1 = new Pipeline("pipe1", Optional.empty(), false, Optional.empty(), Arrays.asList("T1.1", "T1.2"));
        //pipe1 <- pipe2 (no output)
        Pipeline pipe2 = new Pipeline("pipe2", Optional.empty(), false, Optional.of("pipe1"), Arrays.asList("T2.1", "T2.2"));
        //pipe1 <- pipe2 <- pipe3
        Pipeline pipe3 = new Pipeline("pipe3", Optional.of("p3"), true, Optional.of("pipe2"), Arrays.asList("T3.1", "T3.2"));
        //pipe1 <- pipe2 <- pipe3 <- pipe4
        Pipeline pipe4 = new Pipeline("pipe4", Optional.of("p4"), true, Optional.of("pipe3"), Arrays.asList("T4.1", "T4.2"));
        //pipe5
        Pipeline pipe5 = new Pipeline("pipe5", Optional.of("p5"), true, Optional.empty(), Arrays.asList("T5.1", "T5.2"));

        //mix the order up a bit
        Configuration configuration = new Configuration(Arrays.asList(pipe2, pipe1, pipe4, pipe3, pipe5));

        assertThat(configuration.getPipelines()).containsExactly(pipe2, pipe1, pipe4, pipe3, pipe5);

        List<Pipeline> effectiveOutputPipelines = configuration.getEffectiveOutputPipelines();
        //Only 3 pipes are output ones
        assertThat(effectiveOutputPipelines).hasSize(3);

        assertThat(
                effectiveOutputPipelines.stream()
                        .map(Pipeline::getName))
                .containsExactly("pipe4", "pipe3", "pipe5");

        final Map<String, Pipeline> effectivePipelineMap = effectiveOutputPipelines.stream()
                .collect(Collectors.toMap(Pipeline::getName, Function.identity()));

        Pipeline effectivePipe3 = effectivePipelineMap.get("pipe3");
        Pipeline effectivePipe4 = effectivePipelineMap.get("pipe4");
        Pipeline effectivePipe5 = effectivePipelineMap.get("pipe5");

        assertThat(effectivePipe3.getName()).isEqualTo("pipe3");
        assertThat(effectivePipe3.getSuffix().get()).isEqualTo("p3");
        assertThat(effectivePipe3.hasOutput()).isTrue();
        assertThat(effectivePipe3.getBasePipelineName()).isEmpty();
        assertThat(effectivePipe3.getTransformations())
                .containsExactly("T1.1", "T1.2", "T2.1", "T2.2", "T3.1", "T3.2");

        assertThat(effectivePipe4.getName()).isEqualTo("pipe4");
        assertThat(effectivePipe4.getSuffix().get()).isEqualTo("p4");
        assertThat(effectivePipe4.hasOutput()).isTrue();
        assertThat(effectivePipe4.getBasePipelineName()).isEmpty();
        assertThat(effectivePipe4.getTransformations())
                .containsExactly("T1.1", "T1.2", "T2.1", "T2.2", "T3.1", "T3.2", "T4.1", "T4.2");

        assertThat(effectivePipe5.getName()).isEqualTo("pipe5");
        assertThat(effectivePipe5.getSuffix().get()).isEqualTo("p5");
        assertThat(effectivePipe5.hasOutput()).isTrue();
        assertThat(effectivePipe5.getBasePipelineName()).isEmpty();
        assertThat(effectivePipe5.getTransformations())
                .containsExactly("T5.1", "T5.2");
    }
}