package xyz.immortius.museumcurator.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
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
        super(new TranslatableComponent("config.museumcurator.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        settingsList = new SettingListWidget(minecraft, this, width, 22, height - 44, (int) (0.9f * width));

        int w = (width / 3 - 60) / 2;
        resetButton = new Button(w, height - 32, 60, 20, new TranslatableComponent("controls.reset"), button -> {
            settingsList.reset();
        });
        cancelButton = new Button(width / 3 + w, height - 32, 60, 20, new TranslatableComponent("gui.cancel"), button -> {
            ConfigUtil.loadDefaultConfig();
            this.minecraft.setScreen(lastScreen);
        });
        saveButton = new Button(2 * width / 3 + w, height - 32, 60, 20, new TranslatableComponent("selectWorld.edit.save"), button -> {
            ConfigUtil.saveDefaultConfig();
            this.minecraft.setScreen(lastScreen);
        });

        this.addWidget(settingsList);
        this.addWidget(cancelButton);
        this.addWidget(saveButton);
        this.addWidget(resetButton);
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
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        this.settingsList.render(stack, mouseX, mouseY, delta);

        int titleWidth = font.width(title);
        int titleX = (width - titleWidth) / 2;
        font.drawShadow(stack, title, titleX, 8, 0xFFFFFF);
        this.cancelButton.render(stack, mouseX, mouseY, delta);
        this.saveButton.render(stack, mouseX, mouseY, delta);
        this.resetButton.render(stack, mouseX, mouseY, delta);
        super.render(stack, mouseX, mouseY, delta);
    }
}
