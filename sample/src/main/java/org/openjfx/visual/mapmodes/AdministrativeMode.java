package org.openjfx.visual.mapmodes;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.openjfx.map.DataStorage;

import java.awt.*;

@RequiredArgsConstructor
public class AdministrativeMode implements CellStyleProvider {
    @Getter
    @Setter
    private Point awaitedCell;

    private final DataStorage dataStorage;

    private final static Color empty = Color.LIGHTCYAN;

    private final PoliticalMode mode;

    public AdministrativeMode(DataStorage dataStorage, int mapColumns) {
        this.dataStorage = dataStorage;
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
        if (region == null || region.getProvince() == null) {
            return empty;
        } else if (region.getProvince().getCapital() == region) {
            return region.getProvince().getColor().deriveColor(0, 1.0, 0.5, 1.0);
        } else {
            return region.getProvince().getColor();
        }
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
