package com.arc_studio.brick_lib.mixin.common;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.LivingEntityEvent;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean isUsingItem();

    @Shadow protected ItemStack useItem;

    @Shadow public abstract void stopUsingItem();

    @Shadow public abstract void setItemInHand(InteractionHand hand, ItemStack stack);

    @Shadow protected abstract void triggerItemUseEffects(ItemStack stack, int amount);

    @Shadow public abstract InteractionHand getUsedItemHand();

    @Shadow public abstract int getUseItemRemainingTicks();

    /*@Inject(method = "releaseUsingItem", at = @At("HEAD"))
    public void releaseUsingItemMixin(CallbackInfo ci) {
        if(getThis() instanceof Player player){
            if(this.getUseItemRemainingTicks() == 0) {
                PlayerEvent.UseItem.Finish finish = new PlayerEvent.UseItem.Finish(player, this.useItem,this.useItem,
                        this.getUsedItemHand());
                BrickEventBus.postEvent(finish);
            }
        }
    }*/

    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    public void stopUsingItemMixin(CallbackInfo ci) {
        if(getThis() instanceof Player player){
            if (isUsingItem() && !useItem.isEmpty()) {
                PlayerEvent.UseItem.Stop stop = new PlayerEvent.UseItem.Stop(player, this.useItem,
                        this.getUsedItemHand(), this.useItem.getUseDuration(

                ) - this.getUseItemRemainingTicks());
                BrickEventBus.postEvent(stop);
            }
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    public void inject59(CallbackInfo ci) {
        if (BrickEventBus.postEvent(new LivingEntityEvent.Jump(getThis()))) {
            ci.cancel();
        }
    }

    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    public void startUsingItemMixin(InteractionHand hand, CallbackInfo ci) {
        if(getThis() instanceof Player player){
            if (BrickEventBus.postEvent(new PlayerEvent.UseItem.Start(player, this.useItem, hand))) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "completeUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;triggerItemUseEffects(Lnet/minecraft/world/item/ItemStack;I)V"),locals = LocalCapture.CAPTURE_FAILHARD)
    public void completeUsingItemMixin(CallbackInfo ci, InteractionHand interactionHand) {
        if(getThis() instanceof Player player){
            this.triggerItemUseEffects(this.useItem, 16);
            PlayerEvent.UseItem.Finish finish = new PlayerEvent.UseItem.Finish(player, this.useItem, this.useItem.finishUsingItem(player.level(), player), interactionHand);
            BrickEventBus.postEvent(finish);
            ItemStack itemStack = finish.getFinish();
            if (itemStack != this.useItem) {
                this.setItemInHand(interactionHand, itemStack);
            }
            this.stopUsingItem();
        }
    }

    @Inject(method = "updateUsingItem", at = @At("HEAD"))
    public void updateUsingItemMixin(ItemStack usingItem, CallbackInfo ci) {
        if(getThis() instanceof Player player){
            PlayerEvent.UseItem.Using using = new PlayerEvent.UseItem.Using(player, this.useItem,
                    this.getUsedItemHand(),usingItem.getUseDuration(

            ) - this.getUseItemRemainingTicks());
            BrickEventBus.postEvent(using);

        }
    }

    @Unique
    private LivingEntity getThis() {
        return (LivingEntity) (Object) this;
    }
}