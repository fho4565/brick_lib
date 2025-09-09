package com.arc_studio.brick_lib.mixin.common.client;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.client.RenderEvent;
import com.arc_studio.brick_lib.register.BrickRegistries;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Shadow
    @Final
    private PoseStack pose;

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void renderDeco(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (BrickEventBus.postEventClient(new RenderEvent.RenderItemDecoration(this.pose, stack, font, x, y, text))) {
            ci.cancel();
        } else {
            GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
            BrickRegistries.ITEM_DECORATION.values().forEach(decoration -> {
                if (decoration.getItem() == null || decoration.getItem().asItem().equals(stack.getItem())) {
                    decoration.render(guiGraphics, stack, font, x, y, Minecraft.getInstance()
                            //? if >= 1.21 {
                            /*.getTimer().getGameTimeDeltaPartialTick(true)
                            *///?} else {
                                    .getFrameTime()
                            //?}
                    );
                }
            });
        }
    }
}
