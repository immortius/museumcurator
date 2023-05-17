package xyz.immortius.museumcurator.client.uielements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import xyz.immortius.museumcurator.common.data.MuseumCollection;
import xyz.immortius.museumcurator.common.data.MuseumCollections;

public class CollectionButton extends ColorTextButton {

    private final MuseumCollection collection;

    public CollectionButton(int x, int y, int width, int height, MuseumCollection collection, OnPress onPress) {
        super(x, y, width, height, collection.getName(), onPress);
        this.collection = collection;
    }


    public CollectionButton(int x, int y, int width, int height, MuseumCollection collection, OnPress onPress, CreateNarration onTooltip) {
        super(x, y, width, height, collection.getName(), onPress, onTooltip);
        this.collection = collection;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int totalCount = collection.getExhibits().stream().map(x -> x.getItems().size()).reduce(0, Integer::sum);
        int unlockedCount = collection.getExhibits().stream().map(x -> MuseumCollections.countChecked(x.getItems())).reduce(0L, Long::sum).intValue();
        setMessage(Component.literal("").append(collection.getName()).append(" (" + unlockedCount + " / " + totalCount + ")"));
        setActiveTextColor((unlockedCount == totalCount) ? 0xFFDD00 : 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }
}
