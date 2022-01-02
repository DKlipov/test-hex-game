package org.openjfx.map.economy.trade;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.openjfx.map.economy.production.template.TradeGoodType;

import java.util.*;

@Getter
@Setter
public class Storage {
    private final Map<TradeGoodType, Integer> set = new HashMap<>();
}
