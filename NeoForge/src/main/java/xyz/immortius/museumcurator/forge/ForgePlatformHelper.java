package xyz.immortius.museumcurator.forge;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.PacketDistributor;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;
import xyz.immortius.museumcurator.common.network.ChecklistChangeRequest;
import xyz.immortius.museumcurator.common.network.ChecklistUpdateMessage;
import xyz.immortius.museumcurator.interop.MCPlatformHelper;

/**
 * Static methods whose implementation varies by mod system
 */
public final class ForgePlatformHelper implements MCPlatformHelper {

    @Override
    public MenuType<MuseumChecklistMenu> museumMenu() {
        return MuseumCuratorMod.MUSEUM_CHECKLIST_MENU.get();
    }

    @Override
    public void broadcastChecklistUpdate(MinecraftServer server, ChecklistUpdateMessage msg) {
        PacketDistributor.ALL.noArg().send(new MuseumCuratorMod.ChecklistUpdateMessagePayload(msg));
    }

    @Override
    public void sendChecklistUpdate(MinecraftServer server, ServerPlayer player, ChecklistUpdateMessage msg) {
        PacketDistributor.PLAYER.with(player).send(new MuseumCuratorMod.ChecklistUpdateMessagePayload(msg));
    }

    @Override
    public void sendClientChecklistChange(ChecklistChangeRequest msg) {
        PacketDistributor.SERVER.noArg().send(new MuseumCuratorMod.ChecklistChangeRequestPayload(msg));
    }

    @Override
    public SoundEvent writingSound() {
        return MuseumCuratorMod.WRITING_SOUND.get();
    }
}
