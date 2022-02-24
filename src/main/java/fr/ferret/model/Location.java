package fr.ferret.model;

public class Location {
    public String annotationRelease;
    public String assemblyaccver;
    public String chraccver;
    public int chrstart;
    public int chrstop;

    @Override public String toString() {
        return "Location{" + "annotationRelease='" + annotationRelease + '\'' + ", assemblyaccver='"
            + assemblyaccver + '\'' + ", chraccver='" + chraccver + '\'' + ", chrstart=" + chrstart
            + ", chrstop=" + chrstop + '}';
    }
}
