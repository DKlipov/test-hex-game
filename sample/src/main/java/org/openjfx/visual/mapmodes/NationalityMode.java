package org.openjfx.visual.mapmodes;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.openjfx.map.DataStorage;
import org.openjfx.map.Nation;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NationalityMode implements CellStyleProvider {
    @Getter
    @Setter
    private Point awaitedCell;

    private final DataStorage dataStorage;

    private final static Color empty = Color.LIGHTCYAN;

    private final PoliticalMode mode;
    private long max = 0;

    private final Map<Nation, Integer> nationMap;

    public NationalityMode(DataStorage dataStorage, int mapColumns) {
        this.dataStorage = dataStorage;
        mode = new PoliticalMode(dataStorage, mapColumns);
        nationMap = new HashMap<>();
        for (int i = 0; i < dataStorage.getNations().size(); i++) {
            nationMap.put(dataStorage.getNations().get(i), i);
        }
    }

    @Override
    public CellStyle getStyle(int x, int y) {
        return new CellStyle(getFill(x, y),
                getBorder(x, y),
                getBordersColor(x, y));
    }

    @Override
    public Color getFill(int x, int y) {
        var region = dataStorage.getRegion(x, y);
        if (region == null) {
            return empty;
        } else if (!region.getPopulation().isEmpty()) {
            int[] nations = new int[dataStorage.getNations().size()];
            region.getPopulation().forEach(p -> nations[nationMap.get(p.getNation())] += 1);
            int max = 0;
            Nation m = null;
            for (int i = 0; i < nations.length; i++) {
                if (nations[i] >= max) {
                    max = nations[i];
                    m = dataStorage.getNations().get(i);
                }
            }
            return m.getColor();
        }
        return Color.WHITE;
    }

    @Override
    public Color getBorder(int x, int y) {
        return mode.getBorder(x, y);
    }

    @Override
    public Color[] getBordersColor(int x, int y) {
        return mode.getBordersColor(x, y);
    }
}
