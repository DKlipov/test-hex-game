package org.openjfx.visual;

import javafx.scene.Scene;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class InteractiveMap {
    private final MapDrawer mapDrawer;

    private final Map<String, List<Runnable>> keyCallbacks = new HashMap<>();

    public void addKeyListener(String key, Runnable listener) {
        keyCallbacks.compute(key, (k, v) -> {
            if (v != null) {
                v.add(listener);
                return v;
            } else {
                var list = new ArrayList<Runnable>();
                list.add(listener);
                return list;
            }
        });
    }


    public void keyPressed(String key) {
        keyCallbacks.getOrDefault(key, List.of()).forEach(Runnable::run);
    }

    public void cellClicked(int x, int y) {

    }

    public void cellHovered(int x, int y) {

    }
}
