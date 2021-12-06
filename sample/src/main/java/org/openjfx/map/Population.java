package org.openjfx.map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Population {
    private int count;

    private int avgEducation;

    private double females;

    private Nation nation;

    private double children;


}
