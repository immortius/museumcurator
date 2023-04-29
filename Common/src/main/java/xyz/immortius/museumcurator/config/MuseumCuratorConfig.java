package xyz.immortius.museumcurator.config;

/**
 * Root configuration
 */
public class MuseumCuratorConfig {

    private static final MuseumCuratorConfig instance = new MuseumCuratorConfig();

    public static MuseumCuratorConfig get() {
        return instance;
    }

}
