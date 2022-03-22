package fr.ferret.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * A phase of the 1KG project. <br>
 * - <i>name</i> is used to get the associated information like the vcf url (so it must be exact).
 * <br>- <i>display</i> is the string representation (used in the interface).
 */
@ConfigSerializable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Phase1KG {

    private String name;
    private String display;

    @Override
    public String toString() {
        return name;
    }

    public String display() {
        return display;
    }
}


