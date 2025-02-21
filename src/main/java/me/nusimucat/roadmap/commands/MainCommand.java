package me.nusimucat.roadmap.commands;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class MainCommand {
    public static LiteralCommandNode<CommandSourceStack> buildcommandmain = Commands.literal("roadmap")
        // .then(Commands.literal("inspector")
        //     .executes(null) 
        // )
            .then(Commands.literal("test")
                .executes(ctx -> {
                    CommandSender executor = ctx.getSource().getSender(); 
                    executor.sendPlainMessage("Hello");
                    return Command.SINGLE_SUCCESS; 
                }))
        .build();  
}
