package xyz.immortius.museumcurator.server;

import com.google.common.base.Strings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import xyz.immortius.museumcurator.common.data.MuseumCollections;
import xyz.immortius.museumcurator.common.network.ChecklistUpdateMessage;
import xyz.immortius.museumcurator.config.MuseumCuratorConfig;
import xyz.immortius.museumcurator.interop.Services;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Persistence for the checklist state, and provides methods for updating it
 */
public class ChecklistState extends SavedData {
    private final MinecraftServer server;
    private final UUID ownerId;
    private final Set<ItemStack> checkedItems = new LinkedHashSet<>();

    public static ChecklistState get(MinecraftServer server, ServerPlayer player) {
        if (MuseumCuratorConfig.get().gameplayConfig.isIndividualChecklists(MuseumCuratorConfig.get())) {
            String checklistId = Services.GROUP_HELPER.getLeaderId(player);
            return server.getLevel(Level.OVERWORLD).getChunkSource().getDataStorage().computeIfAbsent(new Factory<>(() -> new ChecklistState(server, checklistId), (tag) -> ChecklistState.load(server, tag, checklistId), DataFixTypes.LEVEL), "museumchecklist-" + checklistId);
        } else {
            return server.getLevel(Level.OVERWORLD).getChunkSource().getDataStorage().computeIfAbsent(new Factory<>(() -> new ChecklistState(server, ""), (tag) -> ChecklistState.load(server, tag, ""), DataFixTypes.LEVEL), "museumchecklist");
        }
    }

    private static ChecklistState load(MinecraftServer server, CompoundTag tag, String ownerId) {
        ListTag items = tag.getList("items", ListTag.TAG_COMPOUND);
        Set<ItemStack> checkedItems = new LinkedHashSet<>();
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = MuseumCollections.getCollectionItemStack(ItemStack.of(items.getCompound(i)));
            if (item != null) {
                checkedItems.add(item);
            }
        }
        return new ChecklistState(server, ownerId, checkedItems);
    }

    public ChecklistState(MinecraftServer server, String ownerId) {
        this.server = server;
        this.ownerId = Strings.isNullOrEmpty(ownerId) ? null : UUID.fromString(ownerId);
    }

    private ChecklistState(MinecraftServer server, String ownerId, Collection<ItemStack> items) {
        this(server, ownerId);
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
        if (ownerId != null) {
            parent.putString("owner", ownerId.toString());
        }
        return parent;
    }

    public synchronized boolean check(Collection<ItemStack> items) {
        List<ItemStack> toAdd = items.stream().map(MuseumCollections::getCollectionItemStack).filter(Objects::nonNull).filter(x -> !checkedItems.contains(x)).toList();
        if (!toAdd.isEmpty()) {
            checkedItems.addAll(toAdd);
            ChecklistUpdateMessage msg = ChecklistUpdateMessage.check(toAdd);

            if (MuseumCuratorConfig.get().gameplayConfig.isIndividualChecklists(MuseumCuratorConfig.get())) {
                for (ServerPlayer player : Services.GROUP_HELPER.getGroupPlayers(server, ownerId)) {
                    Services.PLATFORM.sendChecklistUpdate(server, player, msg);
                }
            } else {
                Services.PLATFORM.broadcastChecklistUpdate(server, msg);
            }

            setDirty();
            return true;
        }
        return false;
    }

    public synchronized boolean uncheck(Collection<ItemStack> items) {
        Set<ItemStack> toRemove = items.stream().map(MuseumCollections::getCollectionItemStack).filter(Objects::nonNull).filter(checkedItems::contains).collect(Collectors.toSet());
        if (!toRemove.isEmpty()) {
            checkedItems.removeAll(toRemove);

            ChecklistUpdateMessage msg = ChecklistUpdateMessage.uncheck(toRemove);

            if (MuseumCuratorConfig.get().gameplayConfig.isIndividualChecklists(MuseumCuratorConfig.get())) {
                for (ServerPlayer player : Services.GROUP_HELPER.getGroupPlayers(server, ownerId)) {
                    Services.PLATFORM.sendChecklistUpdate(server, player, msg);
                }
            } else {
                Services.PLATFORM.broadcastChecklistUpdate(server, msg);
            }

            setDirty();
            return true;
        }
        return false;
    }

    public synchronized void uncheckAll() {
        if (!checkedItems.isEmpty()) {
            checkedItems.clear();

            ChecklistUpdateMessage msg = ChecklistUpdateMessage.uncheckAll();

            if (MuseumCuratorConfig.get().gameplayConfig.isIndividualChecklists(MuseumCuratorConfig.get())) {
                for (ServerPlayer player : Services.GROUP_HELPER.getGroupPlayers(server, ownerId)) {
                    Services.PLATFORM.sendChecklistUpdate(server, player, msg);
                }
            } else {
                Services.PLATFORM.broadcastChecklistUpdate(server, msg);
            }
            setDirty();
        }
    }

    public synchronized Set<ItemStack> getCheckedItems() {
        return new LinkedHashSet<>(checkedItems);
    }

}
