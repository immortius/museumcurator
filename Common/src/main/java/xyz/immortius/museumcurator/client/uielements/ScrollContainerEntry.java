package xyz.immortius.museumcurator.client.uielements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface ScrollContainerEntry extends GuiEventListener {

    void render(PoseStack stack, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta);

    int getHeight(int width);

    List<Component> getTooltip(int mouseX, int mouseY);
}

