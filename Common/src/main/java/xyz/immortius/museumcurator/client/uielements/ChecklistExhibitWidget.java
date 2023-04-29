package xyz.immortius.museumcurator.client.uielements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import xyz.immortius.museumcurator.client.screens.AbstractChecklistScreen;
import xyz.immortius.museumcurator.client.screens.ChecklistCollectionScreen;
import xyz.immortius.museumcurator.common.data.MuseumExhibit;

public class ChecklistExhibitWidget extends AbstractWidget {

    public static final int TITLE_HEIGHT = 12;
    public final MuseumExhibit exhibit;

    public ChecklistExhibitWidget(int x, int y, int width, int height, MuseumExhibit exhibit) {
        super(x, y, width, height, exhibit.getName());
        this.exhibit = exhibit;
    }

    public Component getTooltip(int mouseX, int mouseY) {
        if (mouseY < y + TITLE_HEIGHT) {
            return null;
        }
        if ((mouseX - x) % 20 >= 18 || (mouseY - y - TITLE_HEIGHT) % 20 >= 18) {
            return null;
        }
        int row = (mouseY - y - TITLE_HEIGHT) / 20;
        int column = (mouseX - x) / 20;

        int itemsPerRow = itemsPerRow();
        int index = itemsPerRow * row + column;
        if (index >= 0 && index < exhibit.getItems().size()) {
            return exhibit.getItems().get(index).getName(exhibit.getItems().get(index).getDefaultInstance());
        }
        return null;
    }

    @Override
    public void renderToolTip(PoseStack stack, int mouseX, int mouseY) {

    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        if (visible) {
            Minecraft minecraft = Minecraft.getInstance();
            int offsetY = 0;
            int offsetX = 0;
            minecraft.font.draw(stack, exhibit.getName(), x + offsetX, y + offsetY, 0x404040);
            offsetY += TITLE_HEIGHT;
            for (Item item : exhibit.getItems()) {
                if (offsetX + 18 > width) {
                    offsetX = 0;
                    offsetY += 20;
                }
                minecraft.getItemRenderer().renderGuiItem(item.getDefaultInstance(), x + offsetX + 1, y + offsetY + 1);
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, AbstractChecklistScreen.CONTAINER_TEXTURE);
                // TODO
                boolean unlocked = false;
                blit(stack, offsetX + x, offsetY + y, (unlocked) ? 18 : 0, 256, 18, 18, AbstractChecklistScreen.TEXTURE_DIM, AbstractChecklistScreen.TEXTURE_DIM);
                offsetX += 20;
            }
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {

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
