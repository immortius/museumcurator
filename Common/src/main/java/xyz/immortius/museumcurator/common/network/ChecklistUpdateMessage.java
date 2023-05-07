package xyz.immortius.museumcurator.common.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Message from the Server to clients to update the list of checked items
 */
public class ChecklistUpdateMessage {
    public static Codec<ChecklistUpdateMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.ITEM.byNameCodec().listOf().fieldOf("checkedItems").forGetter(ChecklistUpdateMessage::getCheckedItems),
            Registry.ITEM.byNameCodec().listOf().fieldOf("uncheckedItems").forGetter(ChecklistUpdateMessage::getUncheckedItems),
            Codec.BOOL.fieldOf("clearAll").forGetter(ChecklistUpdateMessage::isClearAll)
    ).apply(instance, ChecklistUpdateMessage::new));

    private final List<Item> checkedItems;
    private final List<Item> uncheckedItems;
    private final boolean clearAll;

    public static ChecklistUpdateMessage check(Item item) {
        return new ChecklistUpdateMessage(Collections.singletonList(item), Collections.emptyList(), false);
    }

    public static ChecklistUpdateMessage check(Collection<Item> items) {
        return new ChecklistUpdateMessage(new ArrayList<>(items), Collections.emptyList(), false);
    }

    public static ChecklistUpdateMessage uncheck(Item item) {
        return new ChecklistUpdateMessage(Collections.emptyList(), Collections.singletonList(item), false);
    }

    public static ChecklistUpdateMessage uncheck(Collection<Item> items) {
        return new ChecklistUpdateMessage(Collections.emptyList(), new ArrayList<>(items), false);
    }

    public static ChecklistUpdateMessage uncheckAll() {
        return new ChecklistUpdateMessage(Collections.emptyList(), Collections.emptyList(), true);
    }

    private ChecklistUpdateMessage(List<Item> checked, List<Item> unchecked, boolean clearAll) {
        this.checkedItems = checked;
        this.uncheckedItems = unchecked;
        this.clearAll = clearAll;
    }

    public List<Item> getCheckedItems() {
        return checkedItems;
    }

    public List<Item> getUncheckedItems() {
        return uncheckedItems;
    }

    public boolean isClearAll() {
        return clearAll;
    }
}
