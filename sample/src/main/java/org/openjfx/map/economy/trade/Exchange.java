package org.openjfx.map.economy.trade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openjfx.map.RegionControl;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.production.template.TradeGoodType;
import org.openjfx.timeline.BigProductionCycle;
import org.openjfx.utils.ResourceLoader;
import org.openjfx.utils.SortedMultiset;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class Exchange {
    private RegionControl place;
    private Exchange parent;
    private final List<ExchangeBuyOrder> buyOrders = new ArrayList<>();
    private final List<ExchangeSellOrder> sellOrders = new ArrayList<>();
    private final Map<TradeGoodType, Integer> prices = new HashMap<>(ResourceLoader.getResources(TradeGoodType.class)
            .values().stream().collect(Collectors.toMap(v -> v, v -> 2)));

    public void reset() {
        sellOrders.clear();
        buyOrders.clear();
    }

    public void sell(ExchangeSellOrder sellOrder) {
        sellOrders.add(sellOrder);
    }

    public void buy(ExchangeBuyOrder buyOrder) {
        buyOrders.add(buyOrder);
    }

    public List<Contract> computeExchange() {
        Map<TradeGoodType, Set<ExchangeSellOrder>> sells = new HashMap<>();
        getSellOrders().forEach(o -> sells.compute(o.getType(), (key, v) -> {
            if (v == null) {
                var value = new SortedMultiset<>(Comparator
                        .comparing(ExchangeSellOrder::getQuality)
                        .reversed()
                        .thenComparing(ExchangeSellOrder::getPrice));
                value.add(o);
                return value;
            } else {
                v.add(o);
                return v;
            }
        }));
        List<Contract> result = new ArrayList<>();
        getBuyOrders().forEach(b -> {
            if (b.getPrice() <= 0) {
                return;
            }
            if (sells.get(b.getType()) == null) {
                if (getParent() != null && b.getCount() > 0) {
                    getParent().buy(b);
                } else if (parent == null) {
                    prices.put(b.getType(), b.getPrice());
                }
                return;
            }
            for (var or : sells.get(b.getType())) {
                if (b.getCount() == 0) {
                    return;
                }
                if (b.getQuality() > or.getQuality()) {
                    getParent().buy(b);
                    return;
                }
                if (or.getPrice() > b.getPrice()) {
                    continue;
                }
                int value;
                if (or.getCount() > b.getCount()) {
                    value = b.getCount();
                    or.setCount(or.getCount() - b.getCount());
                    b.setCount(0);
                } else {
                    value = or.getCount();
                    b.setCount(b.getCount() - or.getCount());
                    or.setCount(0);
                }
                int price = b.getPrice();
                Contract contract = new Contract(or.getStorage(), b.getStorage(), b.getType(), or.getQuality(), price, value);
                or.getContracts().add(contract);
                b.getContracts().add(contract);
                result.add(contract);
                if (parent == null) {
                    prices.put(b.getType(), price);
                }
            }
            if (getParent() != null && b.getCount() > 0) {
                getParent().buy(b);
            }
        });
        if (getParent() == null) {
            return result;
        }
        sellOrders.stream()
                .filter(c -> c.getCount() > 0)
                .forEach(c -> parent.sell(c));
        return result;
    }
}
