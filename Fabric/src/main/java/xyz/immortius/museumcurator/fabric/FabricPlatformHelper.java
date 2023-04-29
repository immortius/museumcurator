package xyz.immortius.museumcurator.fabric;

import net.minecraft.world.inventory.MenuType;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;
import xyz.immortius.museumcurator.interop.MCPlatformHelper;

/**
 * Static methods whose implementation varies by mod system
 */
public final class FabricPlatformHelper implements MCPlatformHelper {

    @Override
    public MenuType<MuseumChecklistMenu> museumMenu() {
        return MuseumCuratorMod.MUSEUM_CHECKLIST_MENU;
    }
}
