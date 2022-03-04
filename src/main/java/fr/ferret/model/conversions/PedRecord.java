package fr.ferret.model.conversions;

import java.util.List;
import java.util.stream.Stream;

public record PedRecord(String familyId, String individualId, String paternalId, String maternalId,
        int sex, Number phenotype, List<GenotypePair> variants) {

    /** The variant info in a .ped file starts from the 6th column. */
    public static final int VARIANT_OFFSET = 6;

    public PedRecord(Pedigree pedigree, List<GenotypePair> variants) {
        this(pedigree.getFamilyId(), pedigree.getIndividualId(), pedigree.getPaternalId(),
                pedigree.getMaternalId(), pedigree.getGender(), pedigree.getPhenotype(), variants);
    }

    public List<String> toListStrings() {
        return Stream.concat(
                List.of(familyId, individualId, paternalId, maternalId, Integer.toString(sex),
                        phenotype.toString()).stream(),
                variants.stream().flatMap(pair -> pair.toList().stream())).toList();
    }

    public String toString() {
        return String.join("\t", toListStrings());
    }
}
