package org.openjfx.map.economy.production.template;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(of = "id")
public class SelfEmployedType {
    private String id;
    private TradeGoodType output;
    private TradeGoodType input;

    private final int effectivency = 10;
}
