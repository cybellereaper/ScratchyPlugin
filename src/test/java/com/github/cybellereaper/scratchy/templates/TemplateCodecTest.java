package com.github.cybellereaper.scratchy.templates;

import com.github.cybellereaper.scratchy.domain.TriggerSpec;
import com.github.cybellereaper.scratchy.script.blocks.ScriptSequence;
import com.github.cybellereaper.scratchy.script.model.ScriptDocument;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateCodecTest {
    @Test
    void roundTripTemplate() throws Exception {
        ScriptTemplate template = new ScriptTemplate(
                ScriptTemplate.CURRENT_SCHEMA_VERSION,
                new TemplateMetadata("Starter", "desc", "author", "1.0.0", List.of("utility"), ">=1.21"),
                new ScriptDocument(UUID.randomUUID(), "script", TriggerSpec.command("hi"), new ScriptSequence(List.of()), List.of(), List.of(), Map.of())
        );
        TemplateCodec codec = new TemplateCodec();

        String serialized = codec.exportTemplate(template);
        ScriptTemplate imported = codec.importTemplate(serialized);

        assertEquals("Starter", imported.metadata().name());
        assertEquals("script", imported.script().name());
    }
}
