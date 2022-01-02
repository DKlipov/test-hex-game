package org.openjfx.map.economy.production.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString(of = "id")
public class BaseProductionTemplate {
    private String id;
    private TradeGoodType output;
    private List<TradeGoodType> inputs;
    private final Map<TradeGoodType, Integer> inputsMapping=null;

//    @JsonCreator
//    public BaseProductionTemplate(@JsonProperty("id") String id,
//                                  @JsonProperty("output") TradeGoodType output,
//                                  @JsonProperty("inputs") List<TradeGoodType> inputs) {
//        this.id = id;
//        this.output = output;
//        this.inputs = inputs;
//        this.inputsMapping = new HashMap<>();
//        for (int i = 0; i < inputs.size(); i++) {
//            inputsMapping.put(inputs.get(i), i);
//        }
//    }
}
