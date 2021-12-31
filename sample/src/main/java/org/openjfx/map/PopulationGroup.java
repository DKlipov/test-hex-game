package org.openjfx.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.trade.Storage;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PopulationGroup {
    private final List<Population> population = new ArrayList<>();
    private int baseIncome;
    private int expenses;
    private int[] consuming;
    private int[] price;
    private final Storage storage = new Storage();
    private final List<Contract> contracts = new ArrayList<>();
}
