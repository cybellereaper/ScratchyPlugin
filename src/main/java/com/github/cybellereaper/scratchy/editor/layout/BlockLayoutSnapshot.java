package com.github.cybellereaper.scratchy.editor.layout;

import java.util.List;
import java.util.UUID;

public record BlockLayoutSnapshot(List<UUID> orderedBlockIds, List<Integer> insertionZones) {
}
