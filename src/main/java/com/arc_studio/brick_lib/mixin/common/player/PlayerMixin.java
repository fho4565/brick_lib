package com.arc_studio.brick_lib.mixin.common.player;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerClickContext;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "interactOn",at = @At(value = "INVOKE",ordinal = 0, target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    public void rightClickEntity(Entity entityToInteractOn, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        Player player = (Player) (Object) this;
        if (BrickEventBus.postEvent(new PlayerEvent.RightClick(player,
                PlayerClickContext.clickEntity(player, hand,new EntityHitResult(entityToInteractOn))
        ))) {
            cir.cancel();
        }
    }
    @Inject(method = "tick",at = @At("HEAD"))
    public void onPlayerTickStart(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        BrickEventBus.postEvent(new PlayerEvent.Tick.Pre(player));
    }
    @Inject(method = "tick",at = @At("TAIL"))
    public void onPlayerTickEnd(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        BrickEventBus.postEvent(new PlayerEvent.Tick.Post(player));
    }
}
