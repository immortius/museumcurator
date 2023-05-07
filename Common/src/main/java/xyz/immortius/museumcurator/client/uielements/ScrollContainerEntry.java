package xyz.immortius.museumcurator.client.uielements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Interface for entries to display in a scroll container widget
 */
public interface ScrollContainerEntry extends GuiEventListener {

    /**
     * Renders the contents of the entry
     * @param stack
     * @param top
     * @param left
     * @param width
     * @param height
     * @param mouseX
     * @param mouseY
     * @param hovered
     * @param delta
     */
    void render(PoseStack stack, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta);

    /**
     * @param width The width available to render the entry
     * @return The height of the entry, given the specified width
     */
    int getHeight(int width);

    /**
     * @param mouseX
     * @param mouseY
     * @return The tooltip for entry
     */
    List<Component> getTooltip(int mouseX, int mouseY);
}

