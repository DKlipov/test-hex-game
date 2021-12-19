package org.openjfx.map.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openjfx.map.Country;
import org.openjfx.map.Province;
import org.openjfx.map.RegionControl;

import java.util.List;

@Data
@AllArgsConstructor
public class CountryData {
    private Country country;
    private RegionControl capital;
    private List<Province> provinces;
}
