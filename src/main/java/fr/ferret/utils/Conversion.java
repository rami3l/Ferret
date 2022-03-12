package fr.ferret.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Conversion {
    public boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
