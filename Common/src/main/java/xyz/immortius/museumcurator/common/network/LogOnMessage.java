package xyz.immortius.museumcurator.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;
import xyz.immortius.museumcurator.common.data.MuseumCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Message from server to client on logon to provide the list of collections and all currently checked off items
 */
public class LogOnMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LogOnMessage> ID = new CustomPacketPayload.Type<>(MuseumCuratorConstants.LOG_ON_MESSAGE_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, LogOnMessage> STREAM_CODEC = StreamCodec.composite(MuseumCollection.LIST_STREAM_CODEC, LogOnMessage::getCollections, ItemStack.LIST_STREAM_CODEC, LogOnMessage::getCheckedItems, LogOnMessage::new);

    private final List<MuseumCollection> collections;
    private final List<ItemStack> checkedItems;

    public LogOnMessage(List<MuseumCollection> collections, Collection<ItemStack> checkedItems) {
        this.collections = collections;
        this.checkedItems = new ArrayList<>(checkedItems);
    }

    public List<MuseumCollection> getCollections() {
        return collections;
    }

    public List<ItemStack> getCheckedItems() {
        return checkedItems;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}


