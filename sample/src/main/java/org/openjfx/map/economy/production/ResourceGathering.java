package org.openjfx.map.economy.production;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openjfx.map.Population;
import org.openjfx.map.economy.Company;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.production.template.FactoryType;
import org.openjfx.map.economy.production.template.ResourceGatheringType;
import org.openjfx.map.economy.trade.Storage;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ResourceGathering {
    private final ResourceGatheringType type;

    private int size;

    private double effective;

    private Company owner;

    private int income;

    private final List<Population> employee = new ArrayList<>();

    private int payment;

    private int price;

    private final Storage storage = new Storage();
    private final List<Contract> contracts = new ArrayList<>();

    public int getMaxProduction() {
        return (int) (getSize() * getEffective() * 100);
    }
}
