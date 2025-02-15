package xyz.immortius.museumcurator.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.items.MuseumChecklist;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;
import xyz.immortius.museumcurator.common.network.ChecklistChangeRequest;
import xyz.immortius.museumcurator.common.network.ChecklistUpdateMessage;
import xyz.immortius.museumcurator.common.network.LogOnMessage;
import xyz.immortius.museumcurator.config.MuseumCuratorConfig;
import xyz.immortius.museumcurator.config.system.ConfigSystem;
import xyz.immortius.museumcurator.fabric.extensions.ResourceManagerWithRegistryAccess;
import xyz.immortius.museumcurator.server.ChecklistState;
import xyz.immortius.museumcurator.server.ServerEventHandler;
import xyz.immortius.museumcurator.server.commands.ChecklistCommands;
import xyz.immortius.museumcurator.server.commands.ItemDumpCommand;
import xyz.immortius.museumcurator.server.network.ServerChecklistUpdateReceiver;

import java.nio.file.Paths;

/**
 * Common mod initialization
 */
public class MuseumCuratorMod implements ModInitializer {

    private static final Logger LOGGER = LogManager.getLogger(MuseumCuratorConstants.MOD_ID);

    public static Item MUSEUM_CHECKLIST;
    public static SoundEvent WRITING_SOUND = SoundEvent.createVariableRangeEvent(MuseumCuratorConstants.WRITING_SOUND_ID);

    public static MenuType<MuseumChecklistMenu> MUSEUM_CHECKLIST_MENU;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing");

        ServerLifecycleEvents.SERVER_STARTED.register(ServerEventHandler::onServerStarted);
        ServerLifecycleEvents.SERVER_STARTING.register(ServerEventHandler::onServerStarting);

        PayloadTypeRegistry.playS2C().register(LogOnMessage.ID, LogOnMessage.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ChecklistUpdateMessage.ID, ChecklistUpdateMessage.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ChecklistChangeRequest.ID, ChecklistChangeRequest.STREAM_CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LogOnMessage message = new LogOnMessage(MuseumCollections.getCollections(), ChecklistState.get(server, handler.getPlayer()).getCheckedItems());
            ServerPlayNetworking.send(handler.getPlayer(), message);
        });

        ServerPlayNetworking.registerGlobalReceiver(ChecklistChangeRequest.ID, (payload, context) -> {
            ServerChecklistUpdateReceiver.receive(context.player().getServer(), context.player(), payload);
        });


        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> {
            ItemDumpCommand.register(dispatcher);
            ChecklistCommands.register(dispatcher, context);
        });

        MUSEUM_CHECKLIST = Registry.register(BuiltInRegistries.ITEM, createId("museumchecklist"), new MuseumChecklist(new Item.Properties()));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.addAfter(Items.MUSIC_DISC_PIGSTEP, MUSEUM_CHECKLIST);
        });

        MUSEUM_CHECKLIST_MENU = Registry.register(BuiltInRegistries.MENU, createId("museumchecklistmenu"), new MenuType<>(MuseumChecklistMenu::new, FeatureFlags.VANILLA_SET));

        Registry.register(BuiltInRegistries.SOUND_EVENT, MuseumCuratorConstants.WRITING_SOUND_ID, WRITING_SOUND);

        setupConfig();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(MuseumCuratorConstants.MOD_ID, "server_data");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                ServerEventHandler.onResourceManagerReload(resourceManager, ((ResourceManagerWithRegistryAccess) resourceManager).museumcurator$getRegistryAccess());
            }
        });

    }
    private void setupConfig() {
        new ConfigSystem().synchConfig(Paths.get("defaultconfigs", MuseumCuratorConstants.MOD_ID + ".toml"), MuseumCuratorConfig.get());
    }

    private ResourceLocation createId(String id) {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(MuseumCuratorConstants.MOD_ID, id);
        MuseumCuratorConstants.LOGGER.info("Creating {}", loc);
        return loc;
    }


}