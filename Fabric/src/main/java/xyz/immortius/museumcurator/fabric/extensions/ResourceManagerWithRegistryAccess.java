package xyz.immortius.museumcurator.fabric.extensions;

import net.minecraft.core.RegistryAccess;

public interface ResourceManagerWithRegistryAccess {
    RegistryAccess museumcurator$getRegistryAccess();

    void museumcurator$setRegistryAccess(RegistryAccess access);
}
