package com.arc_studio.brick_lib.mixin.common.server;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.server.ServerEvent;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(method = "initServer",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;setPlayerList(Lnet/minecraft/server/players/PlayerList;)V", shift = At.Shift.AFTER))
    public void serverAboutToStart(CallbackInfoReturnable<Boolean> cir){
        BrickEventBus.postEvent(new ServerEvent.AboutToStart((DedicatedServer)(Object)this));
    }
    @Inject(method = "initServer",at = @At(value = "RETURN",ordinal = 2))
    public void serverStarting(CallbackInfoReturnable<Boolean> cir){
        BrickEventBus.postEvent(new ServerEvent.Starting((DedicatedServer) (Object) this));
    }
}
