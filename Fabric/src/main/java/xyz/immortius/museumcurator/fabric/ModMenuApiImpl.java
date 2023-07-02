package xyz.immortius.museumcurator.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.fabric.mixin.screen.ScreenAccessor;
import net.minecraft.client.Minecraft;
import xyz.immortius.museumcurator.client.screens.MuseumCuratorConfigScreen;

public class ModMenuApiImpl implements ModMenuApi {

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return lastScreen -> {
            Minecraft minecraft = ((ScreenAccessor) lastScreen).getClient();
            if (minecraft.isLocalServer() || minecraft.getConnection() != null) {
                return null;
            } else {
                return new MuseumCuratorConfigScreen(lastScreen);
            }
        };
    }
}