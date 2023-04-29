package xyz.immortius.museumcurator.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class MuseumCollections {

    private static List<MuseumCollection> collections = Collections.emptyList();
    private static Set<Item> collectionItems = Collections.emptySet();

    private MuseumCollections() {}

    public static List<MuseumCollection> getCollections() {
        return collections;
    }

    public static Set<Item> getAllCollectionItems() {
        return collectionItems;
    }
    public static void setCollections(List<MuseumCollection> newCollections) {
        collections = ImmutableList.copyOf(newCollections);
        ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
        for (MuseumCollection collection : newCollections) {
            for (MuseumExhibit exhibit : collection.getExhibits()) {
                builder.addAll(exhibit.getItems());
            }
        }
        collectionItems = builder.build();
    }

}
