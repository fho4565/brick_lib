package com.arc_studio.brick_lib.mixin.common.client;

import com.arc_studio.brick_lib.client.command.ClientCommandInternals;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import com.arc_studio.brick_lib.register.BrickRegistries;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Unique
    ThreadLocal<PlayerEvent.Chat.Send.Pre> clientPreSendMsg = new ThreadLocal<>();
    @Unique
    ThreadLocal<PlayerEvent.Chat.Send.Pre> clientPreSendCmd = new ThreadLocal<>();
    @Unique
    ThreadLocal<PlayerEvent.Chat.AddToRecent> clientAddToRecent = new ThreadLocal<>();

    @Shadow
    private FeatureFlagSet enabledFeatures;

    @Shadow
    private CommandDispatcher<SharedSuggestionProvider> commands;

    @Shadow @Final private ClientSuggestionProvider suggestionsProvider;

    //? if <= 1.20.1 {
    @Shadow private LayeredRegistryAccess<ClientRegistryLayer> registryAccess;
    //?} else {
    /*@Shadow @Final private RegistryAccess.Frozen registryAccess;
    *///?}

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onGameJoin(ClientboundLoginPacket packet, CallbackInfo info) {
        CommandDispatcher<ClientSuggestionProvider> dispatcher = new CommandDispatcher<>();
        ClientCommandInternals.setActiveDispatcher(dispatcher);
        BrickRegistries.CLIENT_COMMAND.registerForeachValue(function -> {
                    //? if >= 1.20.6 {
                    /*dispatcher.register(function.apply(CommandBuildContext.simple(this.registryAccess, this.enabledFeatures)));
                    *///?} else {
                    dispatcher.register(function.apply(CommandBuildContext
                            .configurable(
                                    //? if <= 1.20.1 {
                                    this.registryAccess.compositeAccess()
                                    //?} else {
                                    /*this.registryAccess
                                    *///?}
                                    , this.enabledFeatures)));
                    //?}
        });
        ClientCommandInternals.finalizeInit();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "handleCommands", at = @At("RETURN"))
    private void onOnCommandTree(ClientboundCommandsPacket packet, CallbackInfo info) {
        ClientCommandInternals.addCommands((CommandDispatcher) commands, suggestionsProvider);
    }

    @Inject(method = "sendUnsignedCommand", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        if (ClientCommandInternals.executeCommand(command)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfo info) {
        /*PlayerEvent.Chat.Send.Pre event = new PlayerEvent.Chat.Send.Pre(Minecraft.getInstance().player, command, command, true);
        clientPreSendCmd.set(event);
        if(!BrickEventBus.postEventClient(event)) {
            if (ClientCommandInternals.executeCommand(command)) {
                info.cancel();
            }
        }else{
            info.cancel();
        }*/
        if (ClientCommandInternals.executeCommand(command)) {
            info.cancel();
		}
	}
}