package fr.ferret.model.conversions;

import javax.annotation.Nonnull;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single record line in the `pedigrees.txt` file.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedigree {
    @CsvBindByName(column = "Family ID", required = true)
    private String familyId;

    @CsvBindByName(column = "Individual ID", required = true)
    private String individualId;

    @Builder.Default
    @CsvBindByName(column = "Paternal ID", required = true)
    @Nonnull
    private String paternalId = "0";

    @Builder.Default
    @CsvBindByName(column = "Maternal ID", required = true)
    @Nonnull
    private String maternalId = "0";

    @Builder.Default
    @CsvBindByName(column = "Gender", required = true)
    private int gender = 0;

    @Builder.Default
    @CsvBindByName(column = "Phenotype", required = true)
    private int phenotype = 0;

    @Builder.Default
    @CsvBindByName(column = "Population", required = true)
    @Nonnull
    private String population = "0";

    @Builder.Default
    @CsvBindByName(column = "Relationship", required = true)
    @Nonnull
    private String relationship = "0";

    @Builder.Default
    @CsvBindByName(column = "Siblings", required = true)
    @Nonnull
    private String siblings = "0";

    @Builder.Default
    @CsvBindByName(column = "Second Order", required = true)
    @Nonnull
    private String secondOrder = "0";

    @Builder.Default
    @CsvBindByName(column = "Third Order", required = true)
    @Nonnull
    private String thirdOrder = "0";

    @Builder.Default
    @CsvBindByName(column = "Children", required = true)
    @Nonnull
    private String children = "0";

    @Builder.Default
    @CsvBindByName(column = "Other Comments")
    @Nonnull
    private String otherComments = "0";
}
