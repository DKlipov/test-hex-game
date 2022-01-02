package org.openjfx.map.economy.trade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openjfx.map.RegionControl;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.production.template.TradeGoodType;
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
    private final Map<TradeGoodType, Map<Integer, ExchangeStats>> prices = new HashMap<>(
            ResourceLoader.getResources(TradeGoodType.class)
                    .values().stream().collect(Collectors.toMap(v -> v, v -> new TreeMap<>(Comparator.reverseOrder()))));

    public int getPrice(TradeGoodType goodType, int quality) {
        for (var stat : prices.get(goodType).entrySet()) {
            if (stat.getKey() <= quality) {
                return stat.getValue().getPrice();
            }
        }
        return 0;
    }

    public void reset() {
        sellOrders.clear();
        buyOrders.clear();
    }

    public void sell(ExchangeSellOrder sellOrder) {
        sellOrders.add(sellOrder);
    }

    public void buy(ExchangeBuyOrder buyOrder) {
        if (buyOrder.getPrice() <= 0 || buyOrder.getCount() <= 0) {
            return;
        }
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
        prices.values().forEach(Map::clear);
        getBuyOrders().forEach(b -> {
            for (var or : sells.getOrDefault(b.getType(), Set.of())) {
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
                int price = (b.getPrice() + or.getPrice()) / 2;
                Contract contract = new Contract(or.getStorage(), b.getStorage(), b.getType(), or.getQuality(), price, value);
                or.getContracts().add(contract);
                b.getContracts().add(contract);
                result.add(contract);
                prices.get(b.getType()).compute(b.getQuality(), (k, v) -> {
                    if (v == null) {
                        return new ExchangeStats(price, value, 0);
                    } else {
                        v.setPrice(b.getPrice());
                        v.setVolume(v.getVolume() + value);
                        return v;
                    }
                });
            }
            if (getParent() != null) {
                getParent().buy(b);
            }
            prices.get(b.getType()).compute(b.getQuality(), (k, v) -> {
                if (v == null) {
                    return new ExchangeStats(b.getPrice(), 0, b.getCount());
                } else {
                    v.setPrice(b.getPrice());
                    v.setExtraDemand(v.getExtraDemand() + b.getCount());
                    return v;
                }
            });
        });
        if (getParent() != null) {
            sellOrders.stream()
                    .filter(c -> c.getCount() > 0)
                    .forEach(c -> parent.sell(c));
        }
        return result;
    }
}
