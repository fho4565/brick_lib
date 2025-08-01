package com.arc_studio.brick_lib.events.client;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import com.arc_studio.brick_lib.api.event.ICancelableEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class RenderEvent extends BaseEvent {

    public static class RenderItemDecoration extends RenderEvent implements ICancelableEvent {
        Font font;
        ItemStack stack;
        int x;
        int y;
        @Nullable String text;
        PoseStack poseStack;

        public RenderItemDecoration(PoseStack poseStack, ItemStack stack, Font font, int x, int y, @Nullable String text) {
            this.font = font;
            this.stack = stack;
            this.x = x;
            this.y = y;
            this.text = text;
            this.poseStack = poseStack;
        }

        public Font getFont() {
            return font;
        }

        public void setFont(Font font) {
            this.font = font;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public @Nullable String getText() {
            return text;
        }

        public void setText(@Nullable String text) {
            this.text = text;
        }

        public PoseStack getPoseStack() {
            return poseStack;
        }

        public void setPoseStack(PoseStack poseStack) {
            this.poseStack = poseStack;
        }
    }
}
