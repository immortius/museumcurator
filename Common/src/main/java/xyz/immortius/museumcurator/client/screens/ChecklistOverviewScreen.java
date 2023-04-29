package xyz.immortius.museumcurator.client.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import xyz.immortius.museumcurator.common.data.MuseumCollection;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.menus.MuseumChecklistMenu;

import java.util.List;

public class ChecklistOverviewScreen extends AbstractChecklistScreen {


    public ChecklistOverviewScreen(MuseumChecklistMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, null);
    }

    @Override
    protected void init() {
        super.init();

        int yOffset = 50;
        for (MuseumCollection group : MuseumCollections.getCollections()) {
            Button groupButton = new Button((width - imageWidth) / 2 + 64, topPos + yOffset, 128, 20, group.getName(), button -> {
                this.minecraft.setScreen(new ChecklistCollectionScreen(menu, playerInventory, group.getName(), group, this));
            });
            addRenderableWidget(groupButton);
            yOffset += 24;
        }
    }

}
