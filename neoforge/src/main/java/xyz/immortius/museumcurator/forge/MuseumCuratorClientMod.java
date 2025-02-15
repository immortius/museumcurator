package xyz.immortius.museumcurator.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;
import xyz.immortius.museumcurator.client.screens.MuseumCuratorConfigScreen;

public final class MuseumCuratorClientMod {

    private MuseumCuratorClientMod() {
    }

    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> new IConfigScreenFactory() {
            @Override
            public @NotNull Screen createScreen(ModContainer container, Screen modListScreen) {
                return new MuseumCuratorConfigScreen(modListScreen);
            }
        });
    }
}
