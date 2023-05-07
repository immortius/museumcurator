package xyz.immortius.museumcurator.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.LevelResource;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.common.data.MuseumCollection;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.config.MuseumCuratorConfig;
import xyz.immortius.museumcurator.config.system.ConfigSystem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Server event handlers for events triggered server-side. Primarily loads the collection data
 */
public final class ServerEventHandler {

    private static final String SERVERCONFIG = "serverconfig";
    private static final ConfigSystem configSystem = new ConfigSystem();

    private ServerEventHandler() {

    }

    /**
     * Handles the event when the server is first starting, before any levels are created.
     * @param server The minecraft server that is starting
     */
    public static void onServerStarting(MinecraftServer server) {
        configSystem.synchConfig(server.getWorldPath(LevelResource.ROOT).resolve(SERVERCONFIG).resolve(MuseumCuratorConstants.CONFIG_FILE), MuseumCuratorConfig.get());
    }

    /**
     * Event when the server has started.
     * @param server The minecraft server that has started
     */
    public static void onServerStarted(MinecraftServer server) {

    }

    public static void onResourceManagerReload(ResourceManager resourceManager) {
        List<MuseumCollection> collections = new ArrayList<>();
        for (ResourceLocation location : resourceManager.listResources(MuseumCuratorConstants.COLLECTION_DATA_PATH, r -> !r.isEmpty() && !MuseumCuratorConstants.COLLECTION_DATA_PATH.equals(r))) {
            try (InputStreamReader reader = new InputStreamReader(resourceManager.getResource(location).getInputStream())) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                MuseumCollection collection = MuseumCollection.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(false, Util.prefix("Error parsing museum collection: ", MuseumCuratorConstants.LOGGER::error));
                collections.add(collection);
            } catch (IOException e) {
                MuseumCuratorConstants.LOGGER.error("Failed to read museum collection data '{}'", location, e);
            }
        }
        MuseumCollections.setCollections(collections);
        MuseumCuratorConstants.LOGGER.info("Loaded {} museum collections", collections.size());
    }

}
