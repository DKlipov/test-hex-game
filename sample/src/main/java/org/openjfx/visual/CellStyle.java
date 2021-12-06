package org.openjfx.visual;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CellStyle {
    private Color fill;
    private Color border;
    private Color[] borders;
}
