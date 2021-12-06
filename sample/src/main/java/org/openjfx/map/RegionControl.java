package org.openjfx.map;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RegionControl {
    private int x;
    private int y;
    private Country country;
}
