package xyz.immortius.museumcurator.common.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.common.data.MuseumCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Message from the Server to clients to update the list of checked items
 */
public class ChecklistUpdateMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChecklistUpdateMessage> ID = new CustomPacketPayload.Type<>(MuseumCuratorConstants.CHECKLIST_UPDATE_MESSAGE_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ChecklistUpdateMessage> STREAM_CODEC = StreamCodec.composite(ItemStack.LIST_STREAM_CODEC, ChecklistUpdateMessage::getCheckedItems, ItemStack.LIST_STREAM_CODEC, ChecklistUpdateMessage::getUncheckedItems, ByteBufCodecs.BOOL, ChecklistUpdateMessage::isClearAll, ChecklistUpdateMessage::new);

    private final List<ItemStack> checkedItems;
    private final List<ItemStack> uncheckedItems;
    private final boolean clearAll;

    public static ChecklistUpdateMessage check(ItemStack item) {
        return new ChecklistUpdateMessage(Collections.singletonList(item), Collections.emptyList(), false);
    }

    public static ChecklistUpdateMessage check(Collection<ItemStack> items) {
        return new ChecklistUpdateMessage(new ArrayList<>(items), Collections.emptyList(), false);
    }

    public static ChecklistUpdateMessage uncheck(ItemStack item) {
        return new ChecklistUpdateMessage(Collections.emptyList(), Collections.singletonList(item), false);
    }

    public static ChecklistUpdateMessage uncheck(Collection<ItemStack> items) {
        return new ChecklistUpdateMessage(Collections.emptyList(), new ArrayList<>(items), false);
    }

    public static ChecklistUpdateMessage uncheckAll() {
        return new ChecklistUpdateMessage(Collections.emptyList(), Collections.emptyList(), true);
    }

    private ChecklistUpdateMessage(List<ItemStack> checked, List<ItemStack> unchecked, boolean clearAll) {
        this.checkedItems = checked;
        this.uncheckedItems = unchecked;
        this.clearAll = clearAll;
    }

    public List<ItemStack> getCheckedItems() {
        return checkedItems;
    }

    public List<ItemStack> getUncheckedItems() {
        return uncheckedItems;
    }

    public boolean isClearAll() {
        return clearAll;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
