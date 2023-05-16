package xyz.immortius.museumcurator.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.network.ChecklistUpdateMessage;
import xyz.immortius.museumcurator.interop.Services;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Persistence for the checklist state, and provides methods for updating it
 */
public class ChecklistState extends SavedData {
    private final MinecraftServer server;
    private final Set<ItemStack> checkedItems = new LinkedHashSet<>();

    public static ChecklistState get(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getChunkSource().getDataStorage().computeIfAbsent((tag) -> ChecklistState.load(server, tag), () -> new ChecklistState(server), "museumchecklist");
    }

    private static ChecklistState load(MinecraftServer server, CompoundTag tag) {
        ListTag items = tag.getList("items", ListTag.TAG_STRING);
        Set<ItemStack> checkedItems = new LinkedHashSet<>();
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = MuseumCollections.getCollectionItemStack(ItemStack.of(items.getCompound(i)));
            if (item != null) {
                checkedItems.add(item);
            }
        }
        return new ChecklistState(server, checkedItems);
    }

    public ChecklistState(MinecraftServer server) {
        this.server = server;
    }

    private ChecklistState(MinecraftServer server, Collection<ItemStack> items) {
        this(server);
        this.checkedItems.addAll(items);
    }

    @Override
    public synchronized CompoundTag save(CompoundTag parent) {
        ListTag listTag = new ListTag();
        for (ItemStack item : checkedItems) {
            CompoundTag itemTag = new CompoundTag();
            item.save(itemTag);
            listTag.add(itemTag);
        }
        parent.put("items", listTag);
        return parent;
    }

    public synchronized boolean check(Collection<ItemStack> items) {
        List<ItemStack> toAdd = items.stream().map(MuseumCollections::getCollectionItemStack).filter(Objects::nonNull).filter(x -> !checkedItems.contains(x)).toList();
        if (!toAdd.isEmpty()) {
            checkedItems.addAll(toAdd);
            Services.PLATFORM.broadcastChecklistUpdate(server, ChecklistUpdateMessage.check(toAdd));
            setDirty();
            return true;
        }
        return false;
    }

    public synchronized boolean uncheck(Collection<ItemStack> items) {
        Set<ItemStack> toRemove = items.stream().map(MuseumCollections::getCollectionItemStack).filter(Objects::nonNull).filter(checkedItems::contains).collect(Collectors.toSet());
        if (!toRemove.isEmpty()) {
            checkedItems.removeAll(toRemove);
            Services.PLATFORM.broadcastChecklistUpdate(server, ChecklistUpdateMessage.uncheck(toRemove));

            setDirty();
            return true;
        }
        return false;
    }

    public synchronized void uncheckAll() {
        if (!checkedItems.isEmpty()) {
            checkedItems.clear();
            Services.PLATFORM.broadcastChecklistUpdate(server, ChecklistUpdateMessage.uncheckAll());
            setDirty();
        }
    }

    public synchronized Set<ItemStack> getCheckedItems() {
        return new LinkedHashSet<>(checkedItems);
    }

}
