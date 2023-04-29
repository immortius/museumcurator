package xyz.immortius.museumcurator.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;

public class MuseumChecklist extends Item implements MenuProvider {
    public MuseumChecklist(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.openMenu(this);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("museumcurator.menu.checklist.title");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int menuId, Inventory inventory, Player player) {
        return new MuseumChecklistMenu(menuId, inventory);
    }
}
