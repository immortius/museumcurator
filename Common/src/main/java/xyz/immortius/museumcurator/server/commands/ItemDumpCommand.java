package xyz.immortius.museumcurator.server.commands;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import xyz.immortius.museumcurator.common.data.MuseumCollections;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Commands for dumping out lists of items
 */
public class ItemDumpCommand {

    private static final SimpleCommandExceptionType FAILED_TO_DUMP = new SimpleCommandExceptionType(new TranslatableComponent("commands.museumcurator.dumperror"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("museumcommand:itemDump")
                .requires(x -> x.hasPermission(2))
                .executes(ItemDumpCommand::itemDump));
        dispatcher.register(Commands.literal("museumcommand:nonCollectionItemDump")
                .requires(x -> x.hasPermission(2))
                .executes(ItemDumpCommand::nonCollectionItemDump));
    }

    private static int nonCollectionItemDump(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {
        Registry<Item> registry = cmd.getSource().getServer().registryAccess().registry(Registry.ITEM_REGISTRY).get();

        Set<ResourceLocation> collectionItems = MuseumCollections.getAllCollectionItems().stream().map(registry::getKey).collect(Collectors.toSet());
        Set<ResourceLocation> missingItems = Sets.difference(registry.keySet(), collectionItems);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("missingitems.json"))) {
            Gson gson = new Gson();
            gson.toJson(missingItems.stream().map(ResourceLocation::toString).toList(), writer);
        } catch (IOException e) {
            throw FAILED_TO_DUMP.create();
        }

        return 0;
    }

    private static int itemDump(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {

        Registry<Item> registry = cmd.getSource().getServer().registryAccess().registry(Registry.ITEM_REGISTRY).get();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("items.json"))) {
            Gson gson = new Gson();
            gson.toJson(registry.keySet().stream().map(ResourceLocation::toString).toList(), writer);
        } catch (IOException e) {
            throw FAILED_TO_DUMP.create();
        }

        return 0;
    }

}
