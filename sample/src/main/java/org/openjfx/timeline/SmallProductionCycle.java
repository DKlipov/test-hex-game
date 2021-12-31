package org.openjfx.timeline;

import lombok.Getter;
import org.openjfx.map.DataStorage;
import org.openjfx.map.Population;
import org.openjfx.map.RegionControl;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.production.NativeEmployee;
import org.openjfx.map.economy.production.template.TradeGoodGroup;
import org.openjfx.map.economy.production.template.TradeGoodType;
import org.openjfx.utils.CellUtils;

import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class SmallProductionCycle implements TimelineEvent {

    private final DataStorage dataStorage;
    @Getter
    private LocalDate date;

    public SmallProductionCycle(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        date = LocalDate.parse("0000-01-01");
    }

    @Override
    public void execute() {
        dataStorage.getRegionsEconomy()
                .forEach(re -> {
                    re.getGatherings().forEach(g -> {
                        int output = Math.min(g.getEmployee().size() / g.getSize(), 1) * g.getMaxProduction();
                        g.getStorage().getSet().compute(g.getType().getOutput(), (k, v) -> {
                            if (v == null) {
                                return output;
                            } else {
                                return v + output;
                            }
                        });
                        g.getEmployee().forEach(e -> {
                            e.setBudget(e.getBudget() + g.getPayment());
                            e.setPayment(g.getPayment());
                        });
                        g.setIncome(g.getIncome() - (g.getEmployee().size() * g.getPayment()));
                        g.getContracts().forEach(this::computeContract);
                    });
                    computeNative(re.getNativeEmployee());
                    re.getIndustry().forEach(i -> {
                        double k = i.getEmployee().size() / i.getSize();
                        i.getLines().forEach(g -> {
                            double localK = k;
                            for (int inp = 0; inp < g.getTemplate().getInputs().size(); inp++) {
                                if (g.getInputsQuantity()[inp] <= 0) {
                                    continue;
                                }
                                var tg = g.getTemplate().getInputs().get(inp);
                                double ik = g.getInputStorage().getSet().getOrDefault(tg, 0) / g.getInputsQuantity()[inp];
                                localK = Math.min(localK, ik);
                            }
                            int output = (int) (k * g.getMaxProduction());
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
                            e.setBudget(e.getBudget() + i.getPayment());
                            e.setPayment(i.getPayment());
                        });
                        i.setIncome(i.getIncome() - (i.getEmployee().size() * i.getPayment()));
                    });
                });
        outResults();
    }

    private void outResults() {
        TreeSet<String> set = new TreeSet<>(Comparator.naturalOrder());
        set.addAll(sent.entrySet().stream()
                .map(e -> e.getKey().getId() + ": " + e.getValue()).collect(Collectors.toSet()));
        System.out.println("At this week produced and delivered: ");
        set.forEach(System.out::println);
        sent.clear();
    }

    private final Map<TradeGoodType, Integer> sent = new HashMap<>();

    private void computeNative(NativeEmployee nativeEmployee) {
        int output = Math.min(nativeEmployee.getSize(), nativeEmployee.getPopulation().size()) * 2;
        if (output <= 0) {
            return;
        }
        nativeEmployee.getStorage().getSet().compute(nativeEmployee.getTradeGoodType(), (k, v) -> {
            if (v == null) {
                return output;
            } else {
                return v + output;
            }
        });
        int payment = nativeEmployee.getIncome() / nativeEmployee.getPopulation().size();
        nativeEmployee.getPopulation().forEach(e -> {
            e.setBudget(e.getBudget() + payment);
            e.setPayment(payment);
        });
        nativeEmployee.setIncome(0);
        nativeEmployee.getContracts().forEach(this::computeContract);
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
