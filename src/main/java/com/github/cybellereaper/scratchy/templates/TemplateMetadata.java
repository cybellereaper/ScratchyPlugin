package com.github.cybellereaper.scratchy.templates;

import java.util.ArrayList;
import java.util.List;

public record TemplateMetadata(String name,
                               String description,
                               String author,
                               String version,
                               List<String> tags,
                               String compatibility) {
    public TemplateMetadata {
        tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
    }
}
