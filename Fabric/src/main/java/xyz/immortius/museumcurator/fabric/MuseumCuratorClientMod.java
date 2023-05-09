package xyz.immortius.museumcurator.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.nbt.NbtOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.immortius.museumcurator.client.network.ChecklistUpdateReceiver;
import xyz.immortius.museumcurator.client.network.LogonReceiver;
import xyz.immortius.museumcurator.client.screens.ChecklistOverviewScreen;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.common.network.ChecklistUpdateMessage;
import xyz.immortius.museumcurator.common.network.LogOnMessage;

/**
 * Client-only mod initialization
 */
public class MuseumCuratorClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(MuseumCuratorMod.MUSEUM_CHECKLIST_MENU, ChecklistOverviewScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(MuseumCuratorMod.LOG_ON_MESSAGE, (client, handler, buf, responseSender) -> {
            LogOnMessage logOnMessage = buf.readWithCodec(NbtOps.INSTANCE, LogOnMessage.CODEC);
            LogonReceiver.receive(logOnMessage);
        });
        ClientPlayNetworking.registerGlobalReceiver(MuseumCuratorMod.CHECKLIST_UPDATE, (client, handler, buf, responseSender) -> {
            ChecklistUpdateMessage updateMessage = buf.readWithCodec(NbtOps.INSTANCE, ChecklistUpdateMessage.CODEC);
            client.execute(() -> ChecklistUpdateReceiver.receive(client.player, updateMessage));
        });
    }

}
