package xyz.immortius.museumcurator.client.uielements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Scroll container is a scrollable area for entries of variable height.
 */
public class ScrollContainerWidget extends AbstractContainerEventHandler implements Widget, NarratableEntry {

    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_SPACE = 2;

    private int x;
    private int y;
    private int width;
    private int height;
    private double scrollAmount;
    private final List<ScrollContainerEntry> children = new ArrayList<>();
    private ScrollContainerEntry hovered;
    private boolean scrolling;

    public ScrollContainerWidget(int x, int y, int width, int height, List<? extends ScrollContainerEntry> entries) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.children.addAll(entries);
    }

    public void addEntry(ScrollContainerEntry entry) {
        children.add(entry);
    }

    private void setScrollAmount(double amount) {
        scrollAmount = Mth.clamp(amount, 0, getMaxScroll());
    }

    private ScrollContainerEntry getEntryAtPosition(double mouseX, double mouseY) {
        if (mouseX < this.x || mouseX > this.x + width - SCROLLBAR_WIDTH - SCROLLBAR_SPACE || mouseY < y || mouseY > y + height) {
            return null;
        }
        double absolutePos = mouseY + scrollAmount - y;
        int totalY = 0;
        for (ScrollContainerEntry entry : children) {
            int entryHeight = entry.getHeight(width - SCROLLBAR_WIDTH - SCROLLBAR_SPACE);
            if (totalY < absolutePos && totalY + entryHeight >= absolutePos) {
                return entry;
            }
            totalY += entryHeight;
        }
        return null;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        this.hovered = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;

        this.renderList(stack, mouseX, mouseY, delta);
        renderScrollbar(tesselator, bufferbuilder);

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public List<Component> getTooltip(int mouseX, int mouseY) {
        if (hovered != null) {
            return hovered.getTooltip(mouseX, mouseY);
        }
        return Collections.emptyList();
    }

    private void renderScrollbar(Tesselator tesselator, BufferBuilder bufferbuilder) {
        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0) {
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            int scrollerSize = (int)((float)((height) * (height)) / (float)this.getTotalListHeight());
            scrollerSize = Mth.clamp(scrollerSize, 32, height - 8);
            int scrollerPos = (int)this.scrollAmount * (height - scrollerSize) / maxScroll + y;
            if (scrollerPos < y) {
                scrollerPos = y;
            }

            int startX = x + width - SCROLLBAR_WIDTH;
            int endX = x + width;

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferbuilder.vertex(startX, y + height, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(endX, y + height, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(endX, y, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(startX, y, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(startX, scrollerPos + scrollerSize, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(endX, scrollerPos + scrollerSize, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(endX, scrollerPos, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(startX, scrollerPos, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(startX, scrollerPos + scrollerSize - 1, 0.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex(endX - 1, scrollerPos + scrollerSize - 1, 0.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex(endX - 1, scrollerPos, 0.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex(startX, scrollerPos, 0.0D).color(192, 192, 192, 255).endVertex();
            tesselator.end();
        }
    }

    private int getMaxScroll() {
        return Math.max(0, getTotalListHeight() - height);
    }

    private int getTotalListHeight() {
        return children.stream().map(x -> x.getHeight(width)).reduce(0, Integer::sum);
    }

    protected void renderList(PoseStack stack, int mouseX, int mouseY, float delta) {
        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        RenderSystem.enableScissor((int) (scale * x), (int) (scale * (Minecraft.getInstance().getWindow().getGuiScaledHeight() - height - y)), (int) (scale * width), (int) (scale * height));
        int absoluteY = 0;
        for (ScrollContainerEntry child : children) {
            int rowBottom = absoluteY + child.getHeight(width - SCROLLBAR_WIDTH - SCROLLBAR_SPACE);
            if (rowBottom >= scrollAmount && absoluteY <= scrollAmount + height) {
                child.render(stack, y + absoluteY - (int) scrollAmount, x, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACE, rowBottom - absoluteY, mouseX, mouseY, Objects.equals(this.hovered, child), delta);
            }
            absoluteY = rowBottom;
        }
        RenderSystem.disableScissor();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        this.scrolling = button == 0 && mouseX >= (double) (x + width - SCROLLBAR_WIDTH) && mouseX < (double)(x + width);
        this.setDragging(true);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            ScrollContainerEntry e = this.getEntryAtPosition(mouseX, mouseY);
            if (e != null) {
                if (e.mouseClicked(mouseX, mouseY, button)) {
                    this.setFocused(e);
                    this.setDragging(true);
                    return true;
                }
            }

            return this.scrolling;
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(mouseX, mouseY, button);
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        } else if (button == 0 && this.scrolling) {
            if (mouseY < (double)this.y) {
                setScrollAmount(0.0D);
            } else if (mouseY > (double)this.y + height) {
                setScrollAmount(this.getMaxScroll());
            } else {
                double d0 = Math.max(1, this.getMaxScroll());
                int j = Mth.clamp((int)((float)(height * height) / (float)this.getTotalListHeight()), 32, height - 8);
                double d1 = Math.max(1.0D, d0 / (double)(height - j));
                setScrollAmount(scrollAmount + deltaY * d1);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta) {
        setScrollAmount(scrollAmount - wheelDelta * 20);
        return true;
    }

    public boolean keyPressed(int p_93434_, int p_93435_, int p_93436_) {
        if (super.keyPressed(p_93434_, p_93435_, p_93436_)) {
            return true;
        } else if (p_93434_ == 264) {
            setScrollAmount(scrollAmount - 20);
            return true;
        } else if (p_93434_ == 265) {
            setScrollAmount(scrollAmount + 20);
            return true;
        } else {
            return false;
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY >= y && mouseY <= (y + height) && mouseX >= x && mouseX <= x + width;
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {

    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

}
