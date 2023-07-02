package xyz.immortius.museumcurator.interop;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;
import xyz.immortius.museumcurator.common.network.ChecklistChangeRequest;
import xyz.immortius.museumcurator.common.network.ChecklistUpdateMessage;

/**
 * Loader-specific methods
 */
public interface MCPlatformHelper {
    /**
     * @return The museum checklist menu type
     */
    MenuType<MuseumChecklistMenu> museumMenu();

    /**
     * Broadcasts a checklist update to all clients
     * @param server
     * @param msg
     */
    void broadcastChecklistUpdate(MinecraftServer server, ChecklistUpdateMessage msg);

    /**
     * Send a checklist update to a specific client
     * @param server
     * @param player
     * @param msg
     */
    void sendChecklistUpdate(MinecraftServer server, ServerPlayer player, ChecklistUpdateMessage msg);

    /**
     * Sends a checklist update request to the server
     * @param msg
     */
    void sendClientChecklistChange(ChecklistChangeRequest msg);

    SoundEvent writingSound();


}
