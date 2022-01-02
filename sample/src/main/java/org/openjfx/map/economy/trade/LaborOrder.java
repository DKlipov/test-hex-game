package org.openjfx.map.economy.trade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openjfx.map.Population;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.ContractSide;
import org.openjfx.map.economy.production.template.TradeGoodType;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LaborOrder {
    private final int payment;
    private int count;
    private final Collection<Population> target;
}
