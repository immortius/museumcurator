package xyz.immortius.museumcurator.client.uielements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ShapedButton extends AbstractWidget {

    private final ResourceLocation texture;
    private final int textureDim;
    private final int normalX;
    private final int normalY;
    private final int overX;
    private final int overY;
    private final Consumer<ShapedButton> onClickHandler;

    public ShapedButton(int x, int y, int width, int height, Component label, ResourceLocation texture, int textureDim, int normalX, int normalY, int overX, int overY, Consumer<ShapedButton> onClick) {
        super(x, y, width, height, label);
        this.texture = texture;
        this.textureDim = textureDim;
        this.normalX = normalX;
        this.normalY = normalY;
        this.overX = overX;
        this.overY = overY;
        this.onClickHandler = onClick;
    }

    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float delta) {
        int texX, texY;
        if (isMouseOver(mouseX, mouseY)) {
            texX = overX;
            texY = overY;
        } else {
            texX = normalX;
            texY = normalY;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        blit(stack, getX(), getY(), texX, texY, getWidth(), getHeight(), textureDim, textureDim);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        onClickHandler.accept(this);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput var1) {

    }


}
