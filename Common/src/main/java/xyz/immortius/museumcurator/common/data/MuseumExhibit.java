package xyz.immortius.museumcurator.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;

import java.util.List;

public class MuseumExhibit {
    public static final Codec<MuseumExhibit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(x -> x.getName().getString()),
            Registry.ITEM.byNameCodec().listOf().fieldOf("items").forGetter(MuseumExhibit::getItems)
    ).apply(instance, MuseumExhibit::new));

    private final TextComponent name;
    private final List<Item> items;

    public MuseumExhibit(String name, List<Item> items) {
        this.name = new TextComponent(name);
        this.items = items;
    }

    public TextComponent getName() {
        return name;
    }

    public List<Item> getItems() {
        return items;
    }
}
