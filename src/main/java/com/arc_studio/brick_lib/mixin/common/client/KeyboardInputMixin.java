package com.arc_studio.brick_lib.mixin.common.client;


import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.EntityEvent;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.KeyboardInput;
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
@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {
    @Shadow
    private static float calculateImpulse(boolean input, boolean otherInput) {
        return 0;
    }

    @Shadow
    @Final
    private Options options;

    @Inject(method = "tick", at = @At("TAIL"))
    public void inject15(boolean isSneaking, float sneakingSpeedMultiplier, CallbackInfo ci) {
        if(options.keyShift.isDown()){
            if (BrickEventBus.postEventClient(new PlayerEvent.Sneak(Minecraft.getInstance().player))) {
                getThis().shiftKeyDown = false;
                getThis().forwardImpulse = calculateImpulse(getThis().up, getThis().down);
                getThis().leftImpulse = calculateImpulse(getThis().left, getThis().right);
            }
        }
    }

    @Unique
    private KeyboardInput getThis() {
        return (KeyboardInput) (Object) this;
    }
}