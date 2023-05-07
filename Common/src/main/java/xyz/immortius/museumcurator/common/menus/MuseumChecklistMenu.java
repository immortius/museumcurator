package xyz.immortius.museumcurator.common.menus;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import xyz.immortius.museumcurator.interop.Services;

/**
 * Menu for displaying the checklist ui
 */
public class MuseumChecklistMenu extends AbstractContainerMenu {

    public MuseumChecklistMenu(int menuId, Inventory inventory) {
        super(Services.PLATFORM.museumMenu(), menuId);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
