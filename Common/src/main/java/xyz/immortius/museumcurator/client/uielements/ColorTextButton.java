package xyz.immortius.museumcurator.client.uielements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Slightly modified button that allows text color to be specified.
 */
public class ColorTextButton extends Button {
    private static final WidgetSprites SPRITES = new WidgetSprites(
            new ResourceLocation("widget/button"), new ResourceLocation("widget/button_disabled"), new ResourceLocation("widget/button_highlighted")
    );

    private int activeTextColor = 0xffffff;
    private int textColor = 0xa0a0a0;

    public ColorTextButton(int x, int y, int width, int height, Component label, OnPress onPress) {
        this(x, y, width, height, label, onPress, DEFAULT_NARRATION);
    }

    public ColorTextButton(int x, int y, int width, int height, Component label, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, label, onPress, createNarration);
    }

    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        Minecraft $$4 = Minecraft.getInstance();
        $$0.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        $$0.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        $$0.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int $$5 = this.active ? activeTextColor : textColor;
        this.renderString($$0, $$4.font, $$5 | Mth.ceil(this.alpha * 255.0F) << 24);
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
