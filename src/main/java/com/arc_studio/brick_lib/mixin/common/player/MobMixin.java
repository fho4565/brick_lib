package com.arc_studio.brick_lib.mixin.common.player;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Mob.class)
public abstract class MobMixin {

    //? if < 1.20.6 {
    @ModifyArg(method = "maybeDisableShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemCooldowns;addCooldown(Lnet/minecraft/world/item/Item;I)V"),index = 1)
    public int modifyMobCdPlayerShieldCd(int ticks) {
        return 40;
    }
    //?}

    @Unique
    private Mob getThis() {
        return (Mob) (Object) this;
    }
}