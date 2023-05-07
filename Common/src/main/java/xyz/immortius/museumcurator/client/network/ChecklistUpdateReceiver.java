package xyz.immortius.museumcurator.client.network;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.network.ChecklistUpdateMessage;

/**
 * Processes ChecklistUpdateMessage to update a client's checked items.
 */
public class ChecklistUpdateReceiver {
    public static void receive(LocalPlayer player, ChecklistUpdateMessage msg) {
        if (msg.isClearAll()) {
            MuseumCollections.clearCheckedItems();
        }
        MuseumCollections.uncheckItems(msg.getUncheckedItems());
        MuseumCollections.checkItems(msg.getCheckedItems());
        for (Item item : msg.getCheckedItems()) {
            player.sendMessage(new TranslatableComponent("commands.museumcurator.checked", item.getDefaultInstance().getDisplayName()), null);
        }
        for (Item item : msg.getUncheckedItems()) {
            player.sendMessage(new TranslatableComponent("commands.museumcurator.unchecked", item.getDefaultInstance().getDisplayName()), null);
        }
    }
}
