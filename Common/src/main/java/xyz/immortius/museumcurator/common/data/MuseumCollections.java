package xyz.immortius.museumcurator.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.item.Item;

import java.util.*;

/**
 * Static singleton for holding museum information. The collections are loaded both server and client side, but the
 * collected items are only used client side (server-side {@link xyz.immortius.museumcurator.server.ChecklistState} is used instead)
 * TODO: Split the client-side only part into a different class
 */
public final class MuseumCollections {

    private static List<MuseumCollection> collections = Collections.emptyList();
    private static Set<Item> collectionItems = Collections.emptySet();
    private static final Set<Item> checkedItems = new LinkedHashSet<>();

    private MuseumCollections() {}

    /**
     * @return All collections
     */
    public static List<MuseumCollection> getCollections() {
        return collections;
    }

    /**
     * @return All items that are in collection
     */
    public static Set<Item> getAllCollectionItems() {
        return collectionItems;
    }

    /**
     * Sets the available collections
     * @param newCollections
     */
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

    /**
     * Sets all checked items
     * @param checkedItems
     */
    public static void setCheckedItems(Collection<Item> checkedItems) {
        MuseumCollections.checkedItems.clear();
        MuseumCollections.checkedItems.addAll(checkedItems);
    }

    /**
     * Check an item
     * @param items
     */
    public static void checkItems(Collection<Item> items) {
        checkedItems.addAll(items);
    }

    /**
     * Uncheck an item
     * @param items
     */
    public static void uncheckItems(Collection<Item> items) {
        checkedItems.removeAll(items);
    }

    /**
     * Clear all checked items
     */
    public static void clearCheckedItems() {
        checkedItems.clear();
    }

    /**
     * @param item
     * @return Whether the item has been checked
     */
    public static boolean isChecked(Item item) {
        return checkedItems.contains(item);
    }

    /**
     * @param items
     * @return The count of how many of the provided items are checked
     */
    public static long countChecked(List<Item> items) {
        return items.stream().filter(checkedItems::contains).count();
    }
}
