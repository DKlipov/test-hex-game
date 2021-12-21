package org.openjfx.map.economy.production.template;

import lombok.Data;

import java.util.List;

@Data
public class FactoryType {
    private List<BaseProductionTemplate> templates;

//    public static FactoryType OIL = new FactoryType(List.of(
//            new BaseProductionTemplate(TradeGoodType.FUEL, List.of(TradeGoodType.OIL))
//    ));
//    public static FactoryType PLASTIC = new FactoryType(List.of(
//            new BaseProductionTemplate(TradeGoodType.RUBBER, List.of(TradeGoodType.RAW_RUBBER)),
//            new BaseProductionTemplate(TradeGoodType.RUBBER, List.of(TradeGoodType.RAW_RUBBER, TradeGoodType.OIL)),
//            new BaseProductionTemplate(TradeGoodType.PLASTIC, List.of(TradeGoodType.OIL))
//    ));
//    public static FactoryType MACHINES = new FactoryType(List.of(
//            new BaseProductionTemplate(TradeGoodType.MACHINES, List.of(TradeGoodType.ELECTRONIC, TradeGoodType.ALLOYS, TradeGoodType.PLASTIC, TradeGoodType.STEEL)),
//            new BaseProductionTemplate(TradeGoodType.ELECTRONIC, List.of(TradeGoodType.NONFERRUS, TradeGoodType.PLASTIC, TradeGoodType.RUBBER)),
//            new BaseProductionTemplate(TradeGoodType.ENGINE, List.of(TradeGoodType.RUBBER, TradeGoodType.ALLOYS))
//    ));
//    public static FactoryType CAR = new FactoryType(List.of(
//            new BaseProductionTemplate(TradeGoodType.CAR, List.of(TradeGoodType.ENGINE, TradeGoodType.STEEL, TradeGoodType.PLASTIC))
//    ));
//    public static FactoryType CHEMICAL = new FactoryType(List.of(
//            new BaseProductionTemplate(TradeGoodType.MEDICINES, List.of(TradeGoodType.CHEMICALS, TradeGoodType.PLASTIC)),
//            new BaseProductionTemplate(TradeGoodType.FERTILIZER, List.of(TradeGoodType.CHEMICALS))
//    ));
//
//    public static FactoryType CIVIL = new FactoryType(List.of(
//            new BaseProductionTemplate(TradeGoodType.CLOTHES, List.of(TradeGoodType.COTTON)),
//            new BaseProductionTemplate(TradeGoodType.FURNITURE, List.of(TradeGoodType.LUMBER, TradeGoodType.STEEL, TradeGoodType.PLASTIC))
//    ));
//
//    public static FactoryType FOOD = new FactoryType(List.of(
//            new BaseProductionTemplate(TradeGoodType.FOOD, List.of(TradeGoodType.GRAIN)),
//            new BaseProductionTemplate(TradeGoodType.LIQUOR, List.of(TradeGoodType.GRAIN))
//    ));
//
//    public static FactoryType STEEL = new FactoryType(List.of(
//            new BaseProductionTemplate(TradeGoodType.ALLOYS, List.of(TradeGoodType.STEEL, TradeGoodType.ALUMINIUM, TradeGoodType.CHROMIUM))
//    ));

}
