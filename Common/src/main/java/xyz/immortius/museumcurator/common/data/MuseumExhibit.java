package xyz.immortius.museumcurator.common.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * A museum exhibit is a group of strongly themed items that would make sense to display together
 */
public class MuseumExhibit {

    public static final Codec<MuseumExhibit> NET_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(x -> x.name),
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(MuseumExhibit::getItems)
    ).apply(instance, MuseumExhibit::new));

    private final String name;
    private final List<ItemStack> items;

    public MuseumExhibit(String name, List<ItemStack> items) {
        this.name = name;
        this.items = items;
    }

    public Component getName() {
        return Component.translatable(name);
    }

    public String getRawName() {
        return name;
    }

    public List<ItemStack> getItems() {
        return items;
    }

}
