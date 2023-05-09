package xyz.immortius.museumcurator.server;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import joptsimple.internal.Strings;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.NotNull;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.common.data.MuseumCollection;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.data.MuseumExhibit;
import xyz.immortius.museumcurator.common.data.RawExhibit;
import xyz.immortius.museumcurator.config.MuseumCuratorConfig;
import xyz.immortius.museumcurator.config.system.ConfigSystem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.stream.IntStream;
import java.util.Map;

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
        List<RawExhibit> rawExhibits = new ArrayList<>();

        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(MuseumCuratorConstants.EXHIBIT_DATA_PATH, r -> true).entrySet()) {
            try (InputStreamReader reader = new InputStreamReader(entry.getValue().open())) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                RawExhibit exhibit = RawExhibit.EXHIBIT_CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(false, Util.prefix("Error parsing museum collection: ", MuseumCuratorConstants.LOGGER::error));
                rawExhibits.add(exhibit);
            } catch (IOException | RuntimeException e) {
                MuseumCuratorConstants.LOGGER.error("Failed to read museum exhibit data '{}'", entry.getKey(), e);
            }
        }

        rawExhibits = prepareExhibits(rawExhibits);

        Collection<MuseumCollection> collections = prepareCollections(rawExhibits);

        MuseumCollections.setCollections(collections);
        MuseumCuratorConstants.LOGGER.info("Loaded {} museum exhibits", rawExhibits.size());
    }

    @NotNull
    private static Collection<MuseumCollection> prepareCollections(List<RawExhibit> rawExhibits) {
        boolean ignoreConstraints = false;
        Map<String, MuseumCollection> collectionsLookup = new LinkedHashMap<>();
        while (!rawExhibits.isEmpty()) {
            List<RawExhibit> residual = new ArrayList<>();
            for (RawExhibit exhibit : rawExhibits) {
                MuseumCollection collection = collectionsLookup.computeIfAbsent(exhibit.getCollection(), s -> new MuseumCollection(s, Collections.emptyList()));
                if (ignoreConstraints || Strings.isNullOrEmpty(exhibit.getRelativeTo())) {
                    collection.getExhibits().add(new MuseumExhibit(exhibit.getName(), exhibit.getItems()));
                } else {
                    OptionalInt match = IntStream.range(0, collection.getExhibits().size()).filter(i -> collection.getExhibits().get(i).getRawName().equals(exhibit.getRelativeTo())).findFirst();
                    if (match.isPresent()) {
                        collection.getExhibits().add(match.getAsInt() + exhibit.getPlacement().shift(), new MuseumExhibit(exhibit.getName(), exhibit.getItems()));
                    } else {
                        residual.add(exhibit);
                    }
                }
            }
            if (residual.size() == rawExhibits.size()) {
                ignoreConstraints = true;
            } else {
                rawExhibits = residual;
            }
        }
        return collectionsLookup.values();
    }

    private static List<RawExhibit> prepareExhibits(List<RawExhibit> rawExhibits) {
        Table<String, String, RawExhibit> exhibitTable = HashBasedTable.create();
        for (RawExhibit exhibit : rawExhibits) {
            RawExhibit existingExhibit = exhibitTable.get(exhibit.getCollection(), exhibit.getName());
            if (existingExhibit != null) {
                existingExhibit.getInsertGroups().addAll(exhibit.getInsertGroups());
                existingExhibit.getItems().addAll(exhibit.getItems());
                if (Strings.isNullOrEmpty(existingExhibit.getRelativeTo())) {
                    existingExhibit.setRelativeTo(exhibit.getRelativeTo());
                    existingExhibit.setPlacement(exhibit.getPlacement());
                }
            } else {
                exhibitTable.put(exhibit.getCollection(), exhibit.getName(), exhibit);
            }
        }
        exhibitTable.values().forEach(RawExhibit::applyInserts);
        return new ArrayList<>(exhibitTable.values());
    }

}
