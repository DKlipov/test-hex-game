package org.openjfx.timeline;

import lombok.Getter;
import org.openjfx.map.DataStorage;
import org.openjfx.map.Population;
import org.openjfx.map.economy.production.Factory;
import org.openjfx.map.economy.production.ResourceGathering;
import org.openjfx.map.economy.trade.Exchange;
import org.openjfx.map.economy.trade.LaborExchange;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PlanningProductionCycle implements TimelineEvent {

    private final DataStorage dataStorage;
    @Getter
    private LocalDate date;

    private final int PLANNING_PERIODS = 2;

    public PlanningProductionCycle(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        date = LocalDate.parse("0000-01-01");
    }

    @Override
    public void execute() {
        dataStorage.getRegionsEconomy().forEach(re -> {
            var lExchange = dataStorage.getLaborExchanges().get(re.getRegion());
            var globalExchange = dataStorage.getExchanges().get(dataStorage);
            re.getGatherings().forEach(g -> computeGathering(g, lExchange, globalExchange));
            re.getIndustry().forEach(i -> computeIndastry(i, lExchange, globalExchange));
            re.getGatherings().removeIf(g -> g.getSize() <= 0);
            re.getIndustry().removeIf(g -> g.getSize() <= 0);
        });
    }

    private int computePayment(int basePayment, LaborExchange lExchange, int size, Collection<Population> employee) {
        if (size > employee.size()) {
            int payment = lExchange.getMinimalWage() * 11 / 10;
            if (basePayment > payment) {
                return (basePayment * 11 / 10);
            } else {
                return payment;
            }
        }
        return basePayment;
    }

    private int computePrice(double sold, int exchangePrice, int basePrice) {
        if (sold > 0.9) {
            if (exchangePrice > basePrice) {
                return (exchangePrice * 11 / 10);
            } else {
                return (basePrice * 11 / 10);
            }
        } else if (sold < 0.8) {
            if (exchangePrice > basePrice) {
                return (basePrice * 8 / 10);
            } else {
                return (exchangePrice * 9 / 10);
            }
        }
        return basePrice;
    }

    private void computeIndastry(Factory g, LaborExchange lExchange, Exchange globalExchange) {
        g.setPayment(computePayment(g.getPayment(), lExchange, g.getSize(), g.getEmployee()));
        int commonExpenses = g.getPayment() * g.getSize() / g.getSize();
        double[] expectedIncomeArr = new double[g.getLines().size()];
        double expectedIncomeTotal = 0;
        int expensesTotal = 0;
        int soldCandidat = -1;
        for (int i = 0; i < g.getLines().size(); i++) {
            var l = g.getLines().get(i);
            double sold = l.getOutputContracts().stream().mapToDouble(c -> c.getCount()).sum() / l.getMaxProduction();
            var exchangePrice = globalExchange.getPrice(l.getTemplate().getOutput(), l.getQuality());
            l.setPrice(computePrice(sold, exchangePrice, l.getPrice()));
            int expenses = commonExpenses * l.getSize();
            for (int j = 0; j < l.getTemplate().getInputs().size(); j++) {
                expenses += l.getInputsQuantity()[j] * l.getInputsPrice()[j] * l.getSize();
            }
            expensesTotal += expenses;
            expectedIncomeArr[i] = ((double) l.getMaxProduction() * l.getPrice()) / expenses;
            if (sold > 0.9 && (soldCandidat < 0 || expectedIncomeArr[i] > expectedIncomeArr[soldCandidat])) {
                soldCandidat = i;
            }
            int[] bought = new int[l.getTemplate().getInputs().size()];
            l.getInputContracts().forEach(ic -> {
                bought[l.getTemplate().getInputsMapping().get(ic.getType())] += ic.getCount();
            });
            for (int j = 0; j < l.getTemplate().getInputs().size(); j++) {
                int inputExchangePrice = globalExchange.getPrice(l.getTemplate().getInputs().get(i), l.getInputsQuality()[i]);
                if (bought[j] >= l.getInputsQuantity()[j] * l.getSize() && expectedIncomeArr[i] < 1.3) {
                    if (inputExchangePrice < l.getInputsPrice()[i]) {
                        l.getInputsPrice()[i] = inputExchangePrice * 9 / 10;
                    } else {
                        l.getInputsPrice()[i] = l.getInputsPrice()[i] * 9 / 10;
                    }
                } else if (bought[j] < l.getInputsQuantity()[j]) {
                    if (inputExchangePrice < l.getInputsPrice()[i]) {
                        l.getInputsPrice()[i] = l.getInputsPrice()[i] * 12 / 10;
                    } else {
                        l.getInputsPrice()[i] = inputExchangePrice * 11 / 10;
                    }
                }
            }
            expectedIncomeTotal += expectedIncomeArr[i] * l.getSize();
        }
        expectedIncomeTotal /= g.getSize();
        if (expectedIncomeTotal < 1.3) {
            boolean reduced = false;
            if (g.getSize() == g.getEmployee().size()) {
                g.setPayment(g.getPayment() * 8 / 10);
                reduced = true;
            }
            if (!reduced && expectedIncomeTotal < 1 && g.getIncome() < expensesTotal * PLANNING_PERIODS) {
                g.setIncome(expensesTotal * PLANNING_PERIODS);
                int lower = 0;
                for (int i = 0; i < expectedIncomeArr.length; i++) {
                    if (expectedIncomeArr[i] < expectedIncomeArr[lower]) {
                        lower = i;
                    }
                }
                var line = g.getLines().get(lower);
                int sizeChange = (line.getSize() * 3 / 10) + 1;
                g.setSize(g.getSize() - sizeChange);
                line.setSize(line.getSize() - sizeChange);
                if (line.getSize() <= 0) {
                    g.getLines().remove(line);
                }
                if (g.getSize() <= 0) {
                    g.getEmployee().forEach(pop -> {
                        pop.setWorkplace(null);
                        pop.setPayment(50);
                    });
                }
            }
        } else if (expectedIncomeTotal > 1.5 && g.getIncome() > 1000 && soldCandidat > 0) {
            var line = g.getLines().get(soldCandidat);
            line.setSize(line.getSize() + 1);
            g.setSize(g.getSize() + 1);
            g.setIncome(g.getIncome() - 1000);
        }
    }

    private void computeGathering(ResourceGathering g, LaborExchange lExchange, Exchange globalExchange) {
        g.setPayment(computePayment(g.getPayment(), lExchange, g.getSize(), g.getEmployee()));

        double sold = g.getContracts().stream().mapToDouble(c -> c.getCount()).sum() / g.getMaxProduction();
        var exchangePrice = globalExchange.getPrice(g.getType().getOutput(), g.getQuality());
        g.setPrice(computePrice(sold, exchangePrice, g.getPrice()));

        int expenses = g.getPayment() * g.getSize();
        double expectedIncome = ((double) g.getMaxProduction() * g.getPrice()) / expenses;
        if (expectedIncome < 1.3) {
            boolean reduced = false;
            if (g.getSize() == g.getEmployee().size()) {
                g.setPayment(g.getPayment() * 8 / 10);
                reduced = true;
            }
            if (!reduced && expectedIncome < 1 && g.getIncome() < expenses * PLANNING_PERIODS) {
                int diff = ((g.getSize() * 3 / 10) + 1);
                g.setSize(g.getSize() - diff);
                g.setIncome(expenses * 3);
                if (g.getSize() <= 0) {
                    g.getEmployee().forEach(pop -> {
                        pop.setWorkplace(null);
                        pop.setPayment(50);
                    });
                }
            }
        } else if (expectedIncome > 1.5 && sold > 0.9 && g.getIncome() > 1000) {
            g.setSize(g.getSize() + 1);
            g.setIncome(g.getIncome() - 1000);
        }
    }

    @Override
    public void repeat(TimelineEventLoop loop, LocalDate localDate) {
        this.date = localDate.plus(35 * PLANNING_PERIODS, ChronoUnit.DAYS);
        loop.putEvent(this);
    }
}
