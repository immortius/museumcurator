package xyz.immortius.museumcurator.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
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
        ChecklistState.get(server).uncheckAll();
        source.sendSuccess(new TranslatableComponent("commands.museumcurator.uncheckedAll"), true);
        return 1;
    }

    private static int checkItem(CommandSourceStack source, MinecraftServer server, ItemInput item) {
        if (ChecklistState.get(server).check(Collections.singletonList(item.getItem()))) {
            source.sendSuccess(new TranslatableComponent("commands.museumcurator.checked", item.getItem().getDefaultInstance().getDisplayName()), false);
        }
        return 1;
    }

    private static int uncheckItem(CommandSourceStack source, MinecraftServer server, ItemInput item) {
        if (ChecklistState.get(server).uncheck(Collections.singletonList(item.getItem()))) {
            source.sendSuccess(new TranslatableComponent("commands.museumcurator.unchecked", item.getItem().getDefaultInstance().getDisplayName()), false);
        }
        return 1;
    }

}
