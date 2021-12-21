package org.openjfx.map.economy.production;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openjfx.map.economy.Company;
import org.openjfx.map.economy.production.template.FactoryType;
import org.openjfx.map.economy.production.template.ResourceGatheringType;

import java.util.List;

@Data
@AllArgsConstructor
public class ResourceGathering {
    private final ResourceGatheringType type;

    private int size;

    private Company owner;

    private int income;

    private int employee;
}
