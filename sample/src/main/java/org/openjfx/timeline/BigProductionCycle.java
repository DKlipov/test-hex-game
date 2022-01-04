package org.openjfx.timeline;

import lombok.Getter;
import org.openjfx.map.DataStorage;
import org.openjfx.map.Population;
import org.openjfx.map.PopulationGroup;
import org.openjfx.map.Province;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.ContractSide;
import org.openjfx.map.economy.RegionEconomy;
import org.openjfx.map.economy.production.SelfEmployed;
import org.openjfx.map.economy.production.template.TradeGoodGroup;
import org.openjfx.map.economy.production.template.TradeGoodType;
import org.openjfx.map.economy.trade.Exchange;
import org.openjfx.map.economy.trade.ExchangeBuyOrder;
import org.openjfx.map.economy.trade.ExchangeSellOrder;
import org.openjfx.map.economy.trade.LaborOrder;
import org.openjfx.utils.ResourceLoader;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BigProductionCycle implements TimelineEvent {

    private final DataStorage dataStorage;
    @Getter
    private LocalDate date;

    public final static List<Contract> allContracts = new ArrayList<>();

    private final List<TradeGoodType> peopleConsuming = ResourceLoader.getResources(TradeGoodType.class)
            .values().stream()
            .filter(r -> r.getGroups().contains(TradeGoodGroup.CONSUMER_GOODS))
            .sorted(Comparator.comparing(TradeGoodType::getPriority))
            .collect(Collectors.toList());

    private final Map<TradeGoodType, Integer> peopleConsumingMapping = IntStream.range(0, peopleConsuming.size()).boxed()
            .collect(Collectors.toMap(peopleConsuming::get, v -> v));

    public BigProductionCycle(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        date = LocalDate.parse("0000-01-01");
    }

    @Override
    public void execute() {
        dataStorage.getExchanges().values().forEach(Exchange::reset);
        int counter = 100;
        for (var c : allContracts) {
            if (counter > 1) {
                c.setCount(0);
                counter = 0;
            } else {
                counter++;
            }
        }
        dataStorage.getRegionsEconomy()
                .forEach(re -> {
                    setEmployee(re);
                    var exchange = dataStorage.getExchanges().get(re.getRegion().getProvince());
                    computeIndustry(re, exchange);
                    re.getGatherings().forEach(g -> {
                        clearContractsAndCreateNew(g.getContracts(), exchange,
                                new ExchangeSellOrder(g.getType().getOutput(),
                                        1, g.getPrice(), 0,
                                        new ContractSide(g.getStorage(), inc -> g.setIncome(g.getIncome() + inc)),
                                        g.getContracts()),
                                g.getMaxProduction()
                        );
                    });
                    var nativ = re.getNaturalEconomy();
                    if (nativ.getPopulation().size() > 0) {
                        clearContractsAndCreateNew(nativ.getContracts(), exchange,
                                new ExchangeSellOrder(nativ.getTradeGoodType(),
                                        1, 1, 0,
                                        new ContractSide(nativ.getStorage(), i -> nativ.setIncome(nativ.getIncome() + i)),
                                        nativ.getContracts()),
                                nativ.getProduction());
                    }
                });
        var globalExchange = dataStorage.getExchanges().get(dataStorage);
        dataStorage.getCountryData().values().forEach(c -> {
            var exchange = dataStorage.getExchanges().get(c);
            c.getProvinces().stream()
                    .map(Province::getSelfEmployed).flatMap(Collection::stream).forEach(l -> {
                computeSelfEmplyed(l, globalExchange, exchange);
            });
        });
        dataStorage.getCountryData().values().stream().flatMap(c -> c.getProvinces().stream()).forEach(this::createConsuming);
        makeContracts();
        outResults();
    }

    private void computeSelfEmplyed(SelfEmployed l, Exchange exchange, Exchange globalExchange) {
        if (l.getPopulation().isEmpty()) {
            return;
        }
        int inputPrice = globalExchange.getPrice(l.getType().getInput(), 1);
        int outputPrice = globalExchange.getPrice(l.getType().getOutput(), 1);
        int price = (inputPrice + outputPrice) / 2;
        clearContractsAndCreateNew(l.getOutputContracts(), exchange,
                new ExchangeSellOrder(l.getType().getOutput(),
                        1, price, 0,
                        new ContractSide(l.getOutputStorage(), i -> l.setIncome(l.getIncome() + i)),
                        l.getOutputContracts()),
                l.getProduction()
        );

        clearContractsAndCreateBuy(l.getOutputContracts(), exchange,
                new ExchangeBuyOrder(l.getType().getInput(),
                        1, price, 0,
                        new ContractSide(l.getInputStorage(), i -> l.setIncome(l.getIncome() + i)),
                        l.getInputContracts()),
                l.getProduction(), l.getType().getInput());
    }

    private void computeIndustry(RegionEconomy re, Exchange exchange) {
        re.getIndustry().forEach(f -> f.getLines().forEach(l -> {
            clearContractsAndCreateNew(l.getOutputContracts(), exchange,
                    new ExchangeSellOrder(l.getTemplate().getOutput(),
                            l.getQuality(), l.getPrice(), 0,
                            new ContractSide(l.getOutputStorage(), i -> f.setIncome(f.getIncome() + i)),
                            l.getOutputContracts()),
                    (int) (l.getMaxProduction() * l.getWorkload())
            );
            l.getInputContracts().removeIf(c -> c.getCount() <= 0);
            Map<TradeGoodType, Integer> expected = new HashMap<>();
            l.getInputContracts().forEach(ic -> expected.compute(ic.getType(), (k, v) -> {
                if (v == null) {
                    return ic.getCount();
                } else {
                    return v + ic.getCount();
                }
            }));
            for (int i = 0; i < l.getTemplate().getInputs().size(); i++) {
                var it = l.getTemplate().getInputs().get(i);
                clearContractsAndCreateBuy(l.getInputContracts(), exchange,
                        new ExchangeBuyOrder(it,
                                l.getInputsQuality()[i], l.getInputsPrice()[i],
                                0,
                                new ContractSide(l.getInputStorage(), inc -> f.setIncome(f.getIncome() + inc)),
                                l.getInputContracts()
                        ),
                        l.getMaxProduction() * l.getInputsQuantity()[i],
                        it
                );
            }
        }));
    }


    private void outResults() {
        allContracts.removeIf(c -> c.getCount() <= 0);
//        System.out.println("New contracts: ");
//        reduce(newContracts).forEach(s -> System.out.println('\t' + s));
        allContracts.addAll(newContracts);
        System.out.println("Totally contracts: ");
        reduce(allContracts).forEach(s -> System.out.println('\t' + s));
        var globalExchange = dataStorage.getExchanges().get(dataStorage);
        System.out.println("Expected to sell: ");
        Map<TradeGoodType, Integer> sell = new HashMap<>();
        globalExchange.getSellOrders().forEach(so -> sell.compute(so.getType(), (k, v) -> {
            if (v == null) {
                return so.getCount();
            } else {
                return v + so.getCount();
            }
        }));
        Map<TradeGoodType, Integer> buy = new HashMap<>();
        System.out.println("Expected to buy: ");
        globalExchange.getBuyOrders().forEach(so -> buy.compute(so.getType(), (k, v) -> {
            if (v == null) {
                return so.getCount();
            } else {
                return v + so.getCount();
            }
        }));
        Set<String> set = new TreeSet<>(Comparator.naturalOrder());

        sell.forEach((key, value) -> {
            set.add(printTemplate(key.getId(), value, buy.getOrDefault(key, 0), globalExchange.getPrice(key, 100)));
            buy.put(key, 0);
        });
        buy.forEach((k, v) -> {
            if (v == 0) {
                return;
            }
            set.add(printTemplate(k.getId(), 0, v, globalExchange.getPrice(k, 100)));
        });
        set.forEach(System.out::println);
    }

    private String printTemplate(String id, int sell, int buy, int price) {
        return ("| " + format(id, 8) + " | " + format(Integer.toString(sell), 6) + " | " + format(Integer.toString(buy), 6) + " | "
                + format(Integer.toString(price), 4));
    }

    private String format(String width, int size) {
        if (width.length() > size) {
            return width.substring(0, size);
        } else if (width.length() == size) {
            return width;
        }
        var template = "                                 ";
        return width + template.substring(0, size - width.length());
    }

    private Set<String> printOrdered(Map<TradeGoodType, Integer> map) {
        TreeSet<String> set = new TreeSet<>(Comparator.naturalOrder());
        set.addAll(map.entrySet().stream()
                .map(e -> e.getKey().getId() + ": " + e.getValue()).collect(Collectors.toSet()));
        return set;
    }

    private Set<String> reduce(Collection<Contract> contracts) {
        Map<TradeGoodType, Integer> result = new HashMap<>();
        contracts.forEach(c -> result.compute(c.getType(), (k, v) -> {
            if (v == null) {
                return c.getCount();
            } else {
                return v + c.getCount();
            }
        }));

        return printOrdered(result);
    }

    private int[] baseConsumingForGroup(int price) {
        return peopleConsuming.stream().mapToInt(c -> price).toArray();
    }

    private PopulationGroup createGroup(Province province, int baseIncome) {
        double[] consuming = Arrays.copyOf(province.getConsuming(), peopleConsuming.size());
        double sum = Arrays.stream(consuming).sum();
        return new PopulationGroup(baseIncome, 0,
                consuming, baseConsumingForGroup((int) (baseIncome / sum)));
    }

    private void setEmployee(RegionEconomy economy) {
        var pops = economy.getRegion().getPopulation();
        var lExchange = dataStorage.getLaborExchanges().get(economy.getRegion());
        lExchange.reset();
        int wage = lExchange.getMinimalWage();
        int counter = 100;
        for (var pop : pops) {
            if (pop.getWorkplace() == null || pop.getPayment() > wage) {
                continue;
            }
            if (counter < 5) {
                counter++;
                continue;
            }
            counter = 0;
            pop.getWorkplace().remove(pop);
            pop.setWorkplace(null);
        }
        for (var f : economy.getIndustry()) {
            if (f.getSize() > f.getEmployee().size()) {
                lExchange.buy(new LaborOrder(f.getPayment(),
                        f.getSize() - f.getEmployee().size(),
                        f.getEmployee()));
            }
        }
        for (var f : economy.getGatherings()) {
            if (f.getSize() > f.getEmployee().size()) {
                lExchange.buy(new LaborOrder(f.getPayment(),
                        f.getSize() - f.getEmployee().size(),
                        f.getEmployee()));
            }
        }
        var nativeEmp = economy.getNaturalEconomy();
        nativeEmp.setSize((RegionEconomy.MAX_CAPACITY / economy.getRegion().getTerrain().getEconomyAbility()) -
                economy.getGatherings().stream().map(g -> g.getSize() * 3).mapToInt(t -> t).sum());
        if (nativeEmp.getSize() * 5 > nativeEmp.getPopulation().size()) {
            lExchange.buy(new LaborOrder(nativeEmp.getPayment(),
                    nativeEmp.getSize() * 5 - nativeEmp.getPopulation().size(),
                    nativeEmp.getPopulation()));
        }
        pops.forEach(pop -> {
            if (pop.getWorkplace() == null) {
                lExchange.sell(pop);
            }
        });
        lExchange.buy(new LaborOrder(51, 500000, unemployed));
        lExchange.computeExchange();
    }

    private final List<Population> unemployed1 = new ArrayList<>();

    public final static List<Population> unemployed = new ArrayList<>();

    private void createConsuming(Province province) {
        var exchange = dataStorage.getExchanges().get(province);
        var globalExchange = dataStorage.getExchanges().get(dataStorage);
        province.getPopulationGroups().forEach(pg -> {
            pg.getPopulation().clear();
            pg.setExpenses(0);
        });
        if (province.getPopulationGroups().isEmpty()) {
            province.getPopulationGroups().add(createGroup(province, 0));
            province.getPopulationGroups().add(createGroup(province, 50));
        }
        province.getRegions()
                .stream().flatMap(re -> re.getPopulation().stream())
                .forEach(p -> {
                    int i = 0;
                    while (i < province.getPopulationGroups().size()) {
                        if (p.getPayment() < province.getPopulationGroups().get(i).getBaseIncome()) {
                            province.getPopulationGroups().get(i - 1).getPopulation().add(p);
                            return;
                        }
                        i++;
                    }
                    i -= 1;
                    int ii = province.getPopulationGroups().get(province.getPopulationGroups().size() - 1).getBaseIncome();
                    while (p.getPayment() >= ii) {
                        ii *= 1.5;
                        ii += 50;
                        province.getPopulationGroups().add(
                                createGroup(province, ii));
                        i++;
                    }
                    province.getPopulationGroups().get(province.getPopulationGroups().size() - 2).getPopulation().add(p);
                });
        province.getPopulationGroups().forEach(pg -> {
            if (pg.getBaseIncome() == 0 || pg.getPopulation().size() == 0) {
                return;
            }
            int[] expectedC = new int[peopleConsuming.size()];
            pg.getContracts().removeIf(c -> c.getCount() == 0);
            pg.getContracts().forEach(c -> expectedC[peopleConsumingMapping.get(c.getType())] += c.getCount());
            recalculatePrices(pg, expectedC, province);
//            pg.getContracts().forEach(c -> {
//                int i = peopleConsumingMapping.get(c.getType());
//                if (pg.getPrice()[i] * 14 / 10 < c.getPrice()) {
//                    expectedC[i] -= c.getCount();
//                    c.setCount(0);
//                }
//            });
            for (int i = 0; i < peopleConsuming.size(); i++) {
                clearContractsAndCreateBuy(pg.getContracts(), exchange,
                        new ExchangeBuyOrder(peopleConsuming.get(i), 1, pg.getPrice()[i],
                                0,
                                new ContractSide(pg.getStorage(), inc -> pg.setExpenses(pg.getExpenses() + inc)),
                                pg.getContracts()),
                        (int) (pg.getConsuming()[i] * pg.getPopulation().size()),
                        peopleConsuming.get(i)
                );
            }
        });
    }

    private void recalculatePrices(PopulationGroup pg, int[] expectedConsuming, Province province) {
        for (int i = 0; i < peopleConsuming.size(); i++) {
            if (pg.getConsuming()[i] * pg.getPopulation().size() > expectedConsuming[i]) {
                pg.getPrice()[i] *= 12;
                pg.getPrice()[i] /= 10;
                pg.getPrice()[i] += 1;
            }
            pg.getConsuming()[i] = 0;
        }
        int sum = pg.getBaseIncome();
        int breakLine = sum / 50;
        int i = 0;
        while (i < peopleConsuming.size()) {
            double old = pg.getConsuming()[i];
            pg.getConsuming()[i] += peopleConsuming.get(i).getValue() * province.getConsuming()[i];
            sum -= (pg.getConsuming()[i] - old) * pg.getPrice()[i];
            i++;
            if (sum < breakLine) {
                pg.getConsuming()[i] -= peopleConsuming.get(i).getValue() * province.getConsuming()[i];
                reducePrices(pg, expectedConsuming);
                return;
            }
        }
        i = 0;
        while (i < peopleConsuming.size()) {
            double old = pg.getConsuming()[i];
            pg.getConsuming()[i] += peopleConsuming.get(i).getValue();
            sum -= (pg.getConsuming()[i] - old) * pg.getPrice()[i];
            if (sum < breakLine) {
                pg.getConsuming()[i] -= peopleConsuming.get(i).getValue();
                reducePrices(pg, expectedConsuming);
                return;
            }
            i++;
        }
        double k = (((double) sum) / (1 + pg.getBaseIncome())) + 1;
        if (k <= 1.1) {
            return;
        }
        for (int j = 0; j < peopleConsuming.size(); j++) {
            pg.getPrice()[j] *= k;
        }
    }

    private void reducePrices(PopulationGroup pg, int[] expectedConsuming) {
        for (int i = 0; i < peopleConsuming.size(); i++) {
            if (pg.getConsuming()[i] <= 0.9 * expectedConsuming[i]) {
                pg.getPrice()[i] *= 8;
                pg.getPrice()[i] /= 10;
            }
        }
    }

    private void clearContractsAndCreateNew(List<Contract> contracts, Exchange exchange,
                                            ExchangeSellOrder order, int maxProductionSize) {
        contracts.removeIf(c -> c.getCount() <= 0);
        int expected = contracts.stream()
                .map(Contract::getCount)
                .reduce(0, Integer::sum);
        int diff = expected - maxProductionSize;
        if (diff > 0) {
            while (diff > 0 && !contracts.isEmpty()) {
                var c = contracts.remove(0);
                diff -= c.getCount();
                c.setCount(0);
            }
        } else {
            diff *= -1;
            order.setCount(diff);
            exchange.sell(order);
        }
    }

    private void clearContractsAndCreateBuy(List<Contract> contracts, Exchange exchange,
                                            ExchangeBuyOrder order, int maxRequiredSize, TradeGoodType goodType) {
        contracts.removeIf(c -> c.getCount() <= 0);
        int expected = contracts.stream()
                .filter(c -> c.getType() == goodType)
                .map(Contract::getCount)
                .reduce(0, Integer::sum);
        int diff = expected - Math.max(maxRequiredSize, 0);
        if (diff > 0) {
            int i = 0;
            while (diff > 0 && !contracts.isEmpty()) {
                while (i < contracts.size() && contracts.get(i).getType() != goodType) {
                    i++;
                }
                var c = contracts.remove(i);
                diff -= c.getCount();
                c.setCount(0);
            }
        }
        if (diff < 0) {
            diff *= -1;
            order.setCount(diff);
            exchange.buy(order);
        }
    }

    private final List<Contract> newContracts = new ArrayList<>();

    private void makeContracts() {
        newContracts.clear();
        dataStorage.getCountryData().values().forEach(c -> {
            c.getProvinces().stream().map(p -> dataStorage.getExchanges().get(p)).forEach(e -> newContracts.addAll(e.computeExchange()));
            newContracts.addAll(dataStorage.getExchanges().get(c).computeExchange());
        });
        newContracts.addAll(dataStorage.getExchanges().get(dataStorage).computeExchange());
    }

    @Override
    public void repeat(TimelineEventLoop loop, LocalDate localDate) {
        this.date = localDate.plus(35, ChronoUnit.DAYS);
        loop.putEvent(this);
    }
}
