package fr.ferret.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

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


