package com.arc_studio.brick_lib.tools.quick.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.TreeSet;

public class ExecuteNode extends AbstractNode {
    Command<CommandSourceStack> execute;
    private Command<ClientSuggestionProvider> executeClient;

    protected ExecuteNode(AbstractNode parent) {
        super(parent);
    }

    protected static ExecuteNode get(AbstractNode parent, Command<CommandSourceStack> execute) {
        ExecuteNode node = new ExecuteNode(parent);
        node.execute = execute;
        return node;
    }

    protected static ExecuteNode getC(AbstractNode parent, Command<ClientSuggestionProvider> execute) {
        ExecuteNode node = new ExecuteNode(parent);
        node.executeClient = execute;
        return node;
    }
}
