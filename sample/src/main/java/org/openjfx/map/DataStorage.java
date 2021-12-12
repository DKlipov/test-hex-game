package org.openjfx.map;

import javafx.scene.paint.Color;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openjfx.map.economy.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    @Getter
    private final List<Nation> nations = List.of(
            new Nation("FRA", "France", Color.BLUE),
            new Nation("GER", "Germany", Color.GRAY),
            new Nation("NED", "Netherlands", Color.ORANGE),
            new Nation("BEL", "Belgium", Color.DARKOLIVEGREEN),
            new Nation("FLL", "Flemish", Color.OLIVE),
            new Nation("SWI", "Switzerland", Color.TOMATO),
            new Nation("PRO", "Occtian", Color.SKYBLUE),
            new Nation("CAT", "Catalonian", Color.DARKORANGE),
            new Nation("LOM", "Lombardian", Color.GREENYELLOW),
            new Nation("CZE", "Czech", Color.TAN),
            new Nation("DEN", "Denmark", Color.HOTPINK),
            new Nation("BAV", "Bavaria", Color.STEELBLUE),
            new Nation("SER", "Serbian", Color.BLUEVIOLET),
            new Nation("CRO", "Croatian", Color.AQUAMARINE)
    );

    private final List<RegionControl> regions = new ArrayList<>();

    public RegionControl getRegion(int x, int y) {
        return regions.stream()
                .filter(r -> r.getX() == x && r.getY() == y).findAny().orElse(null);
    }

    public DataStorage() {
        Map<String, Country> countryMap = countries.stream().collect(Collectors.toMap(c -> c.getTag(), c -> c));
        Map<String, Nation> nationMap = nations.stream().collect(Collectors.toMap(c -> c.getTag(), c -> c));
        Map<Integer, Terrain> terrainMap = Stream.of(Terrain.values()).collect(Collectors.toMap(c -> c.getId(), c -> c));
        Map<Integer, Resource> resourcesMap = Stream.of(Resource.values()).collect(Collectors.toMap(c -> c.getId(), c -> c));
        readInput("owners", line -> regions.add(new RegionControl(Integer.parseInt(line[0]), Integer.parseInt(line[1]),
                countryMap.get(line[2]),
                Terrain.GRASSLANDS,
                null,
                new CopyOnWriteArrayList<>())));
        readInput("population", line -> getRegion(Integer.parseInt(line[0]), Integer.parseInt(line[1])).setPopulation(IntStream.range(0, Integer.parseInt(line[2]))
                .boxed().map(i -> new Population(nationMap.get(line[3]))).collect(Collectors.toList())));
        readInput("terrain", line -> getRegion(Integer.parseInt(line[0]), Integer.parseInt(line[1])).setTerrain(terrainMap.get(Integer.parseInt(line[2]))));
        readInput("resources", line -> getRegion(Integer.parseInt(line[0]), Integer.parseInt(line[1])).setResource(resourcesMap.get(Integer.parseInt(line[2]))));
    }

    private void readInput(String input, Consumer<String[]> processor) {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream(input);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            while (reader.ready()) {
                String[] line = reader.readLine().split(",");
                processor.accept(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
