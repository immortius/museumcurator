package xyz.immortius.museumcurator.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import xyz.immortius.museumcurator.client.uielements.ChecklistExhibitWidget;
import xyz.immortius.museumcurator.client.uielements.ScrollContainerEntry;
import xyz.immortius.museumcurator.client.uielements.ScrollContainerWidget;
import xyz.immortius.museumcurator.common.data.MuseumCollection;
import xyz.immortius.museumcurator.common.data.MuseumExhibit;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The screen displaying the exhibits for a collection.
 */
public class ChecklistCollectionScreen extends AbstractChecklistScreen {
    private final MuseumCollection collection;
    private ScrollContainerWidget containerWidget;

    public ChecklistCollectionScreen(MuseumChecklistMenu menu, Inventory inventory, Component title, MuseumCollection collection, Screen lastScreen) {
        super(menu, inventory, title, lastScreen);
        this.collection = collection;
    }

    @Override
    protected void init() {
        super.init();
        List<ChecklistExhibitEntry> entries = collection.getExhibits().stream().map(ChecklistExhibitEntry::new).toList();

        containerWidget = new ScrollContainerWidget( leftPos + 16, topPos + 46, 226, 173, entries);
        addRenderableWidget(containerWidget);
    }

    @Override
    protected void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
        if (containerWidget.isMouseOver(mouseX, mouseY)) {
            List<Component> tooltip = containerWidget.getTooltip(mouseX, mouseY);
            if (tooltip != null && !tooltip.isEmpty()) {
                renderTooltip(stack, tooltip, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    public static class ChecklistExhibitEntry implements ScrollContainerEntry {

        private ChecklistExhibitWidget sectionWidget;
        private List<AbstractWidget> widgets = new ArrayList<>();

        public ChecklistExhibitEntry(MuseumExhibit exhibit) {
            this.sectionWidget = new ChecklistExhibitWidget(0, 0, 0, 0, exhibit);
            widgets.add(sectionWidget);
        }

        @Override
        public void render(PoseStack stack, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            sectionWidget.x = left;
            sectionWidget.y = top;
            sectionWidget.setWidth(width);
            sectionWidget.render(stack, mouseX, mouseY, delta);
        }

        @Override
        public int getHeight(int width) {
            sectionWidget.setWidth(width);
            return sectionWidget.calcHeight();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return sectionWidget.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public List<Component> getTooltip(int mouseX, int mouseY) {
            return sectionWidget.getTooltip(mouseX, mouseY);
        }
    }


}
