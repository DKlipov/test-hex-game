package org.openjfx.map.economy.trade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeStats {
    private int price;
    private int volume;
    private int extraDemand;
}
