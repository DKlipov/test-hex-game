package org.openjfx.visual.mapmodes;

import javafx.scene.paint.Color;
import org.openjfx.utils.Clocker;

import java.util.ArrayList;
import java.util.List;

public class CachedStyleProvider implements CellStyleProvider {
    private final List<List<CellStyle>> styles;
    private final int mapRows;
    private final int mapColumns;
    private CellStyleProvider provider;

    public CachedStyleProvider(int mapRows, int mapColumns, CellStyleProvider defaultProvider) {
        this.mapColumns = mapColumns;
        this.mapRows = mapRows;
        provider = defaultProvider;
        styles = new ArrayList<>(mapColumns);
        for (int i = 0; i < mapColumns; i++) {
            styles.add(new ArrayList<>(mapRows));
            styles.add(new ArrayList<>(mapRows));
            for (int j = 0; j < mapRows; j++) {
                styles.get(i).add(null);
                styles.get(i).add(null);
            }
        }
        Clocker.EXECUTION.add(() -> updateCache());
        updateCache();
    }

    private void updateCache() {
        for (int i = 0; i < mapColumns; i++) {
            for (int j = 0; j < mapRows; j++) {
                styles.get(i).set(j, provider.getStyle(i, j));
            }
        }
    }

    public void setProvider(CellStyleProvider provider) {
        this.provider = provider;
        updateCache();
    }

    @Override
    public Color getFill(int x, int y) {
        return styles.get(x).get(y).getFill();
    }

    @Override
    public CellStyle getStyle(int x, int y) {
        return styles.get(x).get(y);
    }

    @Override
    public Color getBorder(int x, int y) {
        return styles.get(x).get(y).getBorder();
    }

    @Override
    public Color[] getBordersColor(int x, int y) {
        return styles.get(x).get(y).getBorders();
    }
}
