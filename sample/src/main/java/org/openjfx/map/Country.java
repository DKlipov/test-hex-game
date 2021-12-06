package org.openjfx.map;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Country {
    private String tag;
    private String name;
    private Color color;
}