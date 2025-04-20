package me.nusimucat.roadmap.commands;

import java.util.concurrent.CompletableFuture;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.nusimucat.roadmap.Editor;

public class MainCommand {

    @FunctionalInterface
    public interface SuggestionProvider<S> {
        CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) throws CommandSyntaxException;
    }

    public static LiteralCommandNode<CommandSourceStack> buildcommandmain = Commands.literal("roadmap")
        .then(Commands.literal("inspector")
            .executes(
                ctx -> {
                CommandSender sender = ctx.getSource().getSender(); 
                Entity executor = ctx.getSource().getExecutor();
                if (!(executor instanceof Player)) {
                    sender.sendPlainMessage("Only players are allowed to use this command!");
                    return 0; 
                }

                Player player = (Player) executor;
                if (Editor.getEditor(player) != null) {
                    // Player is already an inspector
                    Editor playerInspector = Editor.getEditor(player);
                }
                player.sendMessage("Hello, " + player.getName() + "!");
                return Command.SINGLE_SUCCESS; 
            }
            ) 
        )
        .then(Commands.literal("editor")
            .executes(ctx -> {
                CommandSender sender = ctx.getSource().getSender(); 
                Entity executor = ctx.getSource().getExecutor();
                if (!(executor instanceof Player)) {
                    sender.sendPlainMessage("Only players are allowed to use this command!");
                    return 0; 
                }

                Player player = (Player) executor;
                player.sendMessage("Hello, " + player.getName() + "!");
                return Command.SINGLE_SUCCESS; 
            }) 
        )
        .then(Commands.literal("navigate")
            .executes(null) 
        )
        .then(Commands.literal("webserver")
            .executes(null) 
        )
        .then(Commands.literal("test")
            .executes(ctx -> {
                CommandSender executor = ctx.getSource().getSender(); 
                executor.sendPlainMessage("Hello");
                return Command.SINGLE_SUCCESS; 
            }))
        .build();  
}
