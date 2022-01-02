package org.openjfx.map;

import javafx.scene.paint.Color;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = "regions")
@EqualsAndHashCode(exclude = "regions")
@AllArgsConstructor
public class Area {
    private final int id;
    private final List<RegionControl> regions = new ArrayList<>();
    private final Color color = Color.color(Math.random(), Math.random(), Math.random());
}
