package com.arc_studio.brick_lib.events.game;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import com.arc_studio.brick_lib.api.event.ICancelableEvent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public abstract class CommandEvent extends BaseEvent {
    /**
     * 无作用，仅用于占位
     * */
    @ApiStatus.Experimental
    public static class VanillaRegister extends CommandEvent{

    }

    public static class Register extends CommandEvent {
        CommandDispatcher<CommandSourceStack> dispatcher;
        Commands.CommandSelection selection;
        CommandBuildContext context;

        public Register(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection, CommandBuildContext context) {
            this.dispatcher = dispatcher;
            this.selection = selection;
            this.context = context;
        }

        public Commands.CommandSelection getSelection() {
            return selection;
        }

        public CommandBuildContext getContext() {
            return context;
        }

        public CommandDispatcher<CommandSourceStack> getDispatcher() {
            return dispatcher;
        }
    }

    /**
     * {@link PreExecute}在命令成功完成解析，执行前触发。在{@link Commands#performCommand(ParseResults, String)}的{@link CommandDispatcher#execute(ParseResults)}被调用前触发
     * <p>此事件可以取消，取消这个事件会阻止命令执行</p>
     */
    public static class PreExecute extends CommandEvent implements ICancelableEvent {
        ParseResults<CommandSourceStack> results;
        Throwable exception;

        public PreExecute(ParseResults<CommandSourceStack> results) {
            this.results = results;
        }

        public ParseResults<CommandSourceStack> getResults() {
            return results;
        }

        @Nullable
        public Throwable getException() {
            return exception;
        }

        public void setException(Throwable exception) {
            this.exception = exception;
        }
    }

}
