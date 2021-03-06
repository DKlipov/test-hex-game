package org.openjfx.map.economy.production.template;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(of = "id")
public class TradeGoodType {
    private String id;
    private int priority;
    private double value;
    private List<TradeGoodGroup> groups;
}
