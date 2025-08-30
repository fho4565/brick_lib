package com.arc_studio.brick_lib.mixin.common.client;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.config.ConfigTracker;
import com.arc_studio.brick_lib.events.game.LogInEvent;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class)
public class ClientHandshakePacketListenerImplMixin {
    @Shadow @Final private Connection connection;

    //? if >= 1.20.6 {
    /*@Inject(method = "handleGameProfile", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setupOutboundProtocol(Lnet/minecraft/network/ProtocolInfo;)V"))
    public void defaultConfig(ClientboundGameProfilePacket packet, CallbackInfo ci) {
        BrickEventBus.postEventClient(new LogInEvent.ClientSuccess(this.connection));
    }
    *///?} else if = 1.20.4 {
    /*@Inject(method = "handleGameProfile", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setListener(Lnet/minecraft/network/PacketListener;)V"))
    public void defaultConfig(ClientboundGameProfilePacket packet, CallbackInfo ci) {
        BrickEventBus.postEventClient(new LogInEvent.ClientSuccess(this.connection));
    }
    *///?} else {
    @Inject(method = "handleGameProfile",at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setProtocol(Lnet/minecraft/network/ConnectionProtocol;)V"))
    public void defaultConfig1(ClientboundGameProfilePacket packet, CallbackInfo ci) {
        BrickEventBus.postEventClient(new LogInEvent.ClientSuccess(this.connection));
    }
    //?}
}
