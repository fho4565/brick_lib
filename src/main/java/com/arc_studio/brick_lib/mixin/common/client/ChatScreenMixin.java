package com.arc_studio.brick_lib.mixin.common.client;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Unique
    ThreadLocal<PlayerEvent.Chat.Send.Pre> clientPreSendMsg = new ThreadLocal<>();
    @Unique
    ThreadLocal<PlayerEvent.Chat.Send.Pre> clientPreSendCmd = new ThreadLocal<>();
    @Unique
    ThreadLocal<PlayerEvent.Chat.AddToRecent> clientAddToRecent = new ThreadLocal<>();

    @WrapOperation(method = "handleChatInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;sendChat(Ljava/lang/String;)V"))
    public void beforeClientSendMessage(ClientPacketListener instance, String message, Operation<Void> original) {
        PlayerEvent.Chat.Send.Pre event = new PlayerEvent.Chat.Send.Pre(Minecraft.getInstance().player, message, message, false);
        if(!BrickEventBus.postEventClient(event)){
            original.call(instance,event.getMessage());
        }
    }


    @WrapOperation(method = "handleChatInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;sendCommand(Ljava/lang/String;)V"))
    public void beforeClientSendCommand(ClientPacketListener instance, String command, Operation<Void> original) {
        PlayerEvent.Chat.Send.Pre event = new PlayerEvent.Chat.Send.Pre(Minecraft.getInstance().player, command, command, true);
        if(!BrickEventBus.postEventClient(event)){
            original.call(instance,event.getMessage());
        }
    }

    //? if >= 1.20.6 {
    /*@Inject(method = "handleChatInput", at = @At("TAIL"))
    public void afterClientSend(String message, boolean addToRecentChat, CallbackInfo ci) {
        BrickEventBus.postEvent(new PlayerEvent.Chat.Send.Post(Minecraft.getInstance().player, message, message.startsWith("/")));
    }
    *///?} else {
    @Inject(method = "handleChatInput", at = @At("TAIL"))
    public void afterClientSend(String string, boolean addToRecentChat, CallbackInfoReturnable<Boolean> cir) {
        BrickEventBus.postEventClient(new PlayerEvent.Chat.Send.Post(Minecraft.getInstance().player, string, string.startsWith("/")));
    }
    //?}

    @WrapOperation(method = "handleChatInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;addRecentChat(Ljava/lang/String;)V"))
    public void onAddRecentMessage(ChatComponent instance, String message, Operation<Void> original) {
        PlayerEvent.Chat.AddToRecent event = new PlayerEvent.Chat.AddToRecent(Minecraft.getInstance().player, message, message, message.startsWith("/"));
        if(!BrickEventBus.postEventClient(event)){
            original.call(instance,event.getMessage());
        }
    }
}
