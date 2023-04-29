package xyz.immortius.museumcurator.forge;

import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.ModLoadingContext;
import xyz.immortius.museumcurator.client.screens.MuseumCuratorConfigScreen;

public final class MuseumCuratorClientMod {

    private MuseumCuratorClientMod() {
    }

    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((minecraft, screen) -> new MuseumCuratorConfigScreen(screen)));
    }
}
