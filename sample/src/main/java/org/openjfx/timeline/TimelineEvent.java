package org.openjfx.timeline;

import java.time.LocalDate;

public interface TimelineEvent {
    void execute();

    void repeat(TimelineEventLoop loop, LocalDate localDate);

    LocalDate getDate();
}
