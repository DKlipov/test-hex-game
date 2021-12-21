package org.openjfx.map.economy.production.template;

import lombok.Data;
import org.openjfx.map.Terrain;
import org.openjfx.map.economy.Resource;

import java.util.List;

@Data
public class ResourceGatheringType {
    private TradeGoodType output;
    private List<TradeGoodType> preconditions;
    private List<TradeGoodType> consumables;
    private Resource resourceRequirements;
    private Terrain terrainRequirements;

//    public static ResourceGatheringType OIL = new ResourceGatheringType(
//            TradeGoodType.OIL, List.of(TradeGoodType.MACHINES),
//            new ArrayList<>(),
//            Resource.OIL,
//            null
//    );
//    public static ResourceGatheringType STEEL = new ResourceGatheringType(
//            TradeGoodType.STEEL, List.of(TradeGoodType.MACHINES, TradeGoodType.STEEL),
//            List.of(TradeGoodType.COAL),
//            Resource.IRON,
//            null
//    );
//    public static ResourceGatheringType NONFERRUS = new ResourceGatheringType(
//            TradeGoodType.NONFERRUS, List.of(TradeGoodType.MACHINES, TradeGoodType.STEEL),
//            List.of(TradeGoodType.COAL),
//            Resource.NONFERRUS,
//            null
//    );
//    public static ResourceGatheringType CHROMIUM = new ResourceGatheringType(
//            TradeGoodType.CHROMIUM, List.of(TradeGoodType.MACHINES, TradeGoodType.STEEL),
//            List.of(TradeGoodType.COAL),
//            Resource.CHROMIUM,
//            null
//    );
//    public static ResourceGatheringType ALUMINIUM = new ResourceGatheringType(
//            TradeGoodType.ALUMINIUM, List.of(TradeGoodType.MACHINES, TradeGoodType.STEEL),
//            List.of(TradeGoodType.COAL),
//            Resource.ALUMINIUM,
//            null
//    );
//    public static ResourceGatheringType COAL = new ResourceGatheringType(
//            TradeGoodType.COAL, List.of(TradeGoodType.MACHINES, TradeGoodType.CAR),
//            List.of(),
//            Resource.COAL,
//            null
//    );
//    public static ResourceGatheringType RUBBER = new ResourceGatheringType(
//            TradeGoodType.RAW_RUBBER, List.of(),
//            List.of(),
//            Resource.RUBBER,
//            null
//    );
//    public static ResourceGatheringType CHEMICALS = new ResourceGatheringType(
//            TradeGoodType.CHEMICALS, List.of(TradeGoodType.MACHINES, TradeGoodType.CAR),
//            List.of(),
//            Resource.CHEMICALS,
//            null
//    );
//    public static ResourceGatheringType GRAIN = new ResourceGatheringType(
//            TradeGoodType.GRAIN, List.of(TradeGoodType.CAR),
//            List.of(TradeGoodType.FERTILIZER),
//            null,
//            Terrain.GRASSLANDS
//    );
//    public static ResourceGatheringType LUMBER = new ResourceGatheringType(
//            TradeGoodType.LUMBER, List.of(TradeGoodType.MACHINES, TradeGoodType.CAR),
//            List.of(),
//            null,
//            Terrain.FOREST
//    );
//    public static ResourceGatheringType COTTON = new ResourceGatheringType(
//            TradeGoodType.COTTON, List.of(TradeGoodType.CAR),
//            List.of(TradeGoodType.FERTILIZER),
//            null,
//            Terrain.GRASSLANDS
//    );
}
