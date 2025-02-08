package xyz.immortius.museumcurator.common.data;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * A museum exhibit is a group of strongly themed items that would make sense to display together
 */
public class MuseumExhibit {

    public static final StreamCodec<RegistryFriendlyByteBuf, MuseumExhibit> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, MuseumExhibit::getRawName, CollectionItem.LIST_STREAM_CODEC, MuseumExhibit::getItems, MuseumExhibit::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, List<MuseumExhibit>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
            ByteBufCodecs.collection(NonNullList::createWithCapacity)
    );

    private final String name;
    private final List<CollectionItem> items;

    public MuseumExhibit(String name, List<CollectionItem> items) {
        this.name = name;
        this.items = items;
    }

    public Component getName() {
        return Component.translatable(name);
    }

    public String getRawName() {
        return name;
    }

    public List<CollectionItem> getItems() {
        return items;
    }


}
