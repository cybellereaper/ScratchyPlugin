package com.github.cybellereaper.scratchy.domain;

public record TriggerSpec(TriggerType type, String value, long intervalTicks) {
    public static TriggerSpec command(String command) {
        return new TriggerSpec(TriggerType.COMMAND, command, 0L);
    }

    public static TriggerSpec scheduled(long intervalTicks) {
        return new TriggerSpec(TriggerType.SCHEDULED, "", intervalTicks);
    }
}
