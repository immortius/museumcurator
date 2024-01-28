package xyz.immortius.museumcurator.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xyz.immortius.museumcurator.client.uielements.SettingListWidget;
import xyz.immortius.museumcurator.common.util.ConfigUtil;

/**
 * Configuration screen for the mod menu
 */
public class MuseumCuratorConfigScreen extends Screen {
    private final Screen lastScreen;
    private SettingListWidget settingsList;
    private Button cancelButton;
    private Button resetButton;
    private Button saveButton;

    public MuseumCuratorConfigScreen(Screen lastScreen) {
        super(Component.translatable("config.museumcurator.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        settingsList = new SettingListWidget(minecraft, this, width, 22, height - 44, (int) (0.9f * width));

        int w = (width / 3 - 60) / 2;
        resetButton = Button.builder(Component.translatable("controls.reset"), button -> {
            settingsList.reset();
        }).bounds(w, height - 32, 60, 20).build();
        cancelButton = Button.builder(Component.translatable("gui.cancel"), button -> {
            ConfigUtil.loadDefaultConfig();
            this.minecraft.setScreen(lastScreen);
        }).bounds(width / 3 + w, height - 32, 60, 20).build();
        saveButton = Button.builder(Component.translatable("selectWorld.edit.save"), button -> {
            ConfigUtil.saveDefaultConfig();
            this.minecraft.setScreen(lastScreen);
        }).bounds(2 * width / 3 + w, height - 32, 60, 20).build();

        this.addRenderableWidget(settingsList);
        this.addRenderableWidget(cancelButton);
        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(resetButton);
    }

    @Override
    public void onClose() {
        ConfigUtil.loadDefaultConfig();
        super.onClose();
        this.minecraft.setScreen(lastScreen);
    }

    @Override
    public void tick() {
        settingsList.tick();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        int titleWidth = font.width(title);
        int titleX = (width - titleWidth) / 2;
        graphics.drawString(this.font, title, titleX, 8, 0xFFFFFF, true);
    }
}
