package org.openjfx.map.economy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.openjfx.map.RegionControl;
import org.openjfx.map.economy.production.Factory;
import org.openjfx.map.economy.production.NativeEmployee;
import org.openjfx.map.economy.production.ResourceGathering;
import org.openjfx.map.economy.production.template.TradeGoodType;
import org.openjfx.utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = "region")
@ToString(exclude = "region")
public class RegionEconomy {
    public static final int MAX_CAPACITY = 30;
    private final RegionControl region;
    private final List<Factory> industry = new ArrayList<>();
    private final List<ResourceGathering> gatherings = new ArrayList<>();
    private final List<Contract> contracts = new ArrayList<>();
    private final NativeEmployee nativeEmployee=new NativeEmployee(
            ResourceLoader.getResources(TradeGoodType.class).get("GRAIN")
    );
}
