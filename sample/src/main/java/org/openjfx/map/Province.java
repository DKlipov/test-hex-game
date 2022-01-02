package org.openjfx.map;

import javafx.scene.paint.Color;
import lombok.*;
import org.openjfx.map.economy.production.SelfEmployed;
import org.openjfx.map.economy.trade.Storage;
import org.openjfx.utils.SortedMultiset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class Province {
    private final int id;
    private final Country country;
    private RegionControl capital;
    private double[] consuming;
    private final List<RegionControl> regions = new ArrayList<>();
    private final Color color = Color.color(Math.random(), Math.random(), Math.random());
    private final Storage storage = new Storage();
    private final List<PopulationGroup> populationGroups = new ArrayList<>();
    private final Set<SelfEmployed> selfEmployed = new SortedMultiset<>(Comparator.comparing(SelfEmployed::getPayment)
            .reversed());
}
