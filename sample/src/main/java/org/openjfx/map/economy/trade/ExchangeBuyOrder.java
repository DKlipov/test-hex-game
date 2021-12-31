package org.openjfx.map.economy.trade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.openjfx.map.economy.Contract;
import org.openjfx.map.economy.ContractSide;
import org.openjfx.map.economy.production.template.TradeGoodType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ExchangeBuyOrder {
    private TradeGoodType type;
    private int quality;
    private int price;
    private int count;
    private ContractSide storage;
    private List<Contract> contracts;
}
