package org.openjfx.map.economy.trade;

import lombok.Data;
import org.openjfx.map.economy.production.template.TradeGoodType;

import java.util.*;

@Data
public class Storage {
    private final Map<TradeGoodType, Integer> set = new HashMap<>();
}
