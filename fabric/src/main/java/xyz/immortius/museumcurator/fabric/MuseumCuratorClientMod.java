package xyz.immortius.museumcurator.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import xyz.immortius.museumcurator.client.network.ChecklistUpdateReceiver;
import xyz.immortius.museumcurator.client.network.LogonReceiver;
import xyz.immortius.museumcurator.client.screens.ChecklistOverviewScreen;
import xyz.immortius.museumcurator.common.network.ChecklistUpdateMessage;
import xyz.immortius.museumcurator.common.network.LogOnMessage;

/**
 * Client-only mod initialization
 */
public class MuseumCuratorClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(MuseumCuratorMod.MUSEUM_CHECKLIST_MENU, ChecklistOverviewScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(LogOnMessage.ID, (payload, context) -> {
            LogonReceiver.receive(payload);
        });
        ClientPlayNetworking.registerGlobalReceiver(ChecklistUpdateMessage.ID, (payload, context) -> {
            context.client().execute(() -> ChecklistUpdateReceiver.receive(context.player(), payload));
        });
    }

}
