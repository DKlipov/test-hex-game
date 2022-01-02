package org.openjfx.visual.mapmodes;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CellStyle {
    private Color fill;
    private Color border;
    private Color[] borders;
}
