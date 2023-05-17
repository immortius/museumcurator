package xyz.immortius.museumcurator.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import net.minecraft.nbt.*;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * Static singleton for holding museum information. The collections are loaded both server and client side, but the
 * collected items are only used client side (server-side {@link xyz.immortius.museumcurator.server.ChecklistState} is used instead)
 * TODO: Split the client-side only part into a different class
 */
public final class MuseumCollections {

    private static final Set<String> IGNORE_TAGS = ImmutableSet.of("Damage");
    private static List<MuseumCollection> collections = Collections.emptyList();
    private static ListMultimap<Item, ItemStack> collectionItems = ImmutableListMultimap.of();
    private static final Set<ItemStack> checkedItems = new LinkedHashSet<>();

    private MuseumCollections() {}

    /**
     * @return All collections
     */
    public static List<MuseumCollection> getCollections() {
        return collections;
    }

    /**
     * Sets the available collections
     * @param newCollections
     */
    public static void setCollections(Collection<MuseumCollection> newCollections) {
        collections = ImmutableList.copyOf(newCollections);
        ImmutableListMultimap.Builder<Item, ItemStack> builder = ImmutableListMultimap.builder();
        for (MuseumCollection collection : newCollections) {
            for (MuseumExhibit exhibit : collection.getExhibits()) {
                for (ItemStack item : exhibit.getItems()) {
                    builder.put(item.getItem(), item);
                }
            }
        }

        collectionItems = builder.build();
    }

    public static boolean isValidCollectionItem(ItemStack queryItem) {
        return getCollectionItemStack(queryItem) != null;
    }

    public static ItemStack getCollectionItemStack(ItemStack itemStack) {
        List<ItemStack> itemStacks = collectionItems.get(itemStack.getItem());
        ItemStack bestMatch = null;
        int bestExcessTags = 0;
        for (ItemStack collectionItem : itemStacks) {
            boolean match = true;
            if (collectionItem.getTag() != null) {
                if (itemStack.getTag() == null) {
                    match = false;
                } else {
                    match = new ComparingTagVisitor(collectionItem.getTag()).isMatch(itemStack.getTag());
                }
            }
            if (match) {
                int excessTags = ((itemStack.getTag() != null) ? itemStack.getTag().getAllKeys().size() : 0) - ((collectionItem.getTag() != null) ? collectionItem.getTag().getAllKeys().size() : 0);
                if (bestMatch == null || excessTags < bestExcessTags) {
                    bestMatch = collectionItem;
                    bestExcessTags = excessTags;
                }
            }
        }
        return bestMatch;
    }


    /**
     * Sets all checked items
     * @param items
     */
    public static void setCheckedItems(Collection<ItemStack> items) {
        MuseumCollections.checkedItems.clear();
        checkItems(items);
    }

    /**
     * Check an item
     * @param items
     */
    public static void checkItems(Collection<ItemStack> items) {
        for (ItemStack item : items) {
            ItemStack collectionItem = getCollectionItemStack(item);
            if (collectionItem != null) {
                checkedItems.add(collectionItem);
            }
        }
    }

    /**
     * Uncheck an item
     * @param items
     */
    public static void uncheckItems(Collection<ItemStack> items) {
        for (ItemStack item : items) {
            ItemStack collectionItem = getCollectionItemStack(item);
            if (collectionItem != null) {
                checkedItems.remove(collectionItem);
            }
        }
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
    public static boolean isChecked(ItemStack item) {
        ItemStack collectionItem = getCollectionItemStack(item);
        return collectionItem != null && checkedItems.contains(collectionItem);
    }

    /**
     * @param items
     * @return The count of how many of the provided items are checked
     */
    public static long countChecked(List<ItemStack> items) {
        return items.stream().map(MuseumCollections::getCollectionItemStack).filter(MuseumCollections::isChecked).count();
    }

    public static Set<Item> getAllCollectionItems() {
        return collectionItems.keySet();
    }

    public static class ComparingTagVisitor implements TagVisitor {
        private final Tag target;
        private boolean match = false;

        ComparingTagVisitor(Tag t) {
            target = t;
        }

        public boolean isMatch(Tag tag) {
            match = false;
            tag.accept(this);
            return match;
        }

        @Override
        public void visitString(StringTag tag) {
            if (target instanceof StringTag t) {
                match = t.getAsString().equals(tag.getAsString());
            }
        }

        @Override
        public void visitByte(ByteTag tag) {
            if (target instanceof NumericTag t) {
                match = t.getAsLong() == tag.getAsLong();
            }
        }

        @Override
        public void visitShort(ShortTag tag) {
            if (target instanceof NumericTag t) {
                match = t.getAsLong() == tag.getAsLong();
            }
        }

        @Override
        public void visitInt(IntTag tag) {
            if (target instanceof NumericTag t) {
                match = t.getAsLong() == tag.getAsLong();
            }
        }

        @Override
        public void visitLong(LongTag tag) {
            if (target instanceof NumericTag t) {
                match = t.getAsLong() == tag.getAsLong();
            }
        }

        @Override
        public void visitFloat(FloatTag tag) {
            if (target instanceof NumericTag t) {
                match = (t.getAsDouble() - tag.getAsDouble()) < Mth.EPSILON;
            }
        }

        @Override
        public void visitDouble(DoubleTag tag) {
            if (target instanceof NumericTag t) {
                match = Math.abs(t.getAsDouble() - tag.getAsDouble()) < Mth.EPSILON;
            }
        }

        @Override
        public void visitByteArray(ByteArrayTag tag) {
            if (target instanceof ByteArrayTag t) {
                t.equals(tag);
            }
        }

        @Override
        public void visitIntArray(IntArrayTag tag) {
            if (target instanceof IntArrayTag t) {
                t.equals(tag);
            }
        }

        @Override
        public void visitLongArray(LongArrayTag tag) {
            if (target instanceof LongArrayTag t) {
                t.equals(tag);
            }
        }

        @Override
        public void visitList(ListTag tag) {
            if (target instanceof ListTag t) {
                for (Tag targetValue : t) {
                    ComparingTagVisitor childVisitor = new ComparingTagVisitor(targetValue);
                    boolean foundMatch = false;
                    for (Tag actualValue : tag) {
                        if (childVisitor.isMatch(actualValue)) {
                            foundMatch = true;
                            break;
                        }
                    }
                    if (!foundMatch) {
                        return;
                    }
                }
                match = true;
            }
        }

        @Override
        public void visitCompound(CompoundTag tag) {
            if (target instanceof CompoundTag t) {
                for (String key : t.getAllKeys()) {
                    if (!IGNORE_TAGS.contains(key) && (tag.get(key) == null || !new ComparingTagVisitor(t.get(key)).isMatch(tag.get(key)))) {
                        return;
                    }
                }
                match = true;
            }
        }

        @Override
        public void visitEnd(EndTag tag) {
            if (target instanceof EndTag t) {
                match = t.equals(tag);
            }
        }
    }
}
