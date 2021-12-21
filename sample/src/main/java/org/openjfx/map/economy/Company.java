package org.openjfx.map.economy;

import lombok.Data;
import org.openjfx.map.Country;
import org.openjfx.map.economy.production.Factory;
import org.openjfx.map.economy.production.ResourceGathering;

import java.util.ArrayList;
import java.util.List;

@Data
public class Company {
    private int budget;
    private Country origin;
    private List<Factory> factories = new ArrayList<>();
    private List<ResourceGathering> gatherings = new ArrayList<>();
}
