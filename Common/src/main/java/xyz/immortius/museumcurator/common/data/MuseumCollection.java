package xyz.immortius.museumcurator.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * A museum collection is a series of one or more exhibits with a common theme.
 */
public class MuseumCollection {

    public static final Codec<MuseumCollection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(x -> x.name),
            MuseumExhibit.CODEC.listOf().fieldOf("exhibits").forGetter(MuseumCollection::getExhibits)
    ).apply(instance, MuseumCollection::new));

    private final String name;
    private final List<MuseumExhibit> exhibits;

    public MuseumCollection(String name, List<MuseumExhibit> exhibits) {
        this.name = name;
        this.exhibits = exhibits;
    }

    public Component getName() {
        return Component.translatable(name);
    }

    public List<MuseumExhibit> getExhibits() {
        return exhibits;
    }
}
