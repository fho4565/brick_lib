package com.arc_studio.brick_lib.mixin.common.server;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.server.ServerEvent;
import net.minecraft.gametest.framework.GameTestServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameTestServer.class)
public class GameTestServerMixin {
    @Inject(method = "initServer",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setWeatherParameters(IIZZ)V"))
    public void serverAboutToStart(CallbackInfoReturnable<Boolean> cir){
        BrickEventBus.postEvent(new ServerEvent.AboutToStart((GameTestServer)(Object)this));
    }
    @Inject(method = "initServer",at = @At(value = "INVOKE", target = "Lnet/minecraft/gametest/framework/GameTestServer;setPlayerList(Lnet/minecraft/server/players/PlayerList;)V"))
    public void serverStarting(CallbackInfoReturnable<Boolean> cir){
        BrickEventBus.postEvent(new ServerEvent.Starting((GameTestServer) (Object) this));
    }
}
