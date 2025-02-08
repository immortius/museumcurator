package xyz.immortius.museumcurator.forge;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xyz.immortius.museumcurator.client.network.ChecklistUpdateReceiver;
import xyz.immortius.museumcurator.client.network.LogonReceiver;
import xyz.immortius.museumcurator.client.screens.ChecklistOverviewScreen;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.items.MuseumChecklist;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;
import xyz.immortius.museumcurator.common.network.ChecklistChangeRequest;
import xyz.immortius.museumcurator.common.network.ChecklistUpdateMessage;
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
 * The Forge mod, registers all mod elements for forge
 */
@Mod(MuseumCuratorConstants.MOD_ID)
public class MuseumCuratorMod {

    private static final String PROTOCOL_VERSION = "1";

    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MuseumCuratorConstants.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MuseumCuratorConstants.MOD_ID);
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MuseumCuratorConstants.MOD_ID);

    public static final DeferredHolder<Item, MuseumChecklist> MUSEUM_CHECKLIST = ITEMS.register("museumchecklist", () -> new MuseumChecklist(new Item.Properties()));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRITING_SOUND = SOUNDS.register("writing", () -> SoundEvent.createVariableRangeEvent(MuseumCuratorConstants.WRITING_SOUND_ID));

    public static final DeferredHolder<MenuType<?>, MenuType<MuseumChecklistMenu>> MUSEUM_CHECKLIST_MENU = MENU_TYPES.register("museumchecklistmenu", () -> new MenuType<>(MuseumChecklistMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public MuseumCuratorMod(IEventBus eventBus) {
        new ConfigSystem().synchConfig(Paths.get(MuseumCuratorConstants.DEFAULT_CONFIG_PATH, MuseumCuratorConstants.CONFIG_FILE), MuseumCuratorConfig.get());

        ITEMS.register(eventBus);
        MENU_TYPES.register(eventBus);
        SOUNDS.register(eventBus);

        eventBus.addListener(this::updateCreativeTabs);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::registerPayloadHandler);
        eventBus.addListener(this::registerMenuScreenEvents);

        NeoForge.EVENT_BUS.addListener(this::registerResourceReloadListeners);
        NeoForge.EVENT_BUS.addListener(this::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(this::onLivingEntityInteract);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onServerLogin);
    }

    public void updateCreativeTabs(BuildCreativeModeTabContentsEvent e) {
        if (e.getTab().getType() == CreativeModeTab.Type.CATEGORY && e.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            e.getEntries().put(MUSEUM_CHECKLIST.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(MuseumCuratorClientMod::registerConfigScreen);
    }

    public void registerMenuScreenEvents(RegisterMenuScreensEvent e) {
        e.register(MUSEUM_CHECKLIST_MENU.get(), ChecklistOverviewScreen::new);
    }

    public void registerResourceReloadListeners(AddReloadListenerEvent e) {
        e.addListener(new ResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                MuseumCuratorConstants.LOGGER.info("Loading resources");
                ServerEventHandler.onResourceManagerReload(resourceManager, e.getRegistryAccess());
            }

            @Override
            public String getName() {
                return MuseumCuratorConstants.MOD_ID + ":server_data";
            }
        });
    }

    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getEntity().isSecondaryUseActive()) {
            return;
        }
        if (event.getItemStack().getItem() instanceof MuseumChecklist mc) {
            InteractionResult result = mc.interact(event.getTarget(), event.getLevel(), event.getEntity());
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    public void onLivingEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!event.getEntity().isSecondaryUseActive()) {
            return;
        }
        if (event.getItemStack().getItem() instanceof MuseumChecklist mc) {
            InteractionResult result = mc.interact(event.getTarget(), event.getLevel(), event.getEntity());
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    public void registerCommands(RegisterCommandsEvent event) {
        ItemDumpCommand.register(event.getDispatcher());
        ChecklistCommands.register(event.getDispatcher(), event.getBuildContext());
    }

    public void onServerStarted(ServerStartedEvent event) {
        ServerEventHandler.onServerStarted(event.getServer());
    }

    public void onServerStarting(ServerAboutToStartEvent event) {
        ServerEventHandler.onServerStarting(event.getServer());
    }

    public void registerPayloadHandler(final RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar(MuseumCuratorConstants.MOD_ID).versioned(PROTOCOL_VERSION);
        registrar.playToClient(LogOnMessage.ID, LogOnMessage.STREAM_CODEC, (payload, context) -> LogonReceiver.receive(payload));
        registrar.playToClient(ChecklistUpdateMessage.ID, ChecklistUpdateMessage.STREAM_CODEC,
                (payload, context) -> {
                    ChecklistUpdateReceiver.receive((LocalPlayer) context.player(), payload);
                });

        registrar.playToServer(ChecklistChangeRequest.ID, ChecklistChangeRequest.STREAM_CODEC,
                (payload, context) -> {
                    ServerChecklistUpdateReceiver.receive(context.player().getServer(), (ServerPlayer) context.player(), payload);
                });
    }

    public void onServerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new LogOnMessage(MuseumCollections.getCollections(), ChecklistState.get(event.getEntity().getServer(), (ServerPlayer) event.getEntity()).getCheckedItems()));
    }
}
