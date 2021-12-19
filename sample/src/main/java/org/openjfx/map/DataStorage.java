package org.openjfx.map;

import javafx.scene.paint.Color;
import lombok.Data;
import lombok.Getter;
import org.openjfx.map.data.CountryData;
import org.openjfx.map.economy.Resource;
import org.openjfx.utils.CellUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Data
public class DataStorage {

    @Getter
    private final List<Area> areas = new ArrayList<>(List.of(new Area(0)));

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
    private final Map<Country, CountryData> countryData = countries.stream().map(c -> new CountryData(c, null,
            new ArrayList<>()))
            .collect(Collectors.toMap(c -> c.getCountry(), c -> c));

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
                new CopyOnWriteArrayList<>(), areas.get(0), null, false)));
        readInput("population", line -> getRegion(Integer.parseInt(line[0]), Integer.parseInt(line[1])).setPopulation(
                generatePopulation(Integer.parseInt(line[2]), nationMap.get(line[3]))));
        readInput("terrain", line -> getRegion(Integer.parseInt(line[0]), Integer.parseInt(line[1])).setTerrain(terrainMap.get(Integer.parseInt(line[2]))));
        readInput("resources", line -> getRegion(Integer.parseInt(line[0]), Integer.parseInt(line[1])).setResource(resourcesMap.get(Integer.parseInt(line[2]))));
        readInput("areas", line -> {
            int aaid = Integer.parseInt(line[2]);
            int i = areas.size();
            while (areas.size() <= aaid) {
                areas.add(new Area(i));
                i++;
            }
            var reg = getRegion(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
            reg.setArea(areas.get(aaid));
            areas.get(aaid).getRegions().add(reg);
        });
        areas.remove(0);
        setCities();
        setCapitals();
        setProvinces();
    }

    private void setProvinces() {
        int i = 0;
        for (var a : areas) {
            Map<Country, Province> provinces = new HashMap<>();
            for (var r : a.getRegions()) {
                var p = provinces.get(r.getCountry());
                if (p != null) {
                    r.setProvince(p);
                    p.getRegions().add(r);
                    continue;
                }
                if (r.isCity()) {
                    var pp = new Province(i, r.getCountry(), null);
                    provinces.put(r.getCountry(), pp);
                    getCountryData().get(r.getCountry()).getProvinces().add(pp);
                    i += 1;
                }
            }
            for (var r : a.getRegions()) {
                var p = provinces.get(r.getCountry());
                if (r.getProvince() == null && p != null) {
                    r.setProvince(p);
                    p.getRegions().add(r);
                }
            }
        }

        boolean hasEmpty = true;
        while (hasEmpty) {
            hasEmpty = false;
            Set<RegionControl> used = new HashSet<>();
            for (var r : regions) {
                if (r.getProvince() != null) {
                    continue;
                }
                hasEmpty = true;
                used.add(r);
                Map<Province, Integer> neg = new HashMap<>();
                CellUtils.getNeigbors(new Point(r.getX(), r.getY())).stream()
                        .map(n -> getRegion(n.x, n.y))
                        .filter(n -> n != null)
                        .filter(n -> !used.contains(n))
                        .filter(n -> n.getProvince() != null)
                        .forEach(n -> neg.compute(n.getProvince(), (k, v) -> {
                            if (v==null||v == 0) {
                                return 1;
                            }
                            return v++;
                        }));
                if (neg.isEmpty()) {
                    continue;
                }
                int max = -1;
                Province cand = null;
                for (var e : neg.entrySet()) {
                    if (e.getKey().getCountry()==r.getCountry()&&e.getValue() > max) {
                        max = e.getValue();
                        cand = e.getKey();
                    }
                }
                r.setProvince(cand);
            }
        }
        countryData.values().stream()
                .flatMap(d->d.getProvinces().stream())
                .forEach(p->{
                    RegionControl cand=null;
                    for(var rc:p.getRegions()){
                        if(cand==null||rc.getPopulation().size()>cand.getPopulation().size()){
                            cand=rc;
                        }
                    }
                    p.setCapital(cand);
                });
    }

    private List<Population> generatePopulation(int count, Nation nation) {
        return IntStream.range(0, count)
                .boxed().map(i -> new Population(nation)).collect(Collectors.toList());
    }

    private void setCapitals() {
        regions.forEach(r -> {
            var d = countryData.get(r.getCountry());
            boolean onBorder = CellUtils.getNeigbors(new Point(r.getX(), r.getY())).stream()
                    .map(n -> getRegion(n.x, n.y))
                    .filter(n -> n != null)
                    .anyMatch(n -> n.getCountry() != r.getCountry());
            if (onBorder) {
                return;
            }
            if (d.getCapital() == null || r.getPopulation().size() > d.getCapital().getPopulation().size()) {
                d.setCapital(r);
            }
        });
        countryData.forEach((k, v) -> {
            if (!v.getCapital().isCity()) {
                v.getCapital().setCity(true);
                v.getCapital().getPopulation().addAll(generatePopulation(v.getCapital().getPopulation().size(), v.getCapital().getPopulation().get(0).getNation()));
            }
        });
    }

    private void setCities() {
        areas.forEach(r -> {
            var medium = r.getRegions().size() / r.getRegions().stream().mapToLong(reg -> reg.getPopulation().size()).sum();
            RegionControl max = null;
            for (var reg : r.getRegions()) {
                if (max == null || max.getPopulation().size() < reg.getPopulation().size()) {
                    max = reg;
                }
            }
            if (!max.isCity()) {
                max.setCity(true);
                max.getPopulation().addAll(generatePopulation(max.getPopulation().size(), max.getPopulation().get(0).getNation()));
            }
        });
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
