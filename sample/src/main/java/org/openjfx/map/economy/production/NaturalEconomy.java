package org.openjfx.map.economy.production;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.openjfx.map.Population;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.production.template.TradeGoodType;
import org.openjfx.map.economy.trade.Storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class NaturalEconomy {
    private final TradeGoodType tradeGoodType;
    private int size;
    private final Set<Population> population = new HashSet<>();

    private final Storage storage = new Storage();
    private final List<Contract> contracts = new ArrayList<>();
    private int income;
    private int payment;
    private final int effectivency = 14;

    public int getProduction() {
        return Math.min(getSize(), getPopulation().size()) * effectivency;
    }
}
