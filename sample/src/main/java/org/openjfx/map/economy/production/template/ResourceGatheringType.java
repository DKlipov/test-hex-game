package org.openjfx.map.economy.production.template;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openjfx.map.Terrain;
import org.openjfx.map.economy.Resource;

import java.util.List;

@Getter
@Setter
@ToString(of = "id")
public class ResourceGatheringType {
    private String id;
    private TradeGoodType output;
    private List<TradeGoodType> preconditions;
    private List<TradeGoodType> consumables;
    private Resource resourceRequirements;
    private Terrain terrainRequirements;
}
