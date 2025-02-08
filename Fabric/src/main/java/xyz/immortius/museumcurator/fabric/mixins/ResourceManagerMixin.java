package xyz.immortius.museumcurator.fabric.mixins;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import xyz.immortius.museumcurator.fabric.extensions.ResourceManagerWithRegistryAccess;

@Mixin(value = MultiPackResourceManager.class)
public abstract class ResourceManagerMixin implements ResourceManagerWithRegistryAccess {
    private RegistryAccess registryAccess;

    public RegistryAccess museumcurator$getRegistryAccess() {
        return registryAccess;
    }

    public void museumcurator$setRegistryAccess(RegistryAccess registryAccess) {
        this.registryAccess = registryAccess;
    }
}
