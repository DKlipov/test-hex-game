package org.openjfx.map.economy.production;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openjfx.map.economy.production.template.BaseProductionTemplate;

@Data
@AllArgsConstructor
public class ProductionLine {
    private BaseProductionTemplate template;
    private double effective;
    private int quality;
    private int[] inputsQuality;
    private int size;
}
