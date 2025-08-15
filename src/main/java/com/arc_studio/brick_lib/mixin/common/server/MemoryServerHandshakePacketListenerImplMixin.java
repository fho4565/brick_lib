package com.arc_studio.brick_lib.mixin.common.server;

import com.arc_studio.brick_lib.tools.Constants;
import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.server.ServerEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.server.network.MemoryServerHandshakePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MemoryServerHandshakePacketListenerImpl.class)
public class MemoryServerHandshakePacketListenerImplMixin {
    @Shadow @Final private Connection connection;

    @Inject(method = "handleIntention", at = @At("HEAD"))
    public void handle(ClientIntentionPacket packet, CallbackInfo ci) {
        //? if >= 1.20.4 {
        /*BrickEventBus.postEvent(new ServerEvent.LogIn(Constants.currentServer(),
                packet.protocolVersion(),
                packet.hostName(),
                packet.port(),
                this.connection,
                switch (packet.intention()) {
                    case STATUS -> ConnectionProtocol.STATUS;
                    case LOGIN -> ConnectionProtocol.LOGIN;
                    default -> null;
                }
        ));
        *///?} else {
            BrickEventBus.postEvent(new ServerEvent.LogIn(Constants.currentServer(),
                    packet.getProtocolVersion(),
                    packet.getHostName(),
                    packet.getPort(),
                    this.connection,
                    packet.getIntention()
            ));
        //?}
    }
}
