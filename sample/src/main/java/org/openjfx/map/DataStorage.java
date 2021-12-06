package org.openjfx.map;

import javafx.scene.paint.Color;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class DataStorage {
    @Getter
    private final List<Country> countries = List.of(
            new Country("FRA", "France", Color.BLUE),
            new Country("GER", "Germany", Color.GRAY),
            new Country("NED", "Netherlands", Color.ORANGE),
            new Country("BEL", "Belgium", Color.DARKOLIVEGREEN),
            new Country("LUX", "Luxembourg", Color.LIMEGREEN),
            new Country("RHI", "Rhineland", Color.CHOCOLATE),
            new Country("SWI", "Switzerland", Color.TOMATO),
            new Country("PRO", "Provence", Color.SKYBLUE),
            new Country("CAT", "Catalonia", Color.DARKORANGE),
            new Country("LOM", "Lombardy", Color.GREENYELLOW),
            new Country("CZE", "Czechia", Color.TAN),
            new Country("DEN", "Denmark", Color.HOTPINK),
            new Country("BAV", "Bavaria", Color.STEELBLUE),
            new Country("YUG", "Yugoslavia", Color.BLUEVIOLET)
    );

    private final List<RegionControl> regions = new ArrayList<>();

    public DataStorage() {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("regions");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, Country> countryMap = countries.stream().collect(Collectors.toMap(c -> c.getTag(), c -> c));
        try {
            while (reader.ready()) {
                String[] line = reader.readLine().split(",");
                regions.add(new RegionControl(Integer.parseInt(line[0]), Integer.parseInt(line[1]), countryMap.get(line[2])));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
