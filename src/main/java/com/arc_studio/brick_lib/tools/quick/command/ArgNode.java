package com.arc_studio.brick_lib.tools.quick.command;

import com.mojang.brigadier.arguments.ArgumentType;

public class ArgNode<T> extends InputNode {
    ArgumentType<T> argumentType;

    public ArgNode(AbstractNode parent,String name, ArgumentType<T> argumentType) {
        super(parent,name);
        this.argumentType = argumentType;
    }
}
