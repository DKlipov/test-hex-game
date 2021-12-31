package org.openjfx.map.economy.trade;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openjfx.map.RegionControl;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Exchange {
    private RegionControl place;
    private Exchange parent;
    private final List<ExchangeBuyOrder> buyOrders = new ArrayList<>();
    private final List<ExchangeSellOrder> sellOrders = new ArrayList<>();
}
