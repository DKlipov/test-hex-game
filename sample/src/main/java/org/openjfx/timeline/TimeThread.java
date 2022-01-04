package org.openjfx.timeline;

import org.openjfx.controls.SpeedPane;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeThread {
    private long timestamp;
    private final SpeedPane speedPane;
    private final TimelineEventLoop eventLoop;

    public TimeThread(SpeedPane speedPane, TimelineEventLoop eventLoop) {
        timestamp = Instant.now().toEpochMilli();
        Thread thread = new Thread(()->{
            while (true){
                iterate();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
        this.speedPane = speedPane;
        this.eventLoop = eventLoop;
        thread.start();
    }

    private void iterate() {
        long milli = Instant.now().toEpochMilli();
        if (speedPane.getSpeed() == 0) {
            timestamp = milli;
            return;
        }
        if ((milli - timestamp) > (100 / speedPane.getSpeed()/speedPane.getSpeed())) {
            timestamp = milli;
            speedPane.setDate(speedPane.getDate().plus(1, ChronoUnit.DAYS));
            var date = speedPane.getDate();
            TimelineEvent event = eventLoop.getEvent(date);
            while (event != null) {
                event.execute();
                event.repeat(eventLoop, date);
                event = eventLoop.getEvent(date);
            }
        }
    }
}
