package xyz.immortius.museumcurator.fabric.mixins;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.immortius.museumcurator.common.items.MuseumChecklist;

/**
 * Mixin to trigger checklist interaction when using an armor stand while crouching and holding a museum checklist
 */
@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin extends LivingEntity {

    protected ArmorStandMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method="interactAt", at=@At("HEAD"), cancellable = true)
    private void cancelForChecklist(Player player, Vec3 vec3, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> returnable) {
        if (player.isSecondaryUseActive() && player.getItemInHand(interactionHand).getItem() instanceof MuseumChecklist mc) {
            InteractionResult result = mc.interact(this, player.level(), player);
            returnable.setReturnValue(result);
            returnable.cancel();
        }
    }
}
