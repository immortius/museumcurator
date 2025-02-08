package xyz.immortius.museumcurator.common.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;

import java.util.*;

/**
 * Static singleton for holding museum information. The collections are loaded both server and client side, but the
 * collected items are only used client side (server-side {@link xyz.immortius.museumcurator.server.ChecklistState} is used instead)
 * TODO: Split the client-side only part into a different class
 */
public final class MuseumCollections {

    private static final Set<String> IGNORE_TAGS = ImmutableSet.of("Damage");
    private static List<MuseumCollection> collections = Collections.emptyList();
    private static ListMultimap<Item, CollectionItem> collectionItems = ImmutableListMultimap.of();
    private static final Set<CollectionItem> checkedItems = new LinkedHashSet<>();

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
        ImmutableListMultimap.Builder<Item, CollectionItem> builder = ImmutableListMultimap.builder();
        for (MuseumCollection collection : newCollections) {
            for (MuseumExhibit exhibit : collection.getExhibits()) {
                for (CollectionItem item : exhibit.getItems()) {
                    builder.put(item.itemStack().getItem(), item);
                }
            }
        }

        collectionItems = builder.build();
    }

    public static boolean isValidCollectionItem(ItemStack queryItem) {
        return getCollectionItemStack(queryItem) != null;
    }

    public static CollectionItem getCollectionItemStack(ItemStack itemStack) {
        List<CollectionItem> candidates = collectionItems.get(itemStack.getItem());
        CollectionItem bestMatch = null;
        int bestExcessTags = 0;
        for (CollectionItem candidate : candidates) {
            boolean match = true;
            if (!candidate.componentFilter().isEmpty()) {
                for (Map.Entry<DataComponentType<?>, Optional<?>> entry : candidate.componentFilter().entrySet()) {
                    if (entry.getValue().isPresent()) {
                        Object data = entry.getValue().get();
                        match = itemStack.getComponents().get(entry.getKey()) != null && data.equals(itemStack.getComponents().get(entry.getKey()));
                    }
                }
            }
            if (match) {
                int excessTags = itemStack.getComponents().size() - candidate.itemStack().getComponents().size();
                if (bestMatch == null || excessTags < bestExcessTags) {
                    bestMatch = candidate;
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
            CollectionItem collectionItem = getCollectionItemStack(item);
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
            CollectionItem collectionItem = getCollectionItemStack(item);
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
    public static boolean isChecked(CollectionItem item) {
        return item != null && checkedItems.contains(item);
    }

    /**
     * @param items
     * @return The count of how many of the provided items are checked
     */
    public static long countChecked(List<CollectionItem> items) {
        return items.stream().filter(MuseumCollections::isChecked).count();
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
