package org.openjfx.map.economy.production.template;

import lombok.Data;

import java.util.List;

@Data
public class BaseProductionTemplate {
    private TradeGoodType output;
    private List<TradeGoodType> inputs;
}
