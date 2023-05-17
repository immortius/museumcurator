package xyz.immortius.museumcurator.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import xyz.immortius.museumcurator.server.ChecklistState;

import java.util.Collections;

/**
 * Commands for updating the checked items
 */
public class ChecklistCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("museumcommand:checkItem")
                .then(Commands.argument("item", ItemArgument.item(context))
                .executes((ctx) -> checkItem(ctx.getSource(), ctx.getSource().getServer(), ItemArgument.getItem(ctx, "item")))));
        dispatcher.register(Commands.literal("museumcommand:uncheckItem")
                .then(Commands.argument("item", ItemArgument.item(context))
                .executes((ctx) -> uncheckItem(ctx.getSource(), ctx.getSource().getServer(), ItemArgument.getItem(ctx, "item")))));
        dispatcher.register(Commands.literal("museumcommand:uncheckAllItems")
                .requires(x -> x.hasPermission(2))
                .executes((ctx) -> uncheckAllItems(ctx.getSource(), ctx.getSource().getServer())));
    }

    private static int uncheckAllItems(CommandSourceStack source, MinecraftServer server) {
        ChecklistState.get(server).uncheckAll();
        source.sendSuccess(() -> Component.translatable("commands.museumcurator.uncheckedAll"), true);
        return 1;
    }

    private static int checkItem(CommandSourceStack source, MinecraftServer server, ItemInput item) {
        ChecklistState.get(server).check(Collections.singletonList(item.getItem().getDefaultInstance()));
        return 1;
    }

    private static int uncheckItem(CommandSourceStack source, MinecraftServer server, ItemInput item) {
        ChecklistState.get(server).uncheck(Collections.singletonList(item.getItem().getDefaultInstance()));
        return 1;
    }

}
