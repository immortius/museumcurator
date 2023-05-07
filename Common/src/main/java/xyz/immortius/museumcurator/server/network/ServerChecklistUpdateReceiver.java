package xyz.immortius.museumcurator.server.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import xyz.immortius.museumcurator.common.network.ChecklistChangeRequest;
import xyz.immortius.museumcurator.server.ChecklistState;

/**
 * Processes checklist update requests from clients, applying their changes
 */
public class ServerChecklistUpdateReceiver {

    public static void receive(MinecraftServer server, ServerPlayer player, ChecklistChangeRequest updateMessage) {
        ChecklistState.get(server).uncheck(updateMessage.getUncheckedItems());
        ChecklistState.get(server).check(updateMessage.getCheckedItems());
    }
}
