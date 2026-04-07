package com.github.cybellereaper.scratchy.engine;

public interface SchedulerFacade {
    void runLater(long ticks, Runnable runnable);
}
