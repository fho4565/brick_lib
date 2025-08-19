package com.arc_studio.brick_lib.mixin.common.client;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.client.HudEvent;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiExpBarMixin {
    @Unique
    ThreadLocal<HudEvent.VanillaRender.ExperienceBar> brick_lib$expEvent = new ThreadLocal<>();

    @Shadow public abstract Font getFont();

    @Shadow @Final
    private Minecraft minecraft;

    //? if < 1.20.6 {
    @Shadow protected int screenWidth;

    @Shadow protected int screenHeight;


    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    public void shouldRenderExpBar(GuiGraphics g, int barX, CallbackInfo ci) {
        if (minecraft.player != null) {
            int progressWidth = (int)(minecraft.player.experienceProgress * 183.0F);
            int barY = screenHeight - 32 + 3;
            String levelStr = String.valueOf(minecraft.player.experienceLevel);
            int textX = (this.screenWidth - getFont().width(levelStr)) / 2;
            int textY = screenHeight - 31 - 4;
            HudEvent.VanillaRender.ExperienceBar event = new HudEvent.VanillaRender.ExperienceBar(g,barX,barY,progressWidth,levelStr,textX,textY);
            if (BrickEventBus.postEvent(event)) {
                ci.cancel();
            } else {
                if (event.isVanillaRenderCanceled()) {
                    ci.cancel();
                }else{
                    brick_lib$expEvent.set(event);
                }
            }
        }
    }

    @WrapWithCondition(method = "renderExperienceBar", at = @At(value = "INVOKE", target = /*? <1.20.4 {*/ "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V" /*?} else {*/ /*"Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"*//*?}*/,ordinal = 0))
    public boolean shouldRenderBackgroundBar(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, //? if < 1.20.4 {
            int uOffset, int vOffset,
             //?}
                                             int uWidth, int vHeight) {
        return brick_lib$expEvent.get().renderBackground();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE", target = /*? <1.20.4 {*/ "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V" /*?} else {*/ /*"Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"*//*?}*/, ordinal = 0),index = 1)
    public int modifyRenderBackgroundBarArg_BgBarX(int x) {
        return brick_lib$expEvent.get().barX();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE", target = /*? <1.20.4 {*/ "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V" /*?} else {*/ /*"Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"*//*?}*/, ordinal = 0),index = 2)
    public int modifyRenderBackgroundBarArg_BgBarY(int y) {
        return brick_lib$expEvent.get().barY();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE", target = /*? <1.20.4 {*/ "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 0),index = 5) /*?} else {*/ /*"Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0),index = 3)*//*?}*/
    public int modifyRenderBackgroundBarArg_BgBarW(int w) {
        return brick_lib$expEvent.get().backgroundBarWidth();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE", target = /*? <1.20.4 {*/ "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 0),index = 6) /*?} else {*/ /*"Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0),index = 4)*//*?}*/
    public int modifyRenderBackgroundBarArg_BgBarH(int h) {
        return brick_lib$expEvent.get().barHeight();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            //? if >= 1.20.4 {
            /*target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"),index = 5)
    *///?} else {
    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V" , ordinal = 1),index = 1)
     //?}
    public int modifyRenderBackgroundBarArg_ProgressBarX(int x) {
        return brick_lib$expEvent.get().barX();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            //? if >= 1.20.4 {
            /*target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"),index = 6)
    *///?} else {
    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V" , ordinal = 1 ),index = 2)
     //?}
    public int modifyRenderBackgroundBarArg_ProgressBarY(int y) {
        return brick_lib$expEvent.get().barY();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            //? if >= 1.20.4 {
            /*target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"),index = 7)
    *///?} else {
    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V" , ordinal = 1 ),index = 5)
     //?}
    public int modifyRenderBackgroundBarArg_ProgressBarW(int w) {
        return brick_lib$expEvent.get().expProgressBarWidth();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            //? if >= 1.20.4 {
            /*target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"),index = 8)
    *///?} else {
    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V" , ordinal = 1),index = 6)
     //?}
    public int modifyRenderBackgroundBarArg_ProgressBarH(int h) {
        return brick_lib$expEvent.get().barHeight();
    }

    @WrapWithCondition(method = "renderExperienceBar", at = @At(value = "INVOKE", target = /*? <1.20.4 {*/ "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V" , ordinal = 1 /*?} else {*/ /*"Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"*//*?}*/))
    public boolean shouldRenderProgressBar(GuiGraphics instance, ResourceLocation atlasLocation,//? if >= 1.20.4 {
                                           /*int textureWidth,int textureHeight,
                                           *///?}
                                           int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        return brick_lib$expEvent.get().renderProgressBar();
    }

    @ModifyVariable(method = "renderExperienceBar",at = @At("STORE"),ordinal = 0)
    public String modifyText(String s){
        return brick_lib$expEvent.get().levelText();
    }

    @ModifyVariable(method = "renderExperienceBar",at = @At("STORE"),name = /*? forge {*/ "i1" /*?} else {*//*"k"*//*?}*/,print = true)
    public int modifyTextX(int i1){
        return brick_lib$expEvent.get().levelTextX();
    }

    @ModifyVariable(method = "renderExperienceBar",at = @At("STORE"),name = /*? forge {*/ "j1" /*?} else {*//*"l"*//*?}*/,print = true)
    public int modifyTextY(int j1){
        return brick_lib$expEvent.get().levelTextY();
    }


    @Inject(method = "renderExperienceBar", at = @At("TAIL"))
    public void renderExpBarTail(GuiGraphics arg, int m, CallbackInfo ci) {
        brick_lib$expEvent.remove();
    }

    //?} else {
    /*@Unique
    ThreadLocal<Boolean> eventCanceled = ThreadLocal.withInitial(() -> false);
    @Unique
    ThreadLocal<Boolean> vanillaCanceled = ThreadLocal.withInitial(() -> false);

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    public void shouldRenderExpBar(GuiGraphics g, int barX, CallbackInfo ci) {
        if (minecraft.player != null) {
            int progressWidth = (int)(minecraft.player.experienceProgress * 183.0F);
            int barY = g.guiHeight() - 32 + 3;
            String levelStr = String.valueOf(minecraft.player.experienceLevel);
            int textX = (g.guiWidth() - getFont().width(levelStr)) / 2;
            int textY = g.guiHeight() - 31 - 4;
            HudEvent.VanillaRender.ExperienceBar event = new HudEvent.VanillaRender.ExperienceBar(g,barX,barY,progressWidth,levelStr,textX,textY);
            if (BrickEventBus.postEvent(event)) {
                ci.cancel();
            } else {
                if (event.isVanillaRenderCanceled()) {
                    ci.cancel();
                }else{
                    brick_lib$expEvent = new ThreadLocal<>();
                    brick_lib$expEvent.set(event);
                }
            }
        }
    }

    @WrapWithCondition(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    public boolean shouldRenderBackgroundBar(GuiGraphics instance, ResourceLocation resourceLocation, int x, int y, int w, int h) {
        return brick_lib$expEvent.get().renderBackground();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"),
            index = 1
    )
    public int modifyRenderBackgroundBarArg_BgBarX(int x) {
        return brick_lib$expEvent.get().barX();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"),
            index = 2
    )
    public int modifyRenderBackgroundBarArg_BgBarY(int y) {
        return brick_lib$expEvent.get().barY();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"),
            index = 3
    )
    public int modifyRenderBackgroundBarArg_BgBarW(int w) {
        return brick_lib$expEvent.get().backgroundBarWidth();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"),
            index = 4
    )
    public int modifyRenderBackgroundBarArg_BgBarH(int h) {
        return brick_lib$expEvent.get().barHeight();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"),
            index = 5
    )
    public int modifyRenderBackgroundBarArg_ProgressBarX(int x) {
        return brick_lib$expEvent.get().barX();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"),
            index = 6
    )
    public int modifyRenderBackgroundBarArg_ProgressBarY(int y) {
        return brick_lib$expEvent.get().barY();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"),
            index = 7
    )
    public int modifyRenderBackgroundBarArg_ProgressBarW(int w) {
        return brick_lib$expEvent.get().expProgressBarWidth();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"),
            index = 8
    )
    public int modifyRenderBackgroundBarArg_ProgressBarH(int h) {
        return brick_lib$expEvent.get().barHeight();
    }

    @WrapWithCondition(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    public boolean shouldRenderProgressBar(GuiGraphics instance, ResourceLocation atlasLocation, int textureWidth,int textureHeight, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        return brick_lib$expEvent.get().renderProgressBar();
    }

    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    public void shouldRenderExpText(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if(brick_lib$expEvent == null){
            ci.cancel();
        }else if(brick_lib$expEvent.get() == null){
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderExperienceLevel",at = @At("STORE"),name = //? if fabric {
    "string"
    //?} else {
            /^"s"
            ^///?}
    )
    public String modifyText(String s){
        return brick_lib$expEvent.get().levelText();
    }

    @ModifyVariable(method = "renderExperienceBar",at = @At("STORE"),name = "j",print = true)
    public int modifyTextX(int i1){
        return brick_lib$expEvent.get().levelTextX();
    }

    @ModifyVariable(method = "renderExperienceBar",at = @At("STORE"),name = "k",print = true)
    public int modifyTextY(int j1){
        return brick_lib$expEvent.get().levelTextY();
    }

    @Inject(method = "renderExperienceLevel", at = @At("TAIL"))
    public void shouldRenderExpTextEnd(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        brick_lib$expEvent.remove();
    }
    *///?}
}