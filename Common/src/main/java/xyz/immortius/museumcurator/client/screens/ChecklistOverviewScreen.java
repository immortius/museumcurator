package xyz.immortius.museumcurator.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import xyz.immortius.museumcurator.client.uielements.ColorTextButton;
import xyz.immortius.museumcurator.client.uielements.ScrollContainerEntry;
import xyz.immortius.museumcurator.client.uielements.ScrollContainerWidget;
import xyz.immortius.museumcurator.common.data.MuseumCollection;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;

import java.util.Collections;
import java.util.List;

/**
 * The screen showing the list of available collections
 */
public class ChecklistOverviewScreen extends AbstractChecklistScreen {

    public ChecklistOverviewScreen(MuseumChecklistMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, null);
    }

    @Override
    protected void init() {
        super.init();

        List<ChecklistCollectionEntry> collectionEntries = MuseumCollections.getCollections().stream().map(x -> new ChecklistCollectionEntry(this, x)).toList();
        ScrollContainerWidget scrollContainer = new ScrollContainerWidget(leftPos + 16, topPos + 46, 226, 173, collectionEntries);
        addRenderableWidget(scrollContainer);
    }

    public class ChecklistCollectionEntry implements ScrollContainerEntry {

        private final MuseumCollection collection;
        private final ColorTextButton button;

        public ChecklistCollectionEntry(Screen parentScreen, MuseumCollection collection) {
            this.collection = collection;
            button = new ColorTextButton(0, 0, 192, 20, collection.getName(), button -> {
                Minecraft.getInstance().setScreen(new ChecklistCollectionScreen(menu, playerInventory, collection.getName(), collection, parentScreen));
            });
        }

        @Override
        public void render(PoseStack stack, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            button.x = left + width / 2 - 96;
            button.y = top + 2;
            int totalCount = collection.getExhibits().stream().map(x -> x.getItems().size()).reduce(0, Integer::sum);
            int unlockedCount = collection.getExhibits().stream().map(x -> MuseumCollections.countChecked(x.getItems())).reduce(0L, Long::sum).intValue();
            button.setMessage(new TextComponent("").append(collection.getName()).append(" (" + unlockedCount + " / " + totalCount + ")"));
            button.setActiveTextColor((unlockedCount == totalCount) ? 0xFFDD00 : 0xFFFFFF);
            button.render(stack, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            return button.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        public int getHeight(int width) {
            return 24;
        }

        @Override
        public List<Component> getTooltip(int mouseX, int mouseY) {
            return Collections.emptyList();
        }
    }

}
