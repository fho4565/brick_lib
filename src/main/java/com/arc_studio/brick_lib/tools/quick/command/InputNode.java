package com.arc_studio.brick_lib.tools.quick.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public class InputNode extends AbstractNode {
    private final String name;

    public InputNode(AbstractNode parent,String name) {
        super(parent);
        this.name = name;
    }

    public String name() {
        return name;
    }

    public InputNode literal(String name){
        LiteralNode literalNode = new LiteralNode(this,name);
        this.children.add(literalNode);
        return this;
    }

    public<T> InputNode customArg(ArgumentType<T> type){
        ArgNode<T> argNode = new ArgNode<>(this,"",type);
        this.children.add(argNode);
        return this;
    }

    public<T> InputNode customArg(String name, ArgumentType<T> type){
        ArgNode<T> argNode = new ArgNode<>(this,name,type);
        this.children.add(argNode);
        return argNode;
    }
    public InputNode argBool(){
        return customArg(BoolArgumentType.bool());
    }

    public InputNode argBool(String name){
        return customArg(name, BoolArgumentType.bool());
    }

    public InputNode argInt(){
        return customArg(IntegerArgumentType.integer());
    }

    public InputNode argInt(int min){
        return customArg(IntegerArgumentType.integer(min));
    }

    public InputNode argInt(int min,int max){
        return customArg(IntegerArgumentType.integer(min,max));
    }

    public InputNode argInt(String name){
        return customArg(name, IntegerArgumentType.integer());
    }

    public InputNode argInt(String name, int min){
        return customArg(name,IntegerArgumentType.integer(min));
    }

    public InputNode argInt(String name, int min,int max){
        return customArg(name,IntegerArgumentType.integer(min,max));
    }

    public ExecuteNode exec(Command<CommandSourceStack> execute){
        return ExecuteNode.get(this,execute);
    }

    public ExecuteNode execC(Command<ClientSuggestionProvider> execute){
        return ExecuteNode.getC(this,execute);
    }

    @Override
    public int compareTo(@NotNull AbstractNode o) {
        if(o instanceof InputNode inputNode) {
            return this.name.compareTo(inputNode.name);
        }
        return 0;
    }
}
