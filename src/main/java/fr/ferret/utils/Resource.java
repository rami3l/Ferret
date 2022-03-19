package fr.ferret.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import com.opencsv.bean.CsvToBeanBuilder;
import fr.ferret.controller.settings.FerretConfig;
import fr.ferret.controller.settings.HumanGenomeVersions;
import fr.ferret.model.Region;
import fr.ferret.model.conversions.Pedigree;
import lombok.experimental.UtilityClass;
import org.spongepowered.configurate.ConfigurateException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Utility class to deal with resource files
 */
@UtilityClass
public class Resource {

    private static final Logger logger = Logger.getLogger(Resource.class.getName());

    /** program settings */
    private static FerretConfig config = new FerretConfig();

    /** text elements for the interface */
    private static final ResourceBundle textElements =
            ResourceBundle.getBundle("ferret", Locale.getDefault());

    /** application configuration */
    private static final ResourceBundle serverConfig = ResourceBundle.getBundle("server");

    public static final String ASS_ACC_VERSION_PREFIX = "GCF_000001405";

    public static final Color TITLE_COLOR = new Color(12, 28, 134);
    public static final Color ZONE_LABEL_COLOR = new Color(131, 55, 192);
    public static final Color PANEL_BORDER_COLOR = new Color(131, 55, 192, 140);
    public static final Color BUTTON_COLOR = new Color(201, 157, 240);
    public static final Color LINK_STANDARD_COLOR = new Color(40, 100, 255);
    public static final Color LINK_HOVER_COLOR = new Color(255, 0, 0);
    public static final Color LINK_ACTIVE_COLOR = new Color(128, 0, 128);

    public static final Font TITLE_FONT = new Font("Calibri", Font.BOLD, 24);
    public static final Font ZONE_LABEL_FONT = new Font("Calibri", Font.BOLD, 20);
    public static final Font SETTINGS_LABEL_FONT = new Font("SansSerif", Font.BOLD, 16);

    public static FerretConfig config() {
        return config;
    }

    //TODO: !!! Si éléments de config incorrects dans le fichier sur le disque (ex la phase), garder la valeur par défaut !!!
    public static void loadConfig() {
        try {
            config = FerretConfig.load();
        } catch (ConfigurateException e) {
            logger.log(Level.INFO, "Impossible to load config. Using default one", e);
        }
    }

    public static void saveConfig() {
        try {
            Resource.config().save();
            logger.info("Config saved");
        } catch (ConfigurateException e) {
            logger.log(Level.WARNING, "Impossible to save config", e);
        }
    }

    public static void updateAssemblyAccessVersions() {
        var versions = List.of(HumanGenomeVersions.HG19, HumanGenomeVersions.HG38);
        Mono.fromRunnable(() -> config.updateAssemblyAccessVersions(versions))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnSubscribe(o -> logger.info("Starting assembly accession versions update"))
            .doOnSuccess(o -> logger.info("Assembly accession versions updated"))
            .doOnError(e -> logger.log(Level.WARNING, "Assembly accession versions update failed", e))
            .doOnSuccess(r -> saveConfig())
            .subscribe();
    }

    public static String getAssemblyAccessVersion() {
        return ASS_ACC_VERSION_PREFIX + "." + config.getAssemblyAccessVersion();
    }

    /**
     * @param resourceFileName relative path of the resource image
     * @return an optional image
     */
    public static Optional<BufferedImage> getImage(String resourceFileName) {
        return ResourceFile.getResource(resourceFileName, ImageIO::read);
    }

    /**
     * @param resourceFileName relative path of the resource icon
     * @return an optional icon
     */
    public static Optional<ImageIcon> getIcon(String resourceFileName) {
        return ResourceFile.getResource(resourceFileName, ImageIcon::new);
    }

    public static Optional<ImageIcon> getIcon(String resourceFilename, int width, int height) {
        return getIcon(resourceFilename).map(icon -> new ImageIcon(icon.getImage()
            .getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH)
        ));
    }

    /**
     * Gets an element of text from the resources according to system langage
     *
     * @param element of text to get in the resources
     */
    public static String getTextElement(String element) {
        try {
            return textElements.getString(element);
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Impossible to get text element: %s", element),
                    e);
            return "???";
        }
    }

    public static String getServerConfig(String element) {
        return serverConfig.getString(element);
    }


    /**
     * Returns the mapping from individual ID to a record in the pedigrees file.
     * 
     * Available field names in the record are: "Family ID", "Individual ID", "Paternal ID",
     * "Maternal ID", "Gender", "Phenotype", "Population", "Relationship", "Siblings", "Second
     * Order", "Third Order", "Children", "Other Comments"
     */
    public Map<String, Pedigree> getPedigrees() {
        var pedigreeReader = ResourceFile.getFileReader("samples/pedigrees.txt");
        var parser = new CsvToBeanBuilder<Pedigree>(pedigreeReader)
                .withType(Pedigree.class).withSeparator('\t').build();
        return parser.parse().stream()
                .collect(Collectors.toMap(Pedigree::getIndividualId, Function.identity()));
    }


    public static Set<Region> getSample(String phase) {
        return Region.fromSample(SamplesResource.getSample(phase));
    }

    public static Set<String> getPhases() {
        return SamplesResource.getPhases().keySet();
    }

    public static boolean isDisabled(String phase) {
        return SamplesResource.getPhases().get(phase).isBlank();
    }

    /**
     * Gets the VCF URL template for the selected phase
     */
    public static String getVcfUrlTemplate(String phase1KG) {
        String path = getServerConfig("1kg." + phase1KG + ".path");
        String filenameTemplate = getServerConfig("1kg." + phase1KG + ".filename");
        String host = getServerConfig("1kg.host");
        return host + "/" + path + "/" + filenameTemplate;
    }

    /**
     * Gets the end position for the given chromosome
     *
     * @param hgVersion the human genome version
     * @param chrName the name of the chromosome. `1` for example.
     * @return the end position (empty if chromosome not found in the file, or if an error occurred
     *         while reading the file)
     */
    public static Optional<Integer> getChrEndPosition(HumanGenomeVersions hgVersion,
            String chrName) {
        return ResourceFile.readResource("chrEndPositions/" + hgVersion, reader ->
            reader.lines().map(line -> line.split("\t"))
                .filter(fields -> fields[0].equals(chrName)).map(fields -> fields[1])
                .findFirst().map(Integer::parseInt)
        ).flatMap(Function.identity());
    }
}
