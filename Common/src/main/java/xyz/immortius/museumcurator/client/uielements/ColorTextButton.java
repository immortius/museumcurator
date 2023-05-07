package xyz.immortius.museumcurator.client.uielements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * Slightly modified button that allows text color to be specified.
 */
public class ColorTextButton extends Button {
    private int activeTextColor = 0xffffff;
    private int textColor = 0xa0a0a0;

    public ColorTextButton(int x, int y, int width, int height, Component label, OnPress onPress) {
        super(x, y, width, height, label, onPress);
    }

    public ColorTextButton(int x, int y, int width, int height, Component label, OnPress onPress, OnTooltip onTooltip) {
        super(x, y, width, height, label, onPress, onTooltip);
    }

    public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
        Minecraft m = Minecraft.getInstance();
        Font font = m.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int yImage = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(stack, this.x, this.y, 0, 46 + yImage * 20, this.width / 2, this.height);
        this.blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + yImage * 20, this.width / 2, this.height);
        this.renderBg(stack, m, mouseX, mouseY);
        int color = this.active ? activeTextColor : textColor;
        drawCenteredString(stack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, color | Mth.ceil(this.alpha * 255.0F) << 24);

        if (this.isHoveredOrFocused()) {
            this.renderToolTip(stack, mouseX, mouseY);
        }
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setActiveTextColor(int activeTextColor) {
        this.activeTextColor = activeTextColor;
    }
}
