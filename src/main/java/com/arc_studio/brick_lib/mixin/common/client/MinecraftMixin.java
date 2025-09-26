package com.arc_studio.brick_lib.mixin.common.client;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.client.ClientTickEvent;
import com.arc_studio.brick_lib.events.client.KeyEvent;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerClickContext;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import com.arc_studio.brick_lib.events.server.world.LevelEvent;
import com.arc_studio.brick_lib.register.BrickRegistries;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.BufferUploader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author fho4565
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public HitResult hitResult;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    public Screen screen;

    @Shadow
    @Final
    public MouseHandler mouseHandler;

    @Shadow
    public boolean noRender;

    @Shadow
    @Final
    private Window window;

    @Shadow
    @Final
    private SoundManager soundManager;

    @Shadow
    public abstract void updateTitle();

    @Inject(method = "startUseItem", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1), cancellable = true)
    public void rightClickAir(CallbackInfo ci, InteractionHand[] var1, int var2, int var3, InteractionHand interactionHand) {
        ItemStack itemStack = this.player.getItemInHand(interactionHand);
        if (itemStack.isEmpty() && (this.hitResult == null || this.hitResult.getType() == HitResult.Type.MISS)) {
            if (BrickEventBus.postEvent(new PlayerEvent.RightClick(this.player, PlayerClickContext.clickAir(player, interactionHand)))) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "startUseItem", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    public void rightClickBlock(CallbackInfo ci, InteractionHand[] var1, int var2, int var3, InteractionHand interactionHand) {
        if (BrickEventBus.postEvent(new PlayerEvent.RightClick(player, PlayerClickContext.clickBlock(player, interactionHand, (BlockHitResult) this.hitResult)))) {
            ci.cancel();
        }
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
    public void leftClickAir(CallbackInfoReturnable<Boolean> cir) {
        BrickEventBus.postEvent(new PlayerEvent.LeftClick(player, PlayerClickContext.clickBlock(player, InteractionHand.MAIN_HAND, (BlockHitResult) this.hitResult)));
    }

    //? if >= 1.20.6 {
    /*@Inject(method = "setLevel", at = @At("HEAD"))
    public void levelUnload(ClientLevel level, ReceivingLevelScreen.Reason reason, CallbackInfo ci) {
        if (this.level != null) {
            BrickEventBus.postEvent(new LevelEvent.Unload(this.level));
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("HEAD"))
    public void levelUnload1206(CallbackInfo ci) {
        if (this.level != null) {
            BrickEventBus.postEvent(new LevelEvent.Unload(this.level));
        }
    }
    *///?} else {
    @Inject(method = "setLevel", at = @At("HEAD"))
    public void levelUnload(ClientLevel clientLevel, CallbackInfo ci) {
        if (this.level != null) {
            BrickEventBus.postEvent(new LevelEvent.Unload(this.level));
        }
    }

    //? if =1.20.4 {
    /*@Inject(method = "clearClientLevel", at = @At("HEAD"))
    public void levelUnload1204(CallbackInfo ci) {
        if (this.level != null) {
            BrickEventBus.postEvent(new LevelEvent.Unload(this.level));
        }
    }
    *///?} else {
    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"))
    public void levelUnload1201(CallbackInfo ci) {
        if (this.level != null) {
            BrickEventBus.postEvent(new LevelEvent.Unload(this.level));
        }
    }
    //?}
    //?}

    @Inject(at = @At("HEAD"), method = "tick")
    public void clientTickPre(CallbackInfo info) {
        BrickEventBus.postEventClient(new ClientTickEvent.Pre());
        for (KeyMapping keyMapping : BrickRegistries.KEY_MAPPING.values()) {
            if (keyMapping.isDown()) {
                BrickEventBus.postEventClient(new KeyEvent.Down(keyMapping));
            }
            if (keyMapping.consumeClick()) {
                BrickEventBus.postEventClient(new KeyEvent.Press(keyMapping));
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "tick")
    public void clientTickPost(CallbackInfo info) {
        BrickEventBus.postEvent(new ClientTickEvent.Post());
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void inject111(Screen guiScreen, CallbackInfo ci) {
        if (guiScreen != null) {
            if (BrickEventBus.postEventClient(new PlayerEvent.Gui.Open(Minecraft.getInstance().player, guiScreen))) {
                this.screen = null;
                BufferUploader.reset();
                soundManager.resume();
                if (Minecraft.getInstance().isWindowActive() && !this.mouseHandler.isMouseGrabbed()) {
                    if (!Minecraft.ON_OSX) {
                        KeyMapping.setAll();
                    }
                    Minecraft.getInstance().setScreen(null);
                    this.mouseHandler.setIgnoreFirstMove();
                }
                ci.cancel();
            }
        } else {
            if (BrickEventBus.postEventClient(new PlayerEvent.Gui.Close(Minecraft.getInstance().player,null))) {
                BufferUploader.reset();
                this.soundManager.resume();
                this.mouseHandler.grabMouse();
            }
        }
    }

    private Minecraft getThis(){
        return (Minecraft) (Object) this;
    }
}
