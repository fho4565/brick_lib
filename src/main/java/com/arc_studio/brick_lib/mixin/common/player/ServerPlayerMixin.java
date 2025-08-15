package com.arc_studio.brick_lib.mixin.common.player;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
//? if forge {
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Shadow public abstract ServerLevel serverLevel();

    @Shadow public boolean wonGame;

    //? if forge && neoforge {
    /*@Inject(method = "changeDimension", at = @At("HEAD"),remap = false)
    public void change(ServerLevel serverLevel, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir) {
        ServerLevel serverLevel2 = this.serverLevel();
        ResourceKey<Level> resourceKey = serverLevel2.dimension();
        if (resourceKey == Level.END && serverLevel.dimension() == Level.OVERWORLD) {
            if (!this.wonGame) {
                BrickEventBus.postEvent(new PlayerEvent.WonTheGame((ServerPlayer) (Object) this));
            }
        }
    }*/
    //?}
    //? if fabric {
    /*@Inject(method = "changeDimension", at = @At("HEAD"),remap = false)
    public void change(ServerLevel serverLevel, CallbackInfoReturnable<Entity> cir) {
        ServerLevel serverLevel2 = this.serverLevel();
        ResourceKey<Level> resourceKey = serverLevel2.dimension();
        if (resourceKey == Level.END && serverLevel.dimension() == Level.OVERWORLD) {
            if (!this.wonGame) {
                BrickEventBus.postEvent(new PlayerEvent.WonTheGame((ServerPlayer) (Object) this));
            }
        }
    }
    *///?}
}
