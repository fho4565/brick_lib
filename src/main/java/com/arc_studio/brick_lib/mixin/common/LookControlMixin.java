package com.arc_studio.brick_lib.mixin.common;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.mob.MobEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author fho4565
 */
@Mixin(LookControl.class)
public abstract class LookControlMixin {
    @Unique
    ThreadLocal<MobEvent.LookAtUpdate> brick_lib$local = new ThreadLocal<>();

    @Shadow @Final protected Mob mob;

    @Shadow
    protected double wantedX;

    @Shadow
    protected double wantedY;

    @Shadow
    protected float yMaxRotSpeed;

    @Shadow
    protected double wantedZ;

    @Shadow
    protected float xMaxRotAngle;

    @Inject(method = "setLookAt(DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void setMixin(double x, double y, double z, float deltaYaw, float deltaPitch, CallbackInfo ci) {
        MobEvent.LookAtUpdate event = new MobEvent.LookAtUpdate(mob, x, y, z, deltaPitch, deltaYaw);
        if (BrickEventBus.postEventCommon(event)) {
            ci.cancel();
        } else {
            brick_lib$local.set(event);
        }
    }

    @Inject(method = "setLookAt(DDDFF)V", at = @At("TAIL"))
    public void inject37(double x, double y, double z, float deltaYaw, float deltaPitch, CallbackInfo ci) {
        MobEvent.LookAtUpdate event = brick_lib$local.get();
        if (event != null) {
            this.wantedX = event.getX();
            this.wantedY = event.getY();
            this.wantedZ = event.getZ();
            this.xMaxRotAngle = event.getxMaxRotAngle();
            this.yMaxRotSpeed = event.getyMaxRotSpeed();
            brick_lib$local.remove();
        }
    }

    @Unique
    private LookControl brick_lib$getThis() {
        return (LookControl) (Object) this;
    }
}