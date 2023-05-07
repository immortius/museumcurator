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
 * A request from the client to the server to update checked items.
 */
public class ChecklistChangeRequest {
    public static Codec<ChecklistChangeRequest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.ITEM.byNameCodec().listOf().fieldOf("checkedItems").forGetter(ChecklistChangeRequest::getCheckedItems),
            Registry.ITEM.byNameCodec().listOf().fieldOf("uncheckedItems").forGetter(ChecklistChangeRequest::getUncheckedItems)
    ).apply(instance, ChecklistChangeRequest::new));

    private final List<Item> checkedItems;
    private final List<Item> uncheckedItems;

    public static ChecklistChangeRequest check(Item item) {
        return new ChecklistChangeRequest(Collections.singletonList(item), Collections.emptyList());
    }

    public static ChecklistChangeRequest check(Collection<Item> items) {
        return new ChecklistChangeRequest(new ArrayList<>(items), Collections.emptyList());
    }

    public static ChecklistChangeRequest uncheck(Item item) {
        return new ChecklistChangeRequest(Collections.emptyList(), Collections.singletonList(item));
    }

    public static ChecklistChangeRequest uncheck(Collection<Item> items) {
        return new ChecklistChangeRequest(Collections.emptyList(), new ArrayList<>(items));
    }

    private ChecklistChangeRequest(List<Item> checked, List<Item> unchecked) {
        this.checkedItems = checked;
        this.uncheckedItems = unchecked;
    }

    public List<Item> getCheckedItems() {
        return checkedItems;
    }

    public List<Item> getUncheckedItems() {
        return uncheckedItems;
    }

}
