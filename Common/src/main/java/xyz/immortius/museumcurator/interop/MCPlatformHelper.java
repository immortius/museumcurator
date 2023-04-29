package xyz.immortius.museumcurator.interop;

import net.minecraft.world.inventory.MenuType;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;

public interface MCPlatformHelper {
    MenuType<MuseumChecklistMenu> museumMenu();
}
