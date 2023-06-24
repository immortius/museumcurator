package xyz.immortius.museumcurator.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * A museum collection is a series of one or more exhibits with a common theme.
 */
public class MuseumCollection {

    public static final Codec<MuseumCollection> NET_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(x -> x.getName().getKey()),
            MuseumExhibit.NET_CODEC.listOf().fieldOf("exhibits").forGetter(MuseumCollection::getExhibits)
    ).apply(instance, MuseumCollection::new));

    private final TranslatableComponent name;
    private final List<MuseumExhibit> exhibits;

    public MuseumCollection(String name, List<MuseumExhibit> exhibits) {
        this.name = new TranslatableComponent(name);
        this.exhibits = new ArrayList<>(exhibits);
    }

    public TranslatableComponent getName() {
        return name;
    }

    public List<MuseumExhibit> getExhibits() {
        return exhibits;
    }
}
