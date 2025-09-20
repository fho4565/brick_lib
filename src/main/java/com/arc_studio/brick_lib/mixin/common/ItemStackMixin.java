package com.arc_studio.brick_lib.mixin.common;

import com.arc_studio.brick_lib.api.data.ItemAdditionalData;
//? if >=1.20.6 {
/*import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
*///?}

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    public abstract Item getItem();

    //? if >=1.20.6 {
    /*@Inject(method = "isSameItemSameComponents", at = @At("HEAD"), cancellable = true)
    private static void isSame(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if (!stack.is(other.getItem())) {
            cir.setReturnValue(false);
        } else {
            if (stack.isEmpty() && other.isEmpty()) {
                cir.setReturnValue(true);
            }
            PatchedDataComponentMap otherTag = new PatchedDataComponentMap(other.getComponents());
            PatchedDataComponentMap thisTag = new PatchedDataComponentMap(stack.getComponents());
            thisTag.remove(ItemAdditionalData.DATA_COMPONENT_TYPE);
            otherTag.remove(ItemAdditionalData.DATA_COMPONENT_TYPE);
            cir.setReturnValue(Objects.equals(thisTag, otherTag));
        }
    }
    *///?} else {
    @Inject(method = "isSameItemSameTags", at = @At("HEAD"), cancellable = true)
    private static void isSame(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if (!stack.is(other.getItem())) {
            cir.setReturnValue(false);
        } else {
            if (stack.isEmpty() && other.isEmpty()) {
                cir.setReturnValue(true);
            }
            CompoundTag thisTag = stack.getOrCreateTag();
            CompoundTag otherTag = other.getOrCreateTag();
            thisTag.remove(ItemAdditionalData.KEY_DATA);
            otherTag.remove(ItemAdditionalData.KEY_DATA);
            cir.setReturnValue(Objects.equals(thisTag, otherTag));
        }
    }
    //?}

    @Inject(method = "getTooltipLines", at = @At("TAIL"), cancellable = true)
    public void inject66(Player player, TooltipFlag isAdvanced, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        if (player != null){
            System.out.println("player.level().isClientSide = " + player.level().isClientSide);
            PlayerEvent.RequestItemTooltip event = new PlayerEvent.RequestItemTooltip(player, isAdvanced, getThis(), new ArrayList<>(list));
            BrickEventBus.postEvent(event);
            System.out.println("event.getToolTipLines() = " + event.getToolTipLines());
            cir.setReturnValue(event.getToolTipLines());
        }
    }

    private ItemStack getThis(){
        return (ItemStack) (Object) this;
    }
}
