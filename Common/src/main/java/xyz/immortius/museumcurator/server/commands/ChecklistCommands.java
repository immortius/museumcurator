package xyz.immortius.museumcurator.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import xyz.immortius.museumcurator.server.ChecklistState;

import java.util.Collections;

/**
 * Commands for updating the checked items
 */
public class ChecklistCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("museumcommand:checkItem")
                .then(Commands.argument("item", ItemArgument.item())
                .executes((context) -> checkItem(context.getSource(), context.getSource().getServer(), ItemArgument.getItem(context, "item")))));
        dispatcher.register(Commands.literal("museumcommand:uncheckItem")
                .then(Commands.argument("item", ItemArgument.item())
                .executes((context) -> uncheckItem(context.getSource(), context.getSource().getServer(), ItemArgument.getItem(context, "item")))));
        dispatcher.register(Commands.literal("museumcommand:uncheckAllItems")
                .requires(x -> x.hasPermission(2))
                .executes((context) -> uncheckAllItems(context.getSource(), context.getSource().getServer())));
    }

    private static int uncheckAllItems(CommandSourceStack source, MinecraftServer server) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            ChecklistState.get(server, player).uncheckAll();
            source.sendSuccess(new TranslatableComponent("commands.museumcurator.uncheckedAll"), true);
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }

    }

    private static int checkItem(CommandSourceStack source, MinecraftServer server, ItemInput item) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            ChecklistState.get(server, player).check(Collections.singletonList(item.getItem().getDefaultInstance()));
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private static int uncheckItem(CommandSourceStack source, MinecraftServer server, ItemInput item) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            ChecklistState.get(server, player).uncheck(Collections.singletonList(item.getItem().getDefaultInstance()));
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

}
