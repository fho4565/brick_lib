package com.arc_studio.brick_lib.tools.quick.command;

import com.arc_studio.brick_lib.BrickLib;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.TreeSet;

public abstract class AbstractNode implements Comparable<AbstractNode> {
    protected final TreeSet<AbstractNode> children = new TreeSet<>();
    AbstractNode parent;
    int count = 0;

    public AbstractNode(AbstractNode parent) {
        this.parent = parent;
    }

    @Override
    public int compareTo(@NotNull AbstractNode o) {
        return 0;
    }

    public LiteralArgumentBuilder<CommandSourceStack> build() {
        System.out.println("AbstractNode.build");
        AbstractNode parent;
        AbstractNode current = this;
        while ((parent = current.parent) != null) {
            current = parent;
        }
        LiteralArgumentBuilder<CommandSourceStack> root;
        if (current instanceof RootNode rootNode) {
            root = Commands.literal(rootNode.name());
            trav(root, current.children);
            return root;
        }
        LogUtils.getLogger().error("{} is not root node type!", current.getClass());
        return null;
    }

    private void trav(LiteralArgumentBuilder<CommandSourceStack> builder, TreeSet<AbstractNode> nodes) {
        for (AbstractNode node : nodes) {
            if (!node.children.isEmpty()) {
                if (node instanceof LiteralNode inputNode) {
                    trav(builder.then(Commands.literal(inputNode.name())),node.children);
                } else if (node instanceof ArgNode<?> argNode) {
                    String name = argNode.name();
                    trav(builder.then(Commands.argument(name.isBlank() ? "arg"+count++:name, argNode.argumentType)),node.children);
                } else {
                    if (node instanceof ExecuteNode executeNode) {
                        builder.executes(executeNode.execute);
                    }
                }
            }
        }
    }
}
