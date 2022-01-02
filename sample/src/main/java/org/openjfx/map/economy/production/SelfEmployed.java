package org.openjfx.map.economy.production;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.openjfx.map.Population;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.production.template.SelfEmployedType;
import org.openjfx.map.economy.production.template.TradeGoodType;
import org.openjfx.map.economy.trade.Storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class SelfEmployed {
    private final SelfEmployedType type;
    private final Set<Population> population = new HashSet<>();

    private final Storage outputStorage = new Storage();
    private final Storage inputStorage = new Storage();
    private final List<Contract> outputContracts = new ArrayList<>();
    private final List<Contract> inputContracts = new ArrayList<>();
    private int income;
    private int payment;

    public int getProduction() {
        return getPopulation().size() * type.getEffectivency();
    }
}
