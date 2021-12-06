package org.openjfx.map;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Region {
    private int id;
    private List<Population> population;

}
