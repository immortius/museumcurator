package xyz.immortius.museumcurator.interop;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.UUID;

public class NoopGroupHelper implements GroupHelper {
    @Override
    public String getLeaderId(ServerPlayer player) {
        return player.getStringUUID();
    }

    @Override
    public Iterable<? extends ServerPlayer> getGroupPlayers(MinecraftServer server, UUID ownerId) {
        ServerPlayer player = server.getPlayerList().getPlayer(ownerId);
        if (player != null) {
            return Collections.singletonList(player);
        } else {
            return Collections.emptyList();
        }
    }
}
