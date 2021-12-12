package org.openjfx.visual.mapmodes;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.openjfx.map.DataStorage;

import java.awt.*;

public class PopulationMode implements CellStyleProvider {
    @Getter
    @Setter
    private Point awaitedCell;

    private final DataStorage dataStorage;

    private final static Color empty = Color.LIGHTCYAN;

    private final int mapColumns;

    private final PoliticalMode mode;
    private long max = 0;

    public PopulationMode(DataStorage dataStorage, int mapColumns) {
        this.dataStorage = dataStorage;
        this.mapColumns = mapColumns;
        mode = new PoliticalMode(dataStorage, mapColumns);
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
            long popultion = region.getPopulation().size() * 1000;
            if (max < popultion) {
                max = popultion;
                return Color.RED;
            }
            return Color.RED.deriveColor(0, (double) popultion / max, 1.0, 1.0);
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
