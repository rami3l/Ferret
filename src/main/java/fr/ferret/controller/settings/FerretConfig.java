package fr.ferret.controller.settings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import fr.ferret.model.hgversion.HgVersion;
import org.apache.commons.lang3.SystemUtils;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The global config of Ferret
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
public class FerretConfig {
    /**
     * Version du projet 1KG utilisée
     */
    @Builder.Default
    private String selectedPhase = "phase3";

    /**
     * Selected output file type
     */
    @Builder.Default
    private FileOutputType selectedOutputType = FileOutputType.ALL;

    /**
     * Selected gene version
     */
    @Builder.Default
    private HumanGenomeVersions selectedHumanGenome = HumanGenomeVersions.HG19;

    /**
     * The Minor Allele Frequency
     */
    @Builder.Default
    private double mafThreshold = 0;

    /**
     * The correspondence between the HG versions and the latest assembly accession versions
     */
    @Builder.Default
    Map<HumanGenomeVersions, Integer> assemblyAccessVersions = new EnumMap<>(
            Map.of(HumanGenomeVersions.HG19, 25, HumanGenomeVersions.HG38, 39));

    public void updateAssemblyAccessVersions(List<HumanGenomeVersions> versions) {
        var assAccVersions = HgVersion.getLatestAssemblyAccessVersions(versions);
        assAccVersions.forEach(
            (version, release) -> release.ifPresent(r -> assemblyAccessVersions.put(version, r))
        );
    }

    public int getAssemblyAccessVersion() {
        return assemblyAccessVersions.get(selectedHumanGenome);
    }


    public static final Path DEFAULT_DIR = Paths.get(SystemUtils.USER_HOME, ".config", "ferret");
    public static final String DEFAULT_FILENAME = "ferret.conf";
    public static final Path DEFAULT_FILEPATH = DEFAULT_DIR.resolve(DEFAULT_FILENAME);

    public Path save() throws ConfigurateException {
        return save(DEFAULT_FILEPATH);
    }

    public Path save(Path toPath) throws ConfigurateException {
        var loader = HoconConfigurationLoader.builder().path(toPath).build();
        var node = loader.createNode().set(FerretConfig.class, this);
        loader.save(node);
        return toPath;
    }

    public static FerretConfig load() throws ConfigurateException {
        return load(DEFAULT_FILEPATH);
    }

    public static FerretConfig load(Path fromPath) throws ConfigurateException {
        var loader = HoconConfigurationLoader.builder().path(fromPath).build();
        return loader.load().get(FerretConfig.class);
    }
}
