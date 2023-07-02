package xyz.immortius.museumcurator.forge;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.PacketDistributor;
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
        MuseumCuratorMod.MESSAGE_CHANNEL.send(PacketDistributor.ALL.noArg(), msg);
    }

    @Override
    public void sendChecklistUpdate(MinecraftServer server, ServerPlayer player, ChecklistUpdateMessage msg) {
        MuseumCuratorMod.MESSAGE_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    @Override
    public void sendClientChecklistChange(ChecklistChangeRequest msg) {
        MuseumCuratorMod.MESSAGE_CHANNEL.sendToServer(msg);
    }

    @Override
    public SoundEvent writingSound() {
        return MuseumCuratorMod.WRITING_SOUND.get();
    }
}
