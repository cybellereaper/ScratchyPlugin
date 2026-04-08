package com.github.cybellereaper.scratchy.templates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class TemplateCodec {
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();

    public String exportTemplate(ScriptTemplate template) throws Exception {
        validate(template);
        return mapper.writeValueAsString(template);
    }

    public ScriptTemplate importTemplate(String raw) throws Exception {
        ScriptTemplate template = mapper.readValue(raw, ScriptTemplate.class);
        validate(template);
        return template;
    }

    private void validate(ScriptTemplate template) {
        if (template.schemaVersion() <= 0) {
            throw new IllegalArgumentException("Template schemaVersion must be > 0");
        }
        if (template.metadata() == null || template.metadata().name() == null || template.metadata().name().isBlank()) {
            throw new IllegalArgumentException("Template metadata.name is required");
        }
        if (template.script() == null) {
            throw new IllegalArgumentException("Template script is required");
        }
    }
}
