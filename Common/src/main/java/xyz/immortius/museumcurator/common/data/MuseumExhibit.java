package xyz.immortius.museumcurator.common.data;

import com.mojang.datafixers.FunctionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
