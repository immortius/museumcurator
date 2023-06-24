package xyz.immortius.museumcurator.common.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * A museum exhibit is a group of strongly themed items that would make sense to display together
 */
public class MuseumExhibit {

    public static final Codec<MuseumExhibit> NET_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(x -> x.getName().getKey()),
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(MuseumExhibit::getItems)
    ).apply(instance, MuseumExhibit::new));

    private final TranslatableComponent name;
    private final List<ItemStack> items;

    public MuseumExhibit(String name, List<ItemStack> items) {
        this.name = new TranslatableComponent(name);
        this.items = items;
    }

    public TranslatableComponent getName() {
        return name;
    }

    public String getRawName() {
        return name.getKey();
    }

    public List<ItemStack> getItems() {
        return items;
    }

}
