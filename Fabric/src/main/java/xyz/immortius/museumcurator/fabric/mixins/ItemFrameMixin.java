package xyz.immortius.museumcurator.fabric.mixins;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.immortius.museumcurator.common.items.MuseumChecklist;

/**
 * Mixin to trigger checklist interaction when using an item frame while crouching and holding a museum checklist
 */
@Mixin(ItemFrame.class)
public abstract class ItemFrameMixin extends HangingEntity {

    protected ItemFrameMixin(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method="interact", at=@At("HEAD"), cancellable = true)
    public void interact(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> returnable) {
        if (player.isSecondaryUseActive() && player.getItemInHand(interactionHand).getItem() instanceof MuseumChecklist mc) {
            InteractionResult result = mc.interact(this, player.level(), player);
            returnable.setReturnValue(result);
            returnable.cancel();
        }
    }

}
