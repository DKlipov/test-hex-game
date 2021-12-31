package org.openjfx.map.economy.production;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openjfx.map.Population;
import org.openjfx.map.economy.Company;
import org.openjfx.map.economy.production.template.FactoryType;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Factory {
    private final FactoryType factoryType;

    private int size;

    private Company owner;

    private List<ProductionLine> lines;

    private int income;

    private final List<Population> employee = new ArrayList<>();
    private int payment;
}
