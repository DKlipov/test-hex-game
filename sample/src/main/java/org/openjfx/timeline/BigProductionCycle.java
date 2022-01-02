package org.openjfx.timeline;

import lombok.Getter;
import org.openjfx.map.DataStorage;
import org.openjfx.map.PopulationGroup;
import org.openjfx.map.Province;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.ContractSide;
import org.openjfx.map.economy.RegionEconomy;
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
                            var exI = expected.get(it);
                            int ex = exI == null ? 0 : exI;
                            if (ex < l.getInputsQuantity()[i]) {
                                exchange.buy(
                                        new ExchangeBuyOrder(it,
                                                l.getInputsQuality()[i], l.getInputsPrice()[i],
                                                (l.getMaxProduction() * l.getInputsQuantity()[i]) - ex,
                                                new ContractSide(l.getInputStorage(), inc -> f.setIncome(f.getIncome() + inc)),
                                                l.getInputContracts()
                                        )
                                );
                            }
                        }
                    }));
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
                if (l.getPopulation().isEmpty()) {
                    return;
                }
                int buyPrice = globalExchange.getPrices().get(l.getType().getOutput()) * 7 / 10;
                if (buyPrice <= 0) {
                    buyPrice = globalExchange.getPrices().get(l.getType().getInput()) * 13 / 10;
                }
                clearContractsAndCreateNew(l.getOutputContracts(), exchange,
                        new ExchangeSellOrder(l.getType().getOutput(),
                                1, buyPrice, 0,
                                new ContractSide(l.getOutputStorage(), i -> l.setIncome(l.getIncome() + i)),
                                l.getOutputContracts()),
                        l.getProduction()
                );

                clearContractsAndCreateBuy(l.getOutputContracts(), exchange,
                        new ExchangeBuyOrder(l.getType().getInput(),
                                1, buyPrice, 0,
                                new ContractSide(l.getInputStorage(), i -> l.setIncome(l.getIncome() + i)),
                                l.getInputContracts()),
                        l.getProduction());

            });
        });
        dataStorage.getCountryData().values().stream().flatMap(c -> c.getProvinces().stream()).forEach(this::createConsuming);
        makeContracts();
        outResults();
    }

    private final List<Contract> allContracts = new ArrayList<>();

    private void outResults() {
        allContracts.removeIf(c -> c.getCount() <= 0);
        System.out.println("New contracts: ");
        reduce(newContracts).forEach(s -> System.out.println('\t' + s));
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
        sell.forEach((key, value) -> {
            System.out.println("\t| " + value + "\t | " + buy.getOrDefault(key, 0) + "\t |\t\t" + key.getId());
            buy.put(key, 0);
        });
        buy.forEach((k, v) -> {
            if (v == 0) {
                return;
            }
            System.out.println("\t| 0  \t | " + v + "\t |\t\t" + k.getId());
        });
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
        int counter = 100;
        for (var pop : pops) {
            if (pop.getWorkplace() == null) {
                pop.setPayment(50);
                continue;
            }
            if (pop.getPayment() >= lExchange.getMinimalWage()) {
                continue;
            }
            if (counter < 5) {
                counter++;
                continue;
            }
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
        lExchange.computeExchange();
    }

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
            pg.getContracts().forEach(c -> {
                int i = peopleConsumingMapping.get(c.getType());
                if (pg.getPrice()[i] * 14 / 10 < c.getPrice()) {
                    expectedC[i] -= c.getCount();
                    c.setCount(0);
                }
            });
            for (int i = 0; i < peopleConsuming.size(); i++) {
                if (pg.getConsuming()[i] * pg.getPopulation().size() * 1.2 > expectedC[i]) {
                    int ci = i;
                    pg.getContracts().removeIf(c -> c.getType() == peopleConsuming.get(ci));
                    expectedC[i] = 0;
                }
                int diff = (int) (pg.getConsuming()[i] * pg.getPopulation().size() - expectedC[i]);
                if (diff > 0 && expectedC[i] == 0) {
                    exchange.buy(new ExchangeBuyOrder(peopleConsuming.get(i), -1, pg.getPrice()[i],
                            diff,
                            new ContractSide(pg.getStorage(), inc -> pg.setExpenses(pg.getExpenses() + inc)),
                            pg.getContracts()));
                } else if (diff > 0) {
                    if (diff > pg.getConsuming()[i] * pg.getPopulation().size() * 2) {
                        pg.getPrice()[i] *= 2;
                    } else {
                        pg.getPrice()[i] *= pg.getConsuming()[i] * pg.getPopulation().size();
                        pg.getPrice()[i] /= diff;
                        exchange.buy(new ExchangeBuyOrder(peopleConsuming.get(i), -1, pg.getPrice()[i],
                                diff,
                                new ContractSide(pg.getStorage(), inc -> pg.setExpenses(pg.getExpenses() + inc)),
                                pg.getContracts()));
                    }
                }
            }
        });
    }

    private void recalculatePrices(PopulationGroup pg, int[] expectedConsuming, Province province) {
        for (int i = 0; i < peopleConsuming.size(); i++) {
            if (pg.getConsuming()[i] > expectedConsuming[i]) {
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
                return;
            }
        }
        i = 0;
        while (i < peopleConsuming.size()) {
            double old = pg.getConsuming()[i];
            pg.getConsuming()[i] += peopleConsuming.get(i).getValue();
            sum -= (pg.getConsuming()[i] - old) * pg.getPrice()[i];
            i++;
            if (sum < breakLine) {
                return;
            }
        }
        var k = (sum / (1 + pg.getBaseIncome())) + 1;
        if (k <= 1) {
            return;
        }
        for (int j = 0; j < peopleConsuming.size(); j++) {
            pg.getPrice()[j] *= k;
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
                                            ExchangeBuyOrder order, int maxRequiredSize) {
        contracts.removeIf(c -> c.getCount() <= 0);
        int expected = contracts.stream()
                .map(Contract::getCount)
                .reduce(0, Integer::sum);
        int diff = expected - maxRequiredSize;
        if (diff > 0) {
            while (diff > 0 && !contracts.isEmpty()) {
                var c = contracts.remove(0);
                diff -= c.getCount();
                c.setCount(0);
            }
        } else {
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
