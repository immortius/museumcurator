package xyz.immortius.museumcurator.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;

import java.util.List;

/**
 * A museum exhibit is a group of strongly themed items that would make sense to display together
 */
public class MuseumExhibit {
    public static final Codec<MuseumExhibit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(x -> x.getName().getKey()),
            Registry.ITEM.byNameCodec().listOf().fieldOf("items").forGetter(MuseumExhibit::getItems)
    ).apply(instance, MuseumExhibit::new));

    private final TranslatableComponent name;
    private final List<Item> items;

    public MuseumExhibit(String name, List<Item> items) {
        this.name = new TranslatableComponent(name);
        this.items = items;
    }

    public TranslatableComponent getName() {
        return name;
    }

    public List<Item> getItems() {
        return items;
    }
}
