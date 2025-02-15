package xyz.immortius.museumcurator.interop;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface GroupHelper {

    String getLeaderId(ServerPlayer player);

    Iterable<? extends ServerPlayer> getGroupPlayers(MinecraftServer server, UUID ownerId);
}
