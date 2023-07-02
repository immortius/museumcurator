package xyz.immortius.museumcurator.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
        List<ItemStack> items = new ArrayList<>();
        if (MuseumCollections.isValidCollectionItem(targetBlock.getBlock().asItem().getDefaultInstance())) {
            items.add(targetBlock.getBlock().asItem().getDefaultInstance());
        }

        BlockEntity blockEntity = serverLevel.getBlockEntity(context.getClickedPos());
        if (blockEntity instanceof Container container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack content = container.getItem(i);
                if (MuseumCollections.isValidCollectionItem(content)) {
                    items.add(content);
                }
            }
        }
        if (targetBlock.getBlock() instanceof FlowerPotBlock pot) {
            items.add(pot.getContent().asItem().getDefaultInstance());
            items.add(Items.FLOWER_POT.getDefaultInstance());
        }
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        ChecklistState.get(serverLevel.getServer(), player).check(items);
        return InteractionResult.CONSUME;
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

    public InteractionResult interact(Entity entity, Level level, Player player) {
        if (level.isClientSide) {
            level.playSound(player, entity.blockPosition(), Services.PLATFORM.writingSound(), SoundSource.PLAYERS, 1.0f, 1.f);
            return InteractionResult.SUCCESS;
        }
        List<ItemStack> items = new ArrayList<>();
        if (entity instanceof Container container) {
            items.add(level.getBlockState(entity.blockPosition()).getBlock().asItem().getDefaultInstance());
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack content = container.getItem(i);
                if (MuseumCollections.isValidCollectionItem(content)) {
                    items.add(content);
                }
            }
        } else if (entity instanceof ItemFrame itemFrame) {
            items.add(itemFrame.getItem());
        } else if (entity instanceof ArmorStand armorStand) {
            for (ItemStack armorSlot : armorStand.getArmorSlots()) {
                if (MuseumCollections.isValidCollectionItem(armorSlot)) {
                    items.add(armorSlot);
                }
            }
            items.add(Items.ARMOR_STAND.getDefaultInstance());
        }
        ChecklistState.get(((ServerLevel)level).getServer(), (ServerPlayer) player).check(items);
        return InteractionResult.CONSUME;
    }
}
