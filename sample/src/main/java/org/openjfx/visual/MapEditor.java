package org.openjfx.visual;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import org.openjfx.controls.CountrySelector;
import org.openjfx.controls.SpeedPane;
import org.openjfx.map.Country;
import org.openjfx.map.DataStorage;
import org.openjfx.map.RegionControl;

import javax.xml.crypto.Data;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapEditor {
    private final DataStorage dataStorage;
    private MapDrawer mapDrawer;


    public MapEditor(MapDrawer mapDrawer, Scene scene, CountrySelector countrySelector, DataStorage dataStorage) {
        this.mapDrawer = mapDrawer;
        this.dataStorage = dataStorage;
        scene.setOnMouseDragged(event -> {
            double lastX = event.getX();
            double lastY = event.getY();
            setRegion(lastX, lastY, countrySelector.getCountry());
        });
    }

    private void setRegion(double x, double y, Country tag) {
        var cell = mapDrawer.getCell(x, y);
        if (cell == null) {
            return;
        }
        var region = dataStorage.getRegions().stream()
                .filter(r -> r.getX() == cell.x && r.getY() == cell.y).findAny().orElse(null);
        if (region != null && tag == null) {
            dataStorage.getRegions().remove(region);
        } else if (region != null) {
            region.setCountry(tag);
        } else if (tag != null) {
            dataStorage.getRegions().add(new RegionControl(cell.x, cell.y, tag));
        }
        System.out.println(dataStorage.getRegions().stream()
                .map(l -> l.getX() + "," + l.getY() + "," + l.getCountry().getTag())
                .collect(Collectors.joining("\n")) + "\n\n>>>\n\n");
    }

}
