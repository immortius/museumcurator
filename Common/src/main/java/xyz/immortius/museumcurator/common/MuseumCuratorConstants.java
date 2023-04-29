package xyz.immortius.museumcurator.common;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Constants for ChunkByChunk - may vary by mod system
 */
public final class MuseumCuratorConstants {

    public static final String COLLECTION_DATA_PATH = "museumcollection";

    private MuseumCuratorConstants() {
    }

    public static final Logger LOGGER = LogManager.getLogger(MuseumCuratorConstants.MOD_ID);

    public static final String MOD_ID = "museumcurator";

    public static final String DEFAULT_CONFIG_PATH = "defaultconfigs";
    public static final String CONFIG_FILE = MOD_ID + ".toml";
}