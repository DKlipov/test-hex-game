package org.openjfx.map;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class Population {
    private Nation nation;
    private int budget = 1000;
    private int payment = 0;
    private Collection<Population> workplace;

    public Population(Nation nation) {
        this.nation = nation;
    }

    public void setPayment(int payment) {
        budget += payment;
        this.payment = payment;
    }
}
