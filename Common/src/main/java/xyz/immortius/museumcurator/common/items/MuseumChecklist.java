package xyz.immortius.museumcurator.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;
import xyz.immortius.museumcurator.interop.Services;
import xyz.immortius.museumcurator.server.ChecklistState;

import java.util.ArrayList;
import java.util.List;

/**
 * The MuseumChecklist is used to either access the checklist menu, or while crouching can directly check off items.
 * If used on a block while crouching its item form will be checked off. For Chests, Item Frames, Armor Stands and Flower Pots
 * their contents will be checked off.
 */
public class MuseumChecklist extends Item implements MenuProvider {
    public MuseumChecklist(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        player.openMenu(this);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (context.getLevel().isClientSide) {
            context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), Services.PLATFORM.writingSound(), SoundSource.PLAYERS, 1.0f, 1.f);
            return InteractionResult.SUCCESS;
        }
        ServerLevel serverLevel = (ServerLevel) context.getLevel();
        BlockState targetBlock = context.getLevel().getBlockState(context.getClickedPos());
        List<Item> items = new ArrayList<>();
        if (MuseumCollections.getAllCollectionItems().contains(targetBlock.getBlock().asItem())) {
            items.add(targetBlock.getBlock().asItem());
        }

        BlockEntity blockEntity = serverLevel.getBlockEntity(context.getClickedPos());
        if (blockEntity instanceof Container container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack content = container.getItem(i);
                if (MuseumCollections.getAllCollectionItems().contains(content.getItem())) {
                    items.add(content.getItem());
                }
            }
        }
        if (targetBlock.getBlock() instanceof FlowerPotBlock pot) {
            items.add(pot.getContent().asItem());
            items.add(Items.FLOWER_POT);
        }
        ChecklistState.get(serverLevel.getServer()).check(items);
        return InteractionResult.CONSUME;
    }


    @Override
    public Component getDisplayName() {
        return Component.translatable("museumcurator.menu.checklist.title");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int menuId, Inventory inventory, Player player) {
        return new MuseumChecklistMenu(menuId, inventory);
    }

    public InteractionResult interact(Entity entity, Level level, Player player) {
        if (level.isClientSide) {
            level.playSound(player, entity.blockPosition(), Services.PLATFORM.writingSound(), SoundSource.PLAYERS, 1.0f, 1.f);
            return InteractionResult.SUCCESS;
        }
        List<Item> items = new ArrayList<>();
        if (entity instanceof Container container) {
            items.add(level.getBlockState(entity.blockPosition()).getBlock().asItem());
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack content = container.getItem(i);
                if (MuseumCollections.getAllCollectionItems().contains(content.getItem())) {
                    items.add(content.getItem());
                }
            }
        } else if (entity instanceof ItemFrame itemFrame) {
            items.add(itemFrame.getItem().getItem());
        } else if (entity instanceof ArmorStand armorStand) {
            for (ItemStack armorSlot : armorStand.getArmorSlots()) {
                items.add(armorSlot.getItem());
            }
            items.add(Items.ARMOR_STAND);
        }
        ChecklistState.get(((ServerLevel)level).getServer()).check(items);
        return InteractionResult.CONSUME;
    }
}
