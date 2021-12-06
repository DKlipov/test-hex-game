package org.openjfx.visual;

import javafx.animation.AnimationTimer;

import java.time.Instant;
import java.util.Arrays;

public class AnimationTimerDecorator {
    private AnimatedComponent[] components = new AnimatedComponent[100];
    private int last = 0;
    private boolean started = false;
    private long millis = Instant.now().toEpochMilli();

    public void addAnimation(AnimatedComponent component) {
        if (component == null) {
            return;
        }
        if (components.length >= last) {
            components = Arrays.copyOf(components, components.length * 2);
        }
        components[last] = component;
        last++;
    }

    public void start() {
        if (started) {
            return;
        }
        started = true;
        var timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long diff = now - millis;
                millis = now;
                for (int i = 0; i < last; i++) {
                    components[i].update(now, diff);
                }
            }
        };
        timer.start();
    }
}
