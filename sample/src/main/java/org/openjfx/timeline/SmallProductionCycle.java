package org.openjfx.timeline;

import lombok.Getter;
import org.openjfx.map.DataStorage;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.production.NaturalEconomy;
import org.openjfx.map.economy.production.SelfEmployed;
import org.openjfx.map.economy.production.template.SelfEmployedType;
import org.openjfx.map.economy.production.template.TradeGoodType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class SmallProductionCycle implements TimelineEvent {

    private final DataStorage dataStorage;
    @Getter
    private LocalDate date;

    private Map<SelfEmployedType, Integer> selfEmployeePredicted = new HashMap<>();

    public SmallProductionCycle(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        date = LocalDate.parse("0000-01-01");
    }

    @Override
    public void execute() {
        selfEmployeePredicted.clear();
        BigProductionCycle.unemployed.forEach(pop -> pop.setPayment(50));
        dataStorage.getRegionsEconomy()
                .forEach(re -> {
                    re.getGatherings().forEach(g -> {
                        int output = (int) (Math.min(g.getEmployee().size(), g.getSize()) * g.getEffective() * g.getType().getBaseEffectively());
                        g.getStorage().getSet().compute(g.getType().getOutput(), (k, v) -> {
                            if (v == null) {
                                return output;
                            } else {
                                return v + output;
                            }
                        });
                        g.getEmployee().forEach(e -> {
                            e.setPayment(g.getPayment());
                        });
                        g.setIncome(g.getIncome() - (g.getEmployee().size() * g.getPayment()));
                        g.getContracts().forEach(this::computeContract);
                    });
                    computeNative(re.getNaturalEconomy());
                    re.getIndustry().forEach(i -> {
                        double k = 1.0 * i.getEmployee().size() / i.getSize();
                        i.getLines().forEach(g -> {
                            double localK = k;
                            for (int inp = 0; inp < g.getTemplate().getInputs().size(); inp++) {
                                if (g.getInputsQuantity()[inp] <= 0) {
                                    continue;
                                }
                                var tg = g.getTemplate().getInputs().get(inp);
                                double ik = 1.0 * g.getInputStorage().getSet().getOrDefault(tg, 0) / (g.getInputsQuantity()[inp] * g.getMaxProduction());
                                localK = Math.min(localK, ik);
                            }
                            g.getInputStorage().getSet().clear();
                            int output = (int) (localK * g.getMaxProduction());
                            g.getOutputStorage().getSet().compute(g.getTemplate().getOutput(), (key, v) -> {
                                if (v == null) {
                                    return output;
                                } else {
                                    return v + output;
                                }
                            });
                            g.getOutputContracts().forEach(this::computeContract);
                        });

                        i.getEmployee().forEach(e -> {
                            e.setPayment(i.getPayment());
                        });
                        i.setIncome(i.getIncome() - (i.getEmployee().size() * i.getPayment()));
                    });
                });
        dataStorage.getCountryData().values().stream().flatMap(c -> c.getProvinces().stream())
                .flatMap(p -> p.getSelfEmployed().stream()).forEach(this::computeSelfEmployee);
        outResults();
    }

    private void outResults() {
        TreeSet<String> set = new TreeSet<>(Comparator.naturalOrder());
        set.addAll(sent.entrySet().stream()
                .map(e -> e.getKey().getId() + ": " + e.getValue()).collect(Collectors.toSet()));
        System.out.println("At this week produced and delivered: ");
        set.forEach(s -> System.out.println('\t' + s));
        sent.clear();
    }

    private final Map<TradeGoodType, Integer> sent = new HashMap<>();

    private int predictSelfEmployee(SelfEmployedType type, Integer v) {
        if (v != null) {
            return v;
        }
        var ge = dataStorage.getExchanges().get(dataStorage);
        int inputPrice = ge.getPrice(type.getInput(), 1) + 1;
        int outputPrice = ge.getPrice(type.getOutput(), 1);
        return (outputPrice / inputPrice) * type.getEffectivency();
    }

    private void computeSelfEmployee(SelfEmployed se) {
        int delivered = se.getInputStorage().getSet().getOrDefault(se.getType().getInput(), 0);
        if (se.getProduction() <= 0) {

            se.setPayment(selfEmployeePredicted.compute(se.getType(), this::predictSelfEmployee));
            return;
        }
        int output = Math.min(delivered, se.getProduction());
        se.getOutputStorage().getSet().compute(se.getType().getOutput(), (k, v) -> {
            if (v == null) {
                return output;
            } else {
                return v + output;
            }
        });
        int payment = se.getIncome() / se.getPopulation().size();
        se.setPayment(payment);
        se.getPopulation().forEach(e -> e.setPayment(se.getPayment()));
        se.setIncome(0);
        se.getOutputContracts().forEach(this::computeContract);
    }

    private void computeNative(NaturalEconomy naturalEconomy) {
        int output = naturalEconomy.getProduction();
        if (output <= 0) {
            naturalEconomy.setPayment(dataStorage.getExchanges().get(dataStorage).getPrice(naturalEconomy.getTradeGoodType(), 1) * naturalEconomy.getEffectivency());
            return;
        }
        naturalEconomy.getStorage().getSet().compute(naturalEconomy.getTradeGoodType(), (k, v) -> {
            if (v == null) {
                return output;
            } else {
                return v + output;
            }
        });
        int payment = naturalEconomy.getIncome() / naturalEconomy.getPopulation().size();
        naturalEconomy.setPayment(payment);
        naturalEconomy.getPopulation().forEach(e -> e.setPayment(naturalEconomy.getPayment()));
        naturalEconomy.setIncome(0);
        naturalEconomy.getContracts().forEach(this::computeContract);
    }

    private void computeContract(Contract contract) {
        var available = contract.getSource().getStorage().getSet().getOrDefault(contract.getType(), 0);
        if (available <= 0) {
            return;
        }
        var leaved = available - contract.getCount();
        var extracted = available > contract.getCount() ? contract.getCount() : available;
        if (leaved <= 0) {
            leaved = 0;
        }
        contract.getSource().getStorage().getSet().put(contract.getType(), leaved);
        contract.getSource().getConsumer().accept(extracted * contract.getPrice());
        contract.getTarget().getConsumer().accept(-1 * extracted * contract.getPrice());
        contract.getTarget().getStorage().getSet().compute(contract.getType(), (k, v) -> {
            if (v == null) {
                return extracted;
            }
            return v + extracted;
        });
        sent.compute(contract.getType(), (k, v) -> {
            if (v == null) {
                return extracted;
            }
            return v + extracted;
        });
    }

    @Override
    public void repeat(TimelineEventLoop loop, LocalDate localDate) {
        this.date = localDate.plus(7, ChronoUnit.DAYS);
        loop.putEvent(this);
    }
}
