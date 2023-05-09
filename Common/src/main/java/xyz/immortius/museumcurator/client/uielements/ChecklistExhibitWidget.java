package xyz.immortius.museumcurator.client.uielements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import xyz.immortius.museumcurator.client.screens.AbstractChecklistScreen;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.data.MuseumExhibit;
import xyz.immortius.museumcurator.common.network.ChecklistChangeRequest;
import xyz.immortius.museumcurator.interop.Services;

import java.util.Collections;
import java.util.List;

/**
 * Widget for displaying an exhibit, including title and contents. Clicking an item will toggle its checked state
 */
public class ChecklistExhibitWidget extends AbstractWidget {

    public static final int TITLE_HEIGHT = 12;
    public final MuseumExhibit exhibit;

    public ChecklistExhibitWidget(int x, int y, int width, int height, MuseumExhibit exhibit) {
        super(x, y, width, height, exhibit.getName());
        this.exhibit = exhibit;
    }

    public List<Component> getTooltip(int mouseX, int mouseY) {
        Item item = mouseOverItem(mouseX, mouseY);
        if (item != null) {
            return item.getDefaultInstance().getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL);
        }
        return null;
    }

    private Item mouseOverItem(int mouseX, int mouseY) {
        if (mouseY < getY() + TITLE_HEIGHT) {
            return null;
        }
        if ((mouseX - getX()) % 20 >= 18 || (mouseY - getY() - TITLE_HEIGHT) % 20 >= 18) {
            return null;
        }
        int row = (mouseY - getY() - TITLE_HEIGHT) / 20;
        int column = (mouseX - getX()) / 20;

        int itemsPerRow = itemsPerRow();
        int index = itemsPerRow * row + column;
        if (index >= 0 && index < exhibit.getItems().size()) {
            return exhibit.getItems().get(index);
        }
        return null;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        if (visible) {
            Minecraft minecraft = Minecraft.getInstance();
            int offsetY = 0;
            int offsetX = 0;

            long checked = MuseumCollections.countChecked(exhibit.getItems());
            Component title = Component.literal("").append(exhibit.getName()).append("  (" + checked + " / " + exhibit.getItems().size() + ")");
            minecraft.font.draw(stack, title, getX() + offsetX, getY() + offsetY, 0x404040);

            offsetY += TITLE_HEIGHT;
            for (Item item : exhibit.getItems()) {
                if (offsetX + 18 > width) {
                    offsetX = 0;
                    offsetY += 20;
                }
                minecraft.getItemRenderer().renderGuiItem(stack, item.getDefaultInstance(), getX() + offsetX + 1, getY() + offsetY + 1);
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, AbstractChecklistScreen.CONTAINER_TEXTURE);

                boolean unlocked = MuseumCollections.isChecked(item);
                blit(stack, offsetX + getX(), offsetY + getY(), (unlocked) ? 18 : 0, 256, 18, 18, AbstractChecklistScreen.TEXTURE_DIM, AbstractChecklistScreen.TEXTURE_DIM);
                offsetX += 20;
            }
        }
    }

    @Override
    public void renderWidget(PoseStack var1, int var2, int var3, float var4) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        Item item = mouseOverItem((int)mouseX, (int)mouseY);
        if (item != null) {
            if (MuseumCollections.isChecked(item)) {
                MuseumCollections.uncheckItems(Collections.singleton(item));
                Services.PLATFORM.sendClientChecklistChange(ChecklistChangeRequest.uncheck(item));

            } else {
                MuseumCollections.checkItems(Collections.singleton(item));
                Services.PLATFORM.sendClientChecklistChange(ChecklistChangeRequest.check(item));
            }
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput var1) {

    }

    public int calcHeight() {
        int itemsPerRow = itemsPerRow();
        return 12 + 20 * (exhibit.getItems().size() / itemsPerRow + Integer.signum(exhibit.getItems().size() % itemsPerRow)) + 2;
    }

    private int itemsPerRow() {
        int itemsPerRow = width / 20;
        if (width - itemsPerRow * 20 >= 18) {
            itemsPerRow++;
        }
        return itemsPerRow;
    }

}
