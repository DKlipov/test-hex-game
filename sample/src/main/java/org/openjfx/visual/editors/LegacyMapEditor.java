package org.openjfx.visual.editors;

import javafx.scene.Scene;
import org.openjfx.controls.ItemSelector;
import org.openjfx.map.Country;
import org.openjfx.map.DataStorage;
import org.openjfx.map.RegionControl;
import org.openjfx.visual.MapDrawer;

import java.util.stream.Collectors;

public class LegacyMapEditor {
    private final DataStorage dataStorage;
    private MapDrawer mapDrawer;


    public LegacyMapEditor(MapDrawer mapDrawer, Scene scene, ItemSelector<Country> itemSelector, DataStorage dataStorage) {
        this.mapDrawer = mapDrawer;
        this.dataStorage = dataStorage;
        scene.setOnMouseDragged(event -> {
            double lastX = event.getX();
            double lastY = event.getY();
            setRegion(lastX, lastY, itemSelector.getTarget());
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
            //dataStorage.getRegions().add(new RegionControl(cell.x, cell.y, tag));
        }
        System.out.println(dataStorage.getRegions().stream()
                .map(l -> l.getX() + "," + l.getY() + "," + l.getCountry().getTag())
                .collect(Collectors.joining("\n")) + "\n\n>>>\n\n");
    }

}
