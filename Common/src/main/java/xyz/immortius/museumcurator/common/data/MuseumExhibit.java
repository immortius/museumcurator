package xyz.immortius.museumcurator.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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

    public record CollectionItem(ItemStack itemStack, DataComponentPatch componentFilter) {
        public static final StreamCodec<RegistryFriendlyByteBuf, CollectionItem> STREAM_CODEC = StreamCodec.composite(ItemStack.STREAM_CODEC, CollectionItem::itemStack, DataComponentPatch.STREAM_CODEC, CollectionItem::componentFilter, CollectionItem::new);

        public static final StreamCodec<RegistryFriendlyByteBuf, List<CollectionItem>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
                ByteBufCodecs.collection(NonNullList::createWithCapacity)
        );

        public static final Codec<CollectionItem> CODEC =
                Codec.withAlternative(
                        BuiltInRegistries.ITEM.byNameCodec().comapFlatMap(item -> DataResult.success(new CollectionItem(item.getDefaultInstance(), DataComponentPatch.EMPTY)), collectionItem -> collectionItem.itemStack.getItem()),
                        RecordCodecBuilder.create(instance -> instance.group(
                                BuiltInRegistries.ITEM.byNameCodec().xmap(Item::getDefaultInstance, ItemStack::getItem).fieldOf("id").forGetter(CollectionItem::itemStack),
                                DataComponentPatch.CODEC.fieldOf("tags").forGetter(CollectionItem::componentFilter)
                        ).apply(instance, CollectionItem::new))
                );

        public CollectionItem(ItemStack itemStack, DataComponentPatch componentFilter) {
            this.itemStack = itemStack;
            this.componentFilter = componentFilter;
            itemStack.applyComponents(componentFilter);
        }

    }
}
