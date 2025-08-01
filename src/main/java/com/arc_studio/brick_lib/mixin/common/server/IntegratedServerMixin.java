package com.arc_studio.brick_lib.mixin.common.server;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.server.ServerEvent;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
    @Inject(method = "initServer",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;loadLevel()V", shift = At.Shift.AFTER))
    public void serverAboutToStart(CallbackInfoReturnable<Boolean> cir){
        BrickEventBus.postEvent(new ServerEvent.AboutToStart((IntegratedServer)(Object)this));
    }
    @Inject(method = "initServer",at = @At("TAIL"))
    public void serverStarting(CallbackInfoReturnable<Boolean> cir){
        BrickEventBus.postEvent(new ServerEvent.Starting((IntegratedServer) (Object) this));
    }
}
