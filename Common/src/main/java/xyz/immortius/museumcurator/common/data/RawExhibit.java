package xyz.immortius.museumcurator.common.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.IntStream;

public class RawExhibit {

    private final String collection;
    private final String name;
    private String relativeTo;
    private Placement placement;
    private final List<ItemStack> items;
    private List<ItemGroup> insertGroups;

    public static final Codec<ItemStack> TAGGED_ITEM_STACK = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(x -> BuiltInRegistries.ITEM.getKey(x.getItem()).toString()),
            CompoundTag.CODEC.fieldOf("tags").forGetter(ItemStack::getTag)
    ).apply(instance, (id, tags) -> {
        ItemStack itemStack = BuiltInRegistries.ITEM.get(new ResourceLocation(id)).getDefaultInstance().copy();
        itemStack.setTag(tags);
        return itemStack;
    }));

    public static final Codec<ItemGroup> ITEM_GROUP_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.either(TAGGED_ITEM_STACK, BuiltInRegistries.ITEM.byNameCodec().xmap(Item::getDefaultInstance, ItemStack::getItem)).optionalFieldOf("relativeTo", Either.left(null)).forGetter(x -> Either.left(x.relativeTo)),
            Codec.STRING.optionalFieldOf("placement", Placement.After.getId()).forGetter(x -> (x.placement() != null) ? x.placement().getId() : ""),
            Codec.either(TAGGED_ITEM_STACK, BuiltInRegistries.ITEM.byNameCodec().xmap(Item::getDefaultInstance, ItemStack::getItem)).listOf().fieldOf("items").forGetter((x) ->
                    x.items.stream().<Either<ItemStack, ItemStack>>map(Either::left).toList()
            )
    ).apply(instance, (relativeTo, positioning, items) -> new ItemGroup(
            (relativeTo.left().isPresent()) ? relativeTo.left().get() : relativeTo.right().orElse(null),
            Placement.parse(positioning), items.stream().map(x -> {
        if (x.left().isPresent()) {
            return x.left().get();
        }
        return x.right().orElseThrow();
    }).toList())));

    public static final Codec<RawExhibit> EXHIBIT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("collection").forGetter(x -> x.collection),
            Codec.STRING.fieldOf("name").forGetter(x -> x.name),
            Codec.STRING.optionalFieldOf("relativeTo", "").forGetter(x -> x.relativeTo),
            Codec.STRING.optionalFieldOf("placement", Placement.After.getId()).forGetter(x -> (x.placement != null) ? x.placement.getId() : ""),
            Codec.either(TAGGED_ITEM_STACK, BuiltInRegistries.ITEM.byNameCodec().xmap(Item::getDefaultInstance, ItemStack::getItem)).listOf().optionalFieldOf("items", Collections.emptyList()).forGetter((x) ->
                    x.items.stream().<Either<ItemStack, ItemStack>>map(Either::left).toList()
            ),
            ITEM_GROUP_CODEC.listOf().optionalFieldOf("insertGroups", Collections.emptyList()).forGetter(x -> x.insertGroups)
    ).apply(instance, (collection, name, relativeTo, positioning, items, insertGroups) -> new RawExhibit(collection, name, relativeTo, Placement.parse(positioning), items.stream().map(x -> {
        if (x.left().isPresent()) {
            return x.left().get();
        }
        return x.right().orElseThrow();
    }).toList(), insertGroups)));

    public RawExhibit(String collection, String name, String relativeTo, Placement placement, List<ItemStack> items, List<ItemGroup> itemGroups) {
        this.collection = collection;
        this.name = name;
        this.relativeTo = relativeTo;
        this.placement = placement;
        this.items = new ArrayList<>(items);
        this.insertGroups = new ArrayList<>(itemGroups);
    }

    public String getCollection() {
        return collection;
    }

    public String getName() {
        return name;
    }

    public String getRelativeTo() {
        return relativeTo;
    }

    public Placement getPlacement() {
        return placement;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public List<ItemGroup> getInsertGroups() {
        return insertGroups;
    }

    public void applyInserts() {
        boolean ignoreConstraints = false;
        while (!insertGroups.isEmpty()) {
            List<ItemGroup> residual = new ArrayList<>();
            for (ItemGroup group : insertGroups) {
                if (ignoreConstraints || group.relativeTo == null) {
                    items.addAll(group.items);
                } else {
                    OptionalInt match = IntStream.range(0, items.size()).filter(i -> matchItems(group.relativeTo, items.get(i))).findFirst();
                    if (match.isPresent()) {
                        items.addAll(match.getAsInt() + group.placement().shift(), group.items);
                    } else {
                        residual.add(group);
                    }
                }
            }
            if (residual.size() == insertGroups.size()) {
                ignoreConstraints = true;
            } else {
                insertGroups = residual;
            }
        }
    }

    private boolean matchItems(ItemStack a, ItemStack b) {
        if (!ItemStack.isSameItem(a, b)) {
            return false;
        }
        if (a.getTag() != null) {
            if (b.getTag() == null) {
                return false;
            } else {
                return new MuseumCollections.ComparingTagVisitor(a.getTag()).isMatch(b.getTag());
            }
        }
        return true;
    }

    public void setRelativeTo(String relativeTo) {
        this.relativeTo = relativeTo;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    public record ItemGroup(ItemStack relativeTo, Placement placement, List<ItemStack> items) {
    }

    public enum Placement {
        Before("before", 0),
        After("after", 1);

        private static final Map<String, Placement> lookup;
        private final String id;
        private final int shift;

        static {
            ImmutableMap.Builder<String, Placement> builder = ImmutableMap.builder();
            for (Placement pos : Placement.values()) {
                builder.put(pos.getId(), pos);
            }
            lookup = builder.build();
        }

        Placement(String id, int indexShift) {
            this.id = id;
            this.shift = indexShift;
        }

        public int shift() {
            return shift;
        }

        public static Placement parse(String value) {
            Placement placement = lookup.get(value.toLowerCase(Locale.ROOT));
            if (placement == null) {
                placement = After;
            }
            return placement;
        }

        public String getId() {
            return id;
        }
    }
}
