package org.openjfx.map.economy.production;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openjfx.map.economy.Company;
import org.openjfx.map.economy.production.template.FactoryType;

import java.util.List;

@Data
@AllArgsConstructor
public class Factory {
    private final FactoryType factoryType;

    private int size;

    private Company owner;

    private List<ProductionLine> lines;

    private int income;

    private int employee;
}
