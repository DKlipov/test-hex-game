package org.openjfx.timeline;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.PriorityQueue;

public class TimelineEventLoop {

    private final PriorityQueue<TimelineEvent> events = new PriorityQueue<>(Comparator.comparing(TimelineEvent::getDate));

    public TimelineEvent getEvent(LocalDate localDate) {
        TimelineEvent event = events.peek();
        if (event != null && event.getDate().isBefore(localDate)) {
            return events.poll();
        }
        return null;
    }

    public void putEvent(TimelineEvent event) {
        events.add(event);
    }
}
