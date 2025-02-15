package xyz.immortius.museumcurator.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A request from the client to the server to update checked items.
 */
public class ChecklistChangeRequest implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChecklistChangeRequest> ID = new CustomPacketPayload.Type<>(MuseumCuratorConstants.CHECKLIST_CHANGE_REQUEST_MESSAGE_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ChecklistChangeRequest> STREAM_CODEC = StreamCodec.composite(ItemStack.LIST_STREAM_CODEC, ChecklistChangeRequest::getCheckedItems, ItemStack.LIST_STREAM_CODEC, ChecklistChangeRequest::getUncheckedItems, ChecklistChangeRequest::new);

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

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
