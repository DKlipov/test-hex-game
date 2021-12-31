package org.openjfx.map.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openjfx.map.economy.trade.Storage;

import java.util.function.Consumer;

@AllArgsConstructor
@Getter
@Setter
public class ContractSide {
    private final Storage storage;
    private final Consumer<Integer> consumer;
}
