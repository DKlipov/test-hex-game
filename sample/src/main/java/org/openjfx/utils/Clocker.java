package org.openjfx.utils;

import java.util.ArrayList;
import java.util.List;

public class Clocker {

    public final static List<Runnable> EXECUTION = new ArrayList<>();

    public Clocker() {
        Thread thread = new Thread(() -> {
            while (true) {
                EXECUTION.forEach(e -> e.run());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
        thread.start();
    }
}
