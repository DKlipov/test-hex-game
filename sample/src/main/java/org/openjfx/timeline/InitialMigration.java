package org.openjfx.timeline;

import lombok.Getter;
import org.openjfx.map.DataStorage;
import org.openjfx.map.Population;
import org.openjfx.map.RegionControl;
import org.openjfx.utils.CellUtils;

import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InitialMigration implements TimelineEvent {

    private final DataStorage dataStorage;
    @Getter
    private LocalDate date;

    public InitialMigration(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        date = LocalDate.parse("0000-01-01");
    }

    @Override
    public void execute() {
        Set<RegionControl> visited = new HashSet<>();
        dataStorage.getRegions().forEach(r -> {
            if (!r.getPopulation().isEmpty()) {
                return;
            }
            RegionControl ne = CellUtils.getNeigbors(new Point(r.getX(), r.getY()))
                    .stream()
                    .map(p -> dataStorage.getRegion(p.x, p.y))
                    .filter(n -> n != null && !n.getPopulation().isEmpty())
                    .filter(n -> !visited.contains(n))
                    .max(Comparator.comparing(n -> n.getPopulation().size()))
                    .orElse(null);
            if (ne == null || ne.getPopulation().isEmpty()) {
                return;
            }
            visited.add(r);
            visited.add(ne);
            if (ne.getPopulation().size() < 10) {
                r.setPopulation(new CopyOnWriteArrayList<>(List.of(new Population(ne.getPopulation().get(0).getNation()))));
                return;
            }
            try {
                r.setPopulation(ne.getPopulation().subList(0, ne.getPopulation().size() / 2));
                ne.setPopulation(ne.getPopulation().subList((ne.getPopulation().size() / 2) + 1, ne.getPopulation().size() - 1));
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        dataStorage.getRegions().forEach(r -> {
            if (!r.getPopulation().isEmpty()) {
                return;
            }
            RegionControl ne = CellUtils.getNeigbors(new Point(r.getX(), r.getY()))
                    .stream()
                    .map(p -> dataStorage.getRegion(p.x, p.y))
                    .filter(n -> n != null && !n.getPopulation().isEmpty())
                    .max(Comparator.comparing(n -> n.getPopulation().size()))
                    .orElse(null);
            if (ne == null || ne.getPopulation().isEmpty()) {
                return;
            }
            if (ne.getPopulation().size() > 10) {
                var l = new CopyOnWriteArrayList<>(ne.getPopulation().subList(0, ne.getPopulation().size() / 10));
                l.addAll(r.getPopulation());
                r.setPopulation(l);
                ne.setPopulation(ne.getPopulation().subList((ne.getPopulation().size() / 10) + 1, ne.getPopulation().size() - 1));
            }
            if (r.getPopulation().size() > 10) {
                var l = new CopyOnWriteArrayList<>(r.getPopulation().subList(0, r.getPopulation().size() / 10));
                l.addAll(ne.getPopulation());
                ne.setPopulation(l);
                r.setPopulation(r.getPopulation().subList((r.getPopulation().size() / 10) + 1, r.getPopulation().size() - 1));
            }


        });
        System.out.println("\n\n\n///");
        dataStorage.getRegions()
                .stream()
                .filter(r->!r.getPopulation().isEmpty())
                .forEach(r -> System.out.println(r.getX() + "," + r.getY() + "," + r.getPopulation().size() + "," + r.getPopulation().get(0).getNation().getTag()));
    }

    @Override
    public void repeat(TimelineEventLoop loop, LocalDate localDate) {
        this.date = localDate.plus(30, ChronoUnit.DAYS);
        loop.putEvent(this);
    }
}
