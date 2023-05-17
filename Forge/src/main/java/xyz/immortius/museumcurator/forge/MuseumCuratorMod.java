package xyz.immortius.museumcurator.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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
import java.util.Optional;

/**
 * The Forge mod, registers all mod elements for forge
 */
@Mod(MuseumCuratorConstants.MOD_ID)
public class MuseumCuratorMod {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel MESSAGE_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MuseumCuratorConstants.MOD_ID, "messagechannel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MuseumCuratorConstants.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MuseumCuratorConstants.MOD_ID);
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MuseumCuratorConstants.MOD_ID);

    public static final RegistryObject<Item> MUSEUM_CHECKLIST = ITEMS.register("museumchecklist", () -> new MuseumChecklist(new Item.Properties()));

    public static final RegistryObject<SoundEvent> WRITING_SOUND = SOUNDS.register("writing", () -> SoundEvent.createVariableRangeEvent(MuseumCuratorConstants.WRITING_SOUND_ID));

    public static final RegistryObject<MenuType<MuseumChecklistMenu>> MUSEUM_CHECKLIST_MENU = MENU_TYPES.register("worldforgemenu", () -> new MenuType<>(MuseumChecklistMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public MuseumCuratorMod() {
        new ConfigSystem().synchConfig(Paths.get(MuseumCuratorConstants.DEFAULT_CONFIG_PATH, MuseumCuratorConstants.CONFIG_FILE), MuseumCuratorConfig.get());

        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::updateCreativeTabs);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);

        int packetId = 1;
        MESSAGE_CHANNEL.registerMessage(packetId++, LogOnMessage.class,
                (msg, friendlyByteBuf) -> {
                    friendlyByteBuf.writeWithCodec(NbtOps.INSTANCE, LogOnMessage.CODEC, msg);
                },
                friendlyByteBuf -> friendlyByteBuf.readWithCodec(NbtOps.INSTANCE, LogOnMessage.CODEC),
                (msg, contextSupplier) -> {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LogonReceiver.receive(msg));
                    contextSupplier.get().setPacketHandled(true);
                },
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        MESSAGE_CHANNEL.registerMessage(packetId++, ChecklistUpdateMessage.class,
                (msg, friendlyByteBuf) -> {
                    friendlyByteBuf.writeWithCodec(NbtOps.INSTANCE, ChecklistUpdateMessage.CODEC, msg);
                },
                friendlyByteBuf -> friendlyByteBuf.readWithCodec(NbtOps.INSTANCE, ChecklistUpdateMessage.CODEC),
                (msg, contextSupplier) -> {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> contextSupplier.get().enqueueWork(() -> ChecklistUpdateReceiver.receive(Minecraft.getInstance().player, msg)));
                    contextSupplier.get().setPacketHandled(true);
                },
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        MESSAGE_CHANNEL.registerMessage(packetId++, ChecklistChangeRequest.class,
                (msg, friendlyByteBuf) -> {
                    friendlyByteBuf.writeWithCodec(NbtOps.INSTANCE, ChecklistChangeRequest.CODEC, msg);
                },
                friendlyByteBuf -> friendlyByteBuf.readWithCodec(NbtOps.INSTANCE, ChecklistChangeRequest.CODEC),
                (msg, contextSupplier) -> {
                    ServerChecklistUpdateReceiver.receive(contextSupplier.get().getSender().getServer(), contextSupplier.get().getSender(), msg);
                    contextSupplier.get().setPacketHandled(true);
                },
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public void updateCreativeTabs(BuildCreativeModeTabContentsEvent e) {
        if (e.getTab().getType() == CreativeModeTab.Type.CATEGORY && e.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            e.getEntries().put(MUSEUM_CHECKLIST.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MuseumCuratorClientMod.registerConfigScreen();
            MenuScreens.register(MUSEUM_CHECKLIST_MENU.get(), ChecklistOverviewScreen::new);
        });
    }

    @SubscribeEvent
    public void registerResourceReloadListeners(AddReloadListenerEvent e) {
        e.addListener(new ResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                MuseumCuratorConstants.LOGGER.info("Loading resources");
                ServerEventHandler.onResourceManagerReload(resourceManager);
            }

            @Override
            public String getName() {
                return MuseumCuratorConstants.MOD_ID + ":server_data";
            }
        });
    }

    @SubscribeEvent
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

    @SubscribeEvent
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

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        ItemDumpCommand.register(event.getDispatcher());
        ChecklistCommands.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        ServerEventHandler.onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public void onServerStarting(ServerAboutToStartEvent event) {
        ServerEventHandler.onServerStarting(event.getServer());
    }

    @SubscribeEvent
    public void onServerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        MESSAGE_CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)(event.getEntity())), new LogOnMessage(MuseumCollections.getCollections(), ChecklistState.get(event.getEntity().getServer(), (ServerPlayer) event.getEntity()).getCheckedItems()));
    }

}
