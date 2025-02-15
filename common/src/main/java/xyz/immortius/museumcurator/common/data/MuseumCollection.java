package xyz.immortius.museumcurator.common.data;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

/**
 * A museum collection is a series of one or more exhibits with a common theme.
 */
public class MuseumCollection {

    public static final StreamCodec<RegistryFriendlyByteBuf, MuseumCollection> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, MuseumCollection::getRawName, MuseumExhibit.LIST_STREAM_CODEC, MuseumCollection::getExhibits, MuseumCollection::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, List<MuseumCollection>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
            ByteBufCodecs.collection(NonNullList::createWithCapacity)
    );

    private final String name;
    private final List<MuseumExhibit> exhibits;

    public MuseumCollection(String name, List<MuseumExhibit> exhibits) {
        this.name = name;
        this.exhibits = new ArrayList<>(exhibits);
    }

    public Component getName() {
        return Component.translatable(name);
    }

    public String getRawName() { return name; }

    public List<MuseumExhibit> getExhibits() {
        return exhibits;
    }
}
