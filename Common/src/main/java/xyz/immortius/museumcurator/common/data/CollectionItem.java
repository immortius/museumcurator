package xyz.immortius.museumcurator.common.data;

import com.mojang.datafixers.FunctionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

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