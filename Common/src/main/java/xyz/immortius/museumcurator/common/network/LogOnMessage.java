package xyz.immortius.museumcurator.common.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import xyz.immortius.museumcurator.common.data.MuseumCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Message from server to client on logon to provide the list of collections and all currently checked off items
 */
public class LogOnMessage {
    public static Codec<LogOnMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MuseumCollection.CODEC.listOf().fieldOf("collections").forGetter(LogOnMessage::getCollections),
            BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("checkedItem").forGetter(LogOnMessage::getCheckedItems)
    ).apply(instance, LogOnMessage::new));

    private final List<MuseumCollection> collections;
    private final List<Item> checkedItems;

    public LogOnMessage(List<MuseumCollection> collections, Collection<Item> checkedItems) {
        this.collections = collections;
        this.checkedItems = new ArrayList<>(checkedItems);
    }

    public List<MuseumCollection> getCollections() {
        return collections;
    }

    public List<Item> getCheckedItems() {
        return checkedItems;
    }
}
