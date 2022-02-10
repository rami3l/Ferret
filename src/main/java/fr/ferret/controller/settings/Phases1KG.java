package fr.ferret.controller.settings;

import fr.ferret.model.Region;

/**
 * 1KG project supported versions
 */
public enum Phases1KG {
        // Populations name at the start of "GUI" of the old Ferret
        V1(new Region[] {new Region("all_populations", new String[] {"ALL"}, new int[] {661}),
                        new Region("africa",
                                        new String[] {"AFR", "ACB", "ASW", "ESN", "GWD", "LWK",
                                                        "MSL", "YRI"},
                                        new int[] {661, 96, 61, 99, 113, 99, 85, 108}),
                        new Region("europe",
                                        new String[] {"EUR", "CEU", "GBR", "FIN", "IBS", "TSI"},
                                        new int[] {503, 99, 91, 99, 107, 107}),
                        new Region("east_asia",
                                        new String[] {"EAS", "CDX", "CHB", "CHS", "JPT", "KHV"},
                                        new int[] {504, 93, 103, 105, 104, 99}),
                        new Region("america", new String[] {"AMR", "CLM", "MXL", "PEL", "PUR"},
                                        new int[] {347, 94, 64, 85, 104}),
                        new Region("south_asia",
                                        new String[] {"SAS", "BEB", "GIH", "ITU", "PJL", "STU"},
                                        new int[] {489, 86, 103, 102, 96, 102})}),

        V3(new Region[] {new Region("all_populations", new String[] {"ALL"}, new int[] {2504}),
                        new Region("africa",
                                        new String[] {"AFR", "ACB", "ASW", "ESN", "GWD", "LWK",
                                                        "MSL", "YRI"},
                                        new int[] {661, 96, 61, 99, 113, 99, 85, 108}),
                        new Region("europe",
                                        new String[] {"EUR", "CEU", "GBR", "FIN", "IBS", "TSI"},
                                        new int[] {503, 99, 91, 99, 107, 107}),
                        new Region("east_asia",
                                        new String[] {"EAS", "CDX", "CHB", "CHS", "JPT", "KHV"},
                                        new int[] {504, 93, 103, 105, 104, 99}),
                        new Region("america", new String[] {"AMR", "CLM", "MXL", "PEL", "PUR"},
                                        new int[] {347, 94, 64, 85, 104}),
                        new Region("south_asia",
                                        new String[] {"SAS", "BEB", "GIH", "ITU", "PJL", "STU"},
                                        new int[] {489, 86, 103, 102, 96, 102})}),

        NYGC_30X(new Region[0]);

        /**
         * The regions for this phase
         */
        private final Region[] regions;

        Phases1KG(Region[] regions) {
                this.regions = regions;
        }

        /**
         * @return The regions for this phase
         */
        public Region[] getRegions() {
                return regions;
        }
}
