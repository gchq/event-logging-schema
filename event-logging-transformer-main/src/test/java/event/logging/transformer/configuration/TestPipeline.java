package event.logging.transformer.configuration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class TestPipeline {

    @Test
    public void merge() {
        final String parentName = "parent";
        final String parentSuffix = "parentSuffix";
        final String baseName = "base";
        final String parentTransform1 = "parentTransform1";
        final String parentTransform2 = "parentTransform2";
        final String baseTransform1 = "parentTransform1";
        final String baseTransform2 = "parentTransform2";
        final List<String> parentTransformations = Arrays.asList(parentTransform1, parentTransform2);
        final List<String> baseTransformations = Arrays.asList(baseTransform1, baseTransform2);

        final Pipeline parent = new Pipeline(
                parentName,
                null,
                parentSuffix,
                true,
                baseName,
                parentTransformations);
        final Pipeline base = new Pipeline(
                baseName,
                null,
                null,
                true,
                null,
                baseTransformations);

        final Pipeline merged = parent.merge(base);

        Assertions.assertThat(merged.getPipelineName()).isEqualTo(parentName);
        Assertions.assertThat(merged.hasOutput()).isTrue();
        Assertions.assertThat(merged.getOutputSuffix().get()).isEqualTo(parentSuffix);
        Assertions.assertThat(merged.getBasePipelineName()).isEmpty();
        Assertions.assertThat(merged.getTransformations()).containsExactly(
                baseTransform1,
                baseTransform2,
                parentTransform1,
                parentTransform2);

    }
}
