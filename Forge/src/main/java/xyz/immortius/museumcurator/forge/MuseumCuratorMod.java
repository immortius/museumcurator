package xyz.immortius.museumcurator.forge;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
import xyz.immortius.museumcurator.client.screens.ChecklistOverviewScreen;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.common.commands.ItemDumpCommand;
import xyz.immortius.museumcurator.common.data.MuseumCollection;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.items.MuseumChecklist;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;
import xyz.immortius.museumcurator.common.network.LogOnMessage;
import xyz.immortius.museumcurator.config.MuseumCuratorConfig;
import xyz.immortius.museumcurator.config.system.ConfigSystem;
import xyz.immortius.museumcurator.server.ServerEventHandler;

import java.nio.file.Paths;
import java.util.Optional;

/**
 * The Forge mod, registers all mod elements for forge
 */
@Mod(MuseumCuratorConstants.MOD_ID)
public class MuseumCuratorMod {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CONFIG_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MuseumCuratorConstants.MOD_ID, "configchannel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MuseumCuratorConstants.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MuseumCuratorConstants.MOD_ID);

    public static final RegistryObject<Item> MUSEUM_CHECKLIST = ITEMS.register("museumchecklist", () -> new MuseumChecklist(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistryObject<MenuType<MuseumChecklistMenu>> MUSEUM_CHECKLIST_MENU = CONTAINERS.register("worldforgemenu", () -> new MenuType<>(MuseumChecklistMenu::new));

    public MuseumCuratorMod() {
        new ConfigSystem().synchConfig(Paths.get(MuseumCuratorConstants.DEFAULT_CONFIG_PATH, MuseumCuratorConstants.CONFIG_FILE), MuseumCuratorConfig.get());

        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);

        int packetId = 1;
        CONFIG_CHANNEL.registerMessage(packetId++, LogOnMessage.class,
                (msg, friendlyByteBuf) -> {
                    friendlyByteBuf.writeWithCodec(LogOnMessage.CODEC, msg);
                },
                friendlyByteBuf -> friendlyByteBuf.readWithCodec(LogOnMessage.CODEC),
                (msg, contextSupplier) -> {
                    MuseumCollections.setCollections(msg.getCollections());
                    contextSupplier.get().setPacketHandled(true);
                },
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
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
    public void registerCommands(RegisterCommandsEvent event) {
        ItemDumpCommand.register(event.getDispatcher());
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
        CONFIG_CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)(event.getEntity())), new LogOnMessage(MuseumCollections.getCollections()));
    }

}
