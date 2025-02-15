package xyz.immortius.museumcurator.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;
import xyz.immortius.museumcurator.common.network.ChecklistChangeRequest;
import xyz.immortius.museumcurator.common.network.ChecklistUpdateMessage;
import xyz.immortius.museumcurator.interop.MCPlatformHelper;

/**
 * Static methods whose implementation varies by mod system
 */
public final class FabricPlatformHelper implements MCPlatformHelper {

    @Override
    public MenuType<MuseumChecklistMenu> museumMenu() {
        return MuseumCuratorMod.MUSEUM_CHECKLIST_MENU;
    }

    public void broadcastChecklistUpdate(MinecraftServer server, ChecklistUpdateMessage msg) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, msg);
        }
    }

    @Override
    public void sendChecklistUpdate(MinecraftServer server, ServerPlayer player, ChecklistUpdateMessage msg) {
        ServerPlayNetworking.send(player, msg);
    }

    @Override
    public void sendClientChecklistChange(ChecklistChangeRequest msg) {
        ClientPlayNetworking.send(msg);
    }

    @Override
    public SoundEvent writingSound() {
        return MuseumCuratorMod.WRITING_SOUND;
    }
}
