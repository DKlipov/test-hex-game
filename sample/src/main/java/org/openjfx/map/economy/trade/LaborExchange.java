package org.openjfx.map.economy.trade;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openjfx.map.Population;
import org.openjfx.map.RegionControl;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.production.template.TradeGoodType;
import org.openjfx.timeline.BigProductionCycle;
import org.openjfx.utils.SortedMultiset;

import java.util.*;

@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class LaborExchange {
    private final RegionControl place;
    private final Set<LaborOrder> buyOrders = new SortedMultiset<>(Comparator
            .comparing(LaborOrder::getPayment).reversed());
    private final List<Population> sellOrders = new ArrayList<>();

    @Getter(AccessLevel.PUBLIC)
    private int minimalWage = 10;

    public void reset() {
        buyOrders.clear();
    }

    public void sell(Population population) {
        sellOrders.add(population);
    }

    public void buy(LaborOrder buyOrder) {
        buyOrders.add(buyOrder);
    }

    public void computeExchange() {
        int i = 0;
        for (var place : buyOrders) {
            if (place.getPayment() <= 0) {
                return;
            }
            while (i < sellOrders.size() && place.getCount() > 0) {
                place.setCount(place.getCount() - 1);
                place.getTarget().add(sellOrders.get(i));
                sellOrders.get(i).setWorkplace(place.getTarget());
                i++;
            }
            minimalWage = place.getPayment() * 8 / 10;
        }

    }
}
