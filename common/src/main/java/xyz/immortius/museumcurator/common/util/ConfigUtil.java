package xyz.immortius.museumcurator.common.util;

import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.config.MuseumCuratorConfig;
import xyz.immortius.museumcurator.config.system.ConfigSystem;

import java.nio.file.Paths;

/**
 * Utility methods for working with Config
 */
public final class ConfigUtil {
    private ConfigUtil() {

    }

    private static final ConfigSystem system = new ConfigSystem();

    /**
     * Loads the default (not world specific) config.
     */
    public static void loadDefaultConfig() {
        synchronized (system) {
            system.synchConfig(Paths.get(MuseumCuratorConstants.DEFAULT_CONFIG_PATH).resolve(MuseumCuratorConstants.CONFIG_FILE), MuseumCuratorConfig.get());
        }
    }

    /**
     * Saves the default (not world specific) config.
     */
    public static void saveDefaultConfig() {
        synchronized (system) {
            system.write(Paths.get(MuseumCuratorConstants.DEFAULT_CONFIG_PATH).resolve(MuseumCuratorConstants.CONFIG_FILE), MuseumCuratorConfig.get());
        }
    }
}
