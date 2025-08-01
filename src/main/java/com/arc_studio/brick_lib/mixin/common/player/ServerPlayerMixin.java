package com.arc_studio.brick_lib.mixin.common.player;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
//? if forge {
/*import net.minecraftforge.common.util.ITeleporter;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
