package org.openjfx.map.economy.production;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.production.template.BaseProductionTemplate;
import org.openjfx.map.economy.trade.Storage;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProductionLine {
    private BaseProductionTemplate template;
    private double effective;
    private int quality;
    private int[] inputsQuality;
    private int[] inputsQuantity;
    private int[] inputsPrice;
    private int size;
    private int price;
    private final List<Contract> outputContracts = new ArrayList<>();
    private final Storage outputStorage = new Storage();
    private final List<Contract> inputContracts = new ArrayList<>();
    private final Storage inputStorage = new Storage();
    private double workload;

    public int getMaxProduction() {
        return (int) (100 * getSize() * getEffective());
    }
}
