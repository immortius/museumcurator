package xyz.immortius.museumcurator.forge;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import xyz.immortius.museumcurator.client.screens.MuseumCuratorConfigScreen;

public final class MuseumCuratorClientMod {

    private MuseumCuratorClientMod() {
    }

    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new MuseumCuratorConfigScreen(screen)));
    }
}
