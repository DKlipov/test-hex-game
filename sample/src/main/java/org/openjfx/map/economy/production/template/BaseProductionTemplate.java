package org.openjfx.map.economy.production.template;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(of = "id")
public class BaseProductionTemplate {
    private String id;
    private TradeGoodType output;
    private List<TradeGoodType> inputs;
}
