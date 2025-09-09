package com.arc_studio.brick_lib.mixin.common.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiEffectsMixin {
    @Inject(method = "renderEffects", at = @At("HEAD"))
    public void shouldRenderGUI(GuiGraphics guiGraphics, /*? >=1.20.6 {*/ /*float partialTick, *//*?}*/ CallbackInfo ci) {

    }

}