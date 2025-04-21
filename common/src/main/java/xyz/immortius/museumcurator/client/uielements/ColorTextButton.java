package xyz.immortius.museumcurator.client.uielements;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

/**
 * Slightly modified button that allows text color to be specified.
 */
public class ColorTextButton extends Button {
    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/button"),
            ResourceLocation.withDefaultNamespace("widget/button_disabled"),
            ResourceLocation.withDefaultNamespace("widget/button_highlighted")
    );

    private int activeTextColor = 0xffffff;
    private int textColor = 0xa0a0a0;

    public ColorTextButton(int x, int y, int width, int height, Component label, OnPress onPress) {
        this(x, y, width, height, label, onPress, DEFAULT_NARRATION);
    }

    public ColorTextButton(int x, int y, int width, int height, Component label, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, label, onPress, createNarration);
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.blitSprite(
                RenderType::guiTextured,
                SPRITES.get(this.active, this.isHoveredOrFocused()),
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                ARGB.white(this.alpha)
        );
        int color = this.active ? activeTextColor : textColor;
        this.renderString(guiGraphics, minecraft.font, color);
    }

    private int getTextureY() {
        int offset = 1;
        if (!this.active) {
            offset = 0;
        } else if (this.isHoveredOrFocused()) {
            offset = 2;
        }

        return 46 + offset * 20;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setActiveTextColor(int activeTextColor) {
        this.activeTextColor = activeTextColor;
    }
}
