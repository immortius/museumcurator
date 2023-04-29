package xyz.immortius.museumcurator.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.immortius.museumcurator.client.screens.ChecklistOverviewScreen;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.network.LogOnMessage;

/**
 * Client-only mod initialization
 */
public class MuseumCuratorClientMod implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(MuseumCuratorConstants.MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client Initializing");

        MenuScreens.register(MuseumCuratorMod.MUSEUM_CHECKLIST_MENU, ChecklistOverviewScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(MuseumCuratorMod.LOG_ON_MESSAGE, (client, handler, buf, responseSender) -> {
            LOGGER.info("Receiving config from server");
            LogOnMessage logOnMessage = buf.readWithCodec(LogOnMessage.CODEC);
            MuseumCollections.setCollections(logOnMessage.getCollections());
        });
    }

}
