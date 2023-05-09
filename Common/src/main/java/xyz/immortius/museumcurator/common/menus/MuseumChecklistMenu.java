package xyz.immortius.museumcurator.common.menus;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import xyz.immortius.museumcurator.interop.Services;

/**
 * Menu for displaying the checklist ui
 */
public class MuseumChecklistMenu extends AbstractContainerMenu {

    public MuseumChecklistMenu(int menuId, Inventory inventory) {
        super(Services.PLATFORM.museumMenu(), menuId);
    }

    @Override
    public ItemStack quickMoveStack(Player var1, int var2) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
