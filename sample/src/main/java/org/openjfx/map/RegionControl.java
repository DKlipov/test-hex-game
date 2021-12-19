package org.openjfx.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openjfx.map.economy.Resource;

import java.util.List;

@AllArgsConstructor
@Data
public class RegionControl {
    private int x;
    private int y;
    private Country country;
    private Terrain terrain;
    private Resource resource;
    private List<Population> population;
    private Area area;
    private Province province;
    private boolean city;
}
