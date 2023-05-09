package xyz.immortius.museumcurator.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import xyz.immortius.museumcurator.client.uielements.CollectionButton;
import xyz.immortius.museumcurator.client.uielements.ColorTextButton;
import xyz.immortius.museumcurator.client.uielements.ScrollContainerEntry;
import xyz.immortius.museumcurator.client.uielements.ShapedButton;
import xyz.immortius.museumcurator.common.data.MuseumCollection;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The screen showing the list of available collections
 */
public class ChecklistOverviewScreen extends AbstractChecklistScreen {

    private static final int ITEMS_PER_PAGE = 7;

    private int pages = 0;
    private int currentPage = 0;

    private List<CollectionButton> collectionButtons = new ArrayList<>();
    private ShapedButton nextButton;
    private ShapedButton prevButton;


    public ChecklistOverviewScreen(MuseumChecklistMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, null);
    }

    @Override
    protected void init() {
        super.init();

        pages = MuseumCollections.getCollections().size() / ITEMS_PER_PAGE + Integer.signum(MuseumCollections.getCollections().size() % ITEMS_PER_PAGE);

        collectionButtons.clear();
        int index = 0;
        for (MuseumCollection collection : MuseumCollections.getCollections()) {
            collectionButtons.add(new CollectionButton(leftPos + 16 + 226 / 2 - 96, topPos + 46 + 2 + 24 * (index++ % ITEMS_PER_PAGE), 192, 20, collection, button ->
                    Minecraft.getInstance().setScreen(new ChecklistCollectionScreen(menu, playerInventory, collection.getName(), collection, this))));
        }

        prevButton = new ShapedButton(leftPos + 14, topPos + 226, 8, 13, Component.empty(), AbstractChecklistScreen.CONTAINER_TEXTURE, AbstractChecklistScreen.TEXTURE_DIM, 54, 256, 54, 269, (b) -> currentPage--);
        nextButton = new ShapedButton(leftPos + 232, topPos + 226, 8, 13, Component.empty(), AbstractChecklistScreen.CONTAINER_TEXTURE, AbstractChecklistScreen.TEXTURE_DIM, 62, 256, 62, 269, (b) -> currentPage++);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        super.render(stack, mouseX, mouseY, delta);
        for (int i = currentPage * ITEMS_PER_PAGE; i < Math.min(collectionButtons.size(), (currentPage + 1) * ITEMS_PER_PAGE); i++) {
            collectionButtons.get(i).render(stack, mouseX, mouseY, delta);
        }

        if (currentPage > 0) {
            prevButton.render(stack, mouseX, mouseY, delta);
        }
        if ((currentPage + 1) * ITEMS_PER_PAGE < collectionButtons.size()) {
            nextButton.render(stack, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.minecraft.options.keyLeft.matches($$0, $$1) && currentPage > 0) {
            currentPage--;
            return true;
        } else if (this.minecraft.options.keyRight.matches($$0, $$1) && (currentPage + 1) * ITEMS_PER_PAGE < collectionButtons.size()){
            currentPage++;
            return true;
        }

        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = currentPage * ITEMS_PER_PAGE; i < Math.min(collectionButtons.size(), (currentPage + 1) * ITEMS_PER_PAGE); i++) {
            if (collectionButtons.get(i).isMouseOver(mouseX, mouseY)) {
                if (collectionButtons.get(i).mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        if (currentPage > 0 && prevButton.isMouseOver(mouseX, mouseY)) {
            return prevButton.mouseClicked(mouseX, mouseY, button);
        }
        if ((currentPage + 1) * ITEMS_PER_PAGE < collectionButtons.size() && nextButton.isMouseOver(mouseX, mouseY)) {
            return nextButton.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

}
