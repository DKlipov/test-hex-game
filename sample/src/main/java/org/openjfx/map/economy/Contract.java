package org.openjfx.map.economy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.openjfx.map.economy.production.template.TradeGoodType;

@Getter
@Setter
@AllArgsConstructor
public class Contract {
    private ContractSide source;
    private ContractSide target;
    private TradeGoodType type;
    private int quality;
    private int price;
    private int count;
}
