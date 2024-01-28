package xyz.immortius.museumcurator.fabric;

import io.netty.handler.codec.EncoderException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.Util;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
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
        FriendlyByteBuf buffer = PacketByteBufs.create();
        Tag tag = Util.getOrThrow(ChecklistUpdateMessage.CODEC.encodeStart(NbtOps.INSTANCE, msg), string -> new EncoderException("Failed to encode: " + string + " " + msg));
        buffer.writeNbt(tag);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, MuseumCuratorMod.CHECKLIST_UPDATE, buffer);
        }
    }

    @Override
    public void sendChecklistUpdate(MinecraftServer server, ServerPlayer player, ChecklistUpdateMessage msg) {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        Tag tag = Util.getOrThrow(ChecklistUpdateMessage.CODEC.encodeStart(NbtOps.INSTANCE, msg), string -> new EncoderException("Failed to encode: " + string + " " + msg));
        buffer.writeNbt(tag);
        ServerPlayNetworking.send(player, MuseumCuratorMod.CHECKLIST_UPDATE, buffer);
    }

    @Override
    public void sendClientChecklistChange(ChecklistChangeRequest msg) {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        Tag tag = Util.getOrThrow(ChecklistChangeRequest.CODEC.encodeStart(NbtOps.INSTANCE, msg), string -> new EncoderException("Failed to encode: " + string + " " + msg));
        buffer.writeNbt(tag);
        ClientPlayNetworking.send(MuseumCuratorMod.CHECKLIST_UPDATE, buffer);
    }

    @Override
    public SoundEvent writingSound() {
        return MuseumCuratorMod.WRITING_SOUND;
    }
}
