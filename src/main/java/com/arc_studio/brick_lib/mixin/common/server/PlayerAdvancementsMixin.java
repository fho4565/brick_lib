package com.arc_studio.brick_lib.mixin.common.server;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.Advancement;
//? if >= 1.20.4 {
/*import net.minecraft.advancements.AdvancementHolder;
*///?}
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author fho4565
 */
@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
    @Shadow
    private ServerPlayer player;

    //? if < 1.20.4 {
    @WrapOperation(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;grantProgress(Ljava/lang/String;)Z"))
    public boolean wrapOperation19(AdvancementProgress instance, String criterionProgress, Operation<Boolean> original, @Local(argsOnly = true) Advancement advancement) {
        PlayerEvent.Advancement.Progress event = new PlayerEvent.Advancement.Progress(this.player, advancement, instance, criterionProgress);
        if (BrickEventBus.postEvent(event)) {
            return false;
        }
        return original.call(instance, event.criterionName());
    }
    //?} else {
    /*@WrapOperation(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;grantProgress(Ljava/lang/String;)Z"))
    public boolean wrapOperation19(AdvancementProgress instance, String criterionProgress, Operation<Boolean> original, @Local(argsOnly = true) AdvancementHolder advancement) {
        PlayerEvent.Advancement.Progress event = new PlayerEvent.Advancement.Progress(this.player, advancement.value(), instance, criterionProgress);
        if (BrickEventBus.postEvent(event)) {
            return false;
        }
        return original.call(instance, event.criterionName());
    }

    *///?}

//? if < 1.20.4 {
@Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V"), cancellable = true)
public void inject35(Advancement advancement, String criterionKey, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) boolean flag) {
    PlayerEvent.Advancement.Complete event = new PlayerEvent.Advancement.Complete(this.player, advancement);
    if (BrickEventBus.postEvent(event)) {
        cir.setReturnValue(flag);
    }
}

    @Inject(method = "revoke", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;revokeProgress(Ljava/lang/String;)Z"), cancellable = true)
    public void inject45(Advancement advancement, String criterionKey, CallbackInfoReturnable<Boolean> cir, @Local AdvancementProgress advancementprogress) {
        PlayerEvent.Advancement.Revoke event = new PlayerEvent.Advancement.Revoke(this.player, advancement, advancementprogress, criterionKey);
        if (BrickEventBus.postEvent(event)) {
            cir.setReturnValue(false);
        }
    }
//?} else {
    /*@Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V"), cancellable = true)
    public void inject35(AdvancementHolder advancement, String criterionKey, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) boolean flag) {
        PlayerEvent.Advancement.Complete event = new PlayerEvent.Advancement.Complete(this.player, advancement.value());
        if (BrickEventBus.postEvent(event)) {
            cir.setReturnValue(flag);
        }
    }

    @Inject(method = "revoke", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;revokeProgress(Ljava/lang/String;)Z"), cancellable = true)
    public void inject45(AdvancementHolder advancement, String criterionKey, CallbackInfoReturnable<Boolean> cir, @Local AdvancementProgress advancementprogress) {
        PlayerEvent.Advancement.Revoke event = new PlayerEvent.Advancement.Revoke(this.player, advancement.value(), advancementprogress, criterionKey);
        if (BrickEventBus.postEvent(event)) {
            cir.setReturnValue(false);
        }
    }
    *///?}

    @Unique
    private PlayerAdvancements getThis() {
        return (PlayerAdvancements) (Object) this;
    }
}