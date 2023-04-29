package xyz.immortius.museumcurator.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import xyz.immortius.museumcurator.client.uielements.ChecklistContainerWidget;
import xyz.immortius.museumcurator.common.data.MuseumCollection;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;

import java.util.List;

public class ChecklistCollectionScreen extends AbstractChecklistScreen {
    private final MuseumCollection collection;
    private ChecklistContainerWidget containerWidget;

    public ChecklistCollectionScreen(MuseumChecklistMenu menu, Inventory inventory, Component title, MuseumCollection collection, Screen lastScreen) {
        super(menu, inventory, title, lastScreen);
        this.collection = collection;
    }

    @Override
    protected void init() {
        super.init();
        containerWidget = new ChecklistContainerWidget( leftPos + 16, topPos + 46, 226, 173, collection);
        addRenderableWidget(containerWidget);
    }

    @Override
    protected void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
        if (containerWidget.isMouseOver(mouseX, mouseY)) {
            Component tooltip = containerWidget.getTooltip(mouseX, mouseY);
            if (tooltip != null) {
                renderTooltip(stack, tooltip, mouseX, mouseY);
            }
        }
    }

}
