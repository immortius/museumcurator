package xyz.immortius.museumcurator.server;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.network.ChecklistUpdateMessage;
import xyz.immortius.museumcurator.interop.Services;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Persistence for the checklist state, and provides methods for updating it
 */
public class ChecklistState extends SavedData {
    private final MinecraftServer server;
    private final Set<Item> checkedItems = new LinkedHashSet<>();

    public static ChecklistState get(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getChunkSource().getDataStorage().computeIfAbsent((tag) -> ChecklistState.load(server, tag), () -> new ChecklistState(server), "museumcuratorchecklist");
    }

    private static ChecklistState load(MinecraftServer server, CompoundTag tag) {
        ListTag items = tag.getList("items", ListTag.TAG_STRING);
        Set<Item> checkedItems = new LinkedHashSet<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = Registry.ITEM.get(new ResourceLocation(items.getString(i)));
            if (MuseumCollections.getAllCollectionItems().contains(item)) {
                checkedItems.add(item);
            }
        }
        return new ChecklistState(server, checkedItems);
    }

    public ChecklistState(MinecraftServer server) {
        this.server = server;
    }

    private ChecklistState(MinecraftServer server, Collection<Item> items) {
        this(server);
        this.checkedItems.addAll(items);
    }

    @Override
    public synchronized CompoundTag save(CompoundTag parent) {
        ListTag listTag = new ListTag();
        for (Item item : checkedItems) {
            listTag.add(StringTag.valueOf(Registry.ITEM.getKey(item).toString()));
        }
        parent.put("items", listTag);
        return parent;
    }

    public synchronized boolean check(Collection<Item> items) {
        List<Item> toAdd = items.stream().filter(x -> MuseumCollections.getAllCollectionItems().contains(x)).filter(x -> !checkedItems.contains(x)).toList();
        if (!toAdd.isEmpty()) {
            checkedItems.addAll(toAdd);
            Services.PLATFORM.broadcastChecklistUpdate(server, ChecklistUpdateMessage.check(toAdd));
            setDirty();
            return true;
        }
        return false;
    }

    public synchronized boolean uncheck(Collection<Item> items) {
        Set<Item> toRemove = items.stream().filter(checkedItems::contains).collect(Collectors.toSet());
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

    public synchronized Set<Item> getCheckedItems() {
        return new LinkedHashSet<>(checkedItems);
    }

}
