package com.arc_studio.brick_lib.mixin.common.server;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
//? if >=1.20.4 {
/*import net.minecraft.server.network.CommonListenerCookie;
*///?}
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * @author fho4565
 */
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow
    @Nullable
    public abstract ServerPlayer getPlayer(UUID playerUUID);

    @Shadow
    @Final
    private MinecraftServer server;

    //? if <1.20.4 {
    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setServerLevel(Lnet/minecraft/server/level/ServerLevel;)V"))
    public void playerLogIn(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        BrickEventBus.postEvent(new PlayerEvent.PlayerJoin.Pre(serverPlayer));
    }

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void playerJoined(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        BrickEventBus.postEvent(new PlayerEvent.PlayerJoin.Post(serverPlayer));
    }
    //?} else {
    /*@Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setServerLevel(Lnet/minecraft/server/level/ServerLevel;)V"))
    public void playerLogIn(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie cookie, CallbackInfo ci) {
        BrickEventBus.postEvent(new PlayerEvent.PlayerJoin.Pre(serverPlayer));
    }

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void playerJoined(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie cookie, CallbackInfo ci) {
        BrickEventBus.postEvent(new PlayerEvent.PlayerJoin.Post(serverPlayer));
    }
    *///?}

    @Inject(method = "op",at = @At("HEAD"), cancellable = true)
    public void wrapMethod43(GameProfile profile, CallbackInfo ci) {
        if (BrickEventBus.postEvent(new PlayerEvent.PermissionsChange(getPlayer(profile.getId()), server.getProfilePermissions(profile), this.server.getOperatorUserPermissionLevel()))) {
            ci.cancel();
        }
    }

    @Inject(method = "deop",at = @At("HEAD"), cancellable = true)
    public void wrapMethod66(GameProfile profile, CallbackInfo ci) {
        if (BrickEventBus.postEvent(new PlayerEvent.PermissionsChange(getPlayer(profile.getId()),server.getProfilePermissions(profile),0))) {
            ci.cancel();
        }
    }
}
