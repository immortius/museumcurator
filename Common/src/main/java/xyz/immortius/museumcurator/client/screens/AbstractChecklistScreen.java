package xyz.immortius.museumcurator.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;

import java.util.Optional;

/**
 * Shared base for the checklist screens. Includes the common look and feel, and common elements.
 */
public abstract class AbstractChecklistScreen extends AbstractContainerScreen<MuseumChecklistMenu> {
    public static final ResourceLocation CONTAINER_TEXTURE = new ResourceLocation(MuseumCuratorConstants.MOD_ID + ":textures/gui/container/checklist.png");
    public static final int TEXTURE_DIM = 512;
    protected final Inventory playerInventory;
    private final Screen lastScreen;

    public AbstractChecklistScreen(MuseumChecklistMenu menu, Inventory inventory, Component title, Screen lastScreen) {
        super(menu, inventory, title);

        imageWidth = 256;
        imageHeight = 256;

        titleLabelX = 14;
        titleLabelY = 34;
        this.playerInventory = inventory;
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();

        Button closeButton = Button.builder(Component.translatable("gui.back"), button -> {
            this.minecraft.setScreen(lastScreen);
        }).bounds((width - imageWidth) / 2 + 96, topPos + 222, 64, 20).build();
        addRenderableWidget(closeButton);
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int width = this.font.width(this.title);
        graphics.drawString(this.font, this.title, (imageWidth - width) / 2, this.titleLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
        renderContent(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }

    public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float delta) {

    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        graphics.blit(CONTAINER_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, TEXTURE_DIM, TEXTURE_DIM);
    }

    @Override
    public Optional<GuiEventListener> getChildAt(double $$0, double $$1) {
        return super.getChildAt($$0, $$1);
    }

    @Override
    public void mouseMoved(double $$0, double $$1) {
        super.mouseMoved($$0, $$1);
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        return super.mouseScrolled($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean mouseDragged(double p_94699_, double p_94700_, int p_94701_, double p_94702_, double p_94703_) {
        return this.getFocused() != null && this.isDragging() && p_94701_ == 0 ? this.getFocused().mouseDragged(p_94699_, p_94700_, p_94701_, p_94702_, p_94703_) : false;
    }

    @Override
    public boolean keyReleased(int $$0, int $$1, int $$2) {
        return super.keyReleased($$0, $$1, $$2);
    }

    @Override
    public boolean charTyped(char $$0, int $$1) {
        return super.charTyped($$0, $$1);
    }

    @Override
    public void setInitialFocus(@Nullable GuiEventListener $$0) {
        super.setInitialFocus($$0);
    }

    @Override
    public void magicalSpecialHackyFocus(@Nullable GuiEventListener $$0) {
        super.magicalSpecialHackyFocus($$0);
    }

}
