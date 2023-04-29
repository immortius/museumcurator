package xyz.immortius.museumcurator.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public class MuseumCollection {

    public static final Codec<MuseumCollection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(x -> x.getName().getString()),
            MuseumExhibit.CODEC.listOf().fieldOf("exhibits").forGetter(MuseumCollection::getExhibits)
    ).apply(instance, MuseumCollection::new));

    private final TextComponent name;
    private final List<MuseumExhibit> exhibits;

    public MuseumCollection(String name, List<MuseumExhibit> exhibits) {
        this.name = new TextComponent(name);
        this.exhibits = exhibits;
    }

    public TextComponent getName() {
        return name;
    }

    public List<MuseumExhibit> getExhibits() {
        return exhibits;
    }
}
