package xyz.immortius.museumcurator.common.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.immortius.museumcurator.common.data.MuseumCollection;

import java.util.List;

public class LogOnMessage {
    public static Codec<LogOnMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MuseumCollection.CODEC.listOf().fieldOf("collections").forGetter(LogOnMessage::getCollections)
    ).apply(instance, LogOnMessage::new));

    private List<MuseumCollection> collections;

    public LogOnMessage(List<MuseumCollection> collections) {
        this.collections = collections;
    }

    public List<MuseumCollection> getCollections() {
        return collections;
    }
}
