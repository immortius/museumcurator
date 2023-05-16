package xyz.immortius.museumcurator.common.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A request from the client to the server to update checked items.
 */
public class ChecklistChangeRequest {
    public static Codec<ChecklistChangeRequest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("checkedItems").forGetter(ChecklistChangeRequest::getCheckedItems),
            ItemStack.CODEC.listOf().fieldOf("uncheckedItems").forGetter(ChecklistChangeRequest::getUncheckedItems)
    ).apply(instance, ChecklistChangeRequest::new));

    private final List<ItemStack> checkedItems;
    private final List<ItemStack> uncheckedItems;

    public static ChecklistChangeRequest check(ItemStack item) {
        return new ChecklistChangeRequest(Collections.singletonList(item), Collections.emptyList());
    }

    public static ChecklistChangeRequest check(Collection<ItemStack> items) {
        return new ChecklistChangeRequest(new ArrayList<>(items), Collections.emptyList());
    }

    public static ChecklistChangeRequest uncheck(ItemStack item) {
        return new ChecklistChangeRequest(Collections.emptyList(), Collections.singletonList(item));
    }

    public static ChecklistChangeRequest uncheck(Collection<ItemStack> items) {
        return new ChecklistChangeRequest(Collections.emptyList(), new ArrayList<>(items));
    }

    private ChecklistChangeRequest(List<ItemStack> checked, List<ItemStack> unchecked) {
        this.checkedItems = checked;
        this.uncheckedItems = unchecked;
    }

    public List<ItemStack> getCheckedItems() {
        return checkedItems;
    }

    public List<ItemStack> getUncheckedItems() {
        return uncheckedItems;
    }

}
