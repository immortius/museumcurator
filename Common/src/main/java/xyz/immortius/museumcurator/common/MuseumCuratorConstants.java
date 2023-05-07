package xyz.immortius.museumcurator.common;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Constants for ChunkByChunk - may vary by mod system
 */
public final class MuseumCuratorConstants {

    public static final String COLLECTION_DATA_PATH = "museumcollection";

    public static final Logger LOGGER = LogManager.getLogger(MuseumCuratorConstants.MOD_ID);

    public static final String MOD_ID = "museumcurator";

    public static final String DEFAULT_CONFIG_PATH = "defaultconfigs";
    public static final String CONFIG_FILE = MOD_ID + ".toml";
    public static final ResourceLocation WRITING_SOUND_ID = new ResourceLocation(MOD_ID + ":writing");

    private MuseumCuratorConstants() {
    }
}