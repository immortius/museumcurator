package xyz.immortius.museumcurator.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.items.MuseumChecklist;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;
import xyz.immortius.museumcurator.common.network.ChecklistChangeRequest;
import xyz.immortius.museumcurator.common.network.LogOnMessage;
import xyz.immortius.museumcurator.config.MuseumCuratorConfig;
import xyz.immortius.museumcurator.config.system.ConfigSystem;
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

    public static final ResourceLocation LOG_ON_MESSAGE = new ResourceLocation(MuseumCuratorConstants.MOD_ID, "config");
    public static final ResourceLocation CHECKLIST_UPDATE = new ResourceLocation(MuseumCuratorConstants.MOD_ID, "checklistupdate");

    public static Item MUSEUM_CHECKLIST;
    public static SoundEvent WRITING_SOUND = new SoundEvent(MuseumCuratorConstants.WRITING_SOUND_ID);

    public static MenuType<MuseumChecklistMenu> MUSEUM_CHECKLIST_MENU;

    static {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(MuseumCuratorConstants.MOD_ID, "server_data");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                ServerEventHandler.onResourceManagerReload(resourceManager);
            }
        });
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing");

        ServerLifecycleEvents.SERVER_STARTED.register(ServerEventHandler::onServerStarted);
        ServerLifecycleEvents.SERVER_STARTING.register(ServerEventHandler::onServerStarting);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            FriendlyByteBuf buffer = PacketByteBufs.create();
            buffer.writeWithCodec(LogOnMessage.CODEC, new LogOnMessage(MuseumCollections.getCollections(), ChecklistState.get(server, handler.getPlayer()).getCheckedItems()));
            ServerPlayNetworking.send(handler.getPlayer(), LOG_ON_MESSAGE, buffer);
        });

        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            ItemDumpCommand.register(dispatcher);
            ChecklistCommands.register(dispatcher);
        }));

        MUSEUM_CHECKLIST = Registry.register(Registry.ITEM, createId("museumchecklist"), new MuseumChecklist(new FabricItemSettings().tab(CreativeModeTab.TAB_MISC)));

        MUSEUM_CHECKLIST_MENU = ScreenHandlerRegistry.registerSimple(createId("museumchecklistmenu"), MuseumChecklistMenu::new);

        ServerPlayNetworking.registerGlobalReceiver(MuseumCuratorMod.CHECKLIST_UPDATE, (server, serverPlayer, listener, buf, responseSender) -> {
            ChecklistChangeRequest updateMessage = buf.readWithCodec(ChecklistChangeRequest.CODEC);
            ServerChecklistUpdateReceiver.receive(server, serverPlayer, updateMessage);
        });

        Registry.register(Registry.SOUND_EVENT, MuseumCuratorConstants.WRITING_SOUND_ID, WRITING_SOUND);

        setupConfig();


    }

    private void setupConfig() {
        new ConfigSystem().synchConfig(Paths.get("defaultconfigs", MuseumCuratorConstants.MOD_ID + ".toml"), MuseumCuratorConfig.get());
    }

    private ResourceLocation createId(String id) {
        return new ResourceLocation(MuseumCuratorConstants.MOD_ID, id);
    }


}