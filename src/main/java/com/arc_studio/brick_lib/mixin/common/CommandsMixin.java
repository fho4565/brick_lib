package com.arc_studio.brick_lib.mixin.common;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.game.CommandEvent;
import com.arc_studio.brick_lib.register.BrickRegistries;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
//? if >= 1.20.6 {
/*import net.minecraft.commands.execution.ExecutionContext;
*///?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Commands.class)
public class CommandsMixin {

    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void onInit(Commands.CommandSelection selection, CommandBuildContext context, CallbackInfo ci) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = this.dispatcher;
        BrickEventBus.postEvent(new CommandEvent.Register(commandDispatcher, selection, context));
        BrickRegistries.COMMAND.toRegValues().forEach(commandRegistration -> commandDispatcher.register(commandRegistration.apply(context)));
    }

    //? if >= 1.20.6 {
    /*@Inject(method = "performCommand", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;executeCommandInContext(Lnet/minecraft/commands/CommandSourceStack;Ljava/util/function/Consumer;)V"), cancellable = true)
    public void onExecute(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo ci) {
        if (BrickEventBus.postEvent(new CommandEvent.PreExecute(parseResults))) {
            ci.cancel();
        }
    }
    *///?} else {
    //? if =1.20.4 {
    /*@Inject(method = "performCommand", remap = false, at = @At(value = "INVOKE", target =//? if >= 1.20.4 {
    "Lnet/minecraft/commands/Commands;executeCommandInContext(Lnet/minecraft/commands/CommandSourceStack;Ljava/util/function/Consumer;)V"
    //?} else {
            /^"Lnet/minecraft/commands/Commands;executeCommandInContext(Lnet/minecraft/commands/CommandSourceStack;Ljava/util/functions/Consumer;)V"
    ^///?}
    ), cancellable = true)
    public void onExecute(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo ci) {
        if (BrickEventBus.postEvent(new CommandEvent.PreExecute(parseResults))) {
            ci.cancel();
        }
    }
    *///?} else {
    @Inject(method = "performCommand", remap = false, at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/ParseResults;)I"), cancellable = true)
    public void onExecute(ParseResults<CommandSourceStack> parseResults, String string, CallbackInfoReturnable<Integer> cir) {
        if (BrickEventBus.postEvent(new CommandEvent.PreExecute(parseResults))) {
            cir.cancel();
        }
    }
    //?}
    //?}
}
