package com.arc_studio.brick_lib.events.client;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import com.arc_studio.brick_lib.api.event.ICancelableEvent;
import com.arc_studio.brick_lib.api.event.IClientOnlyEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class RenderEvent extends BaseEvent implements IClientOnlyEvent {

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

    public abstract static class HudEvent extends RenderEvent {
        GuiGraphics guiGraphics;

        public HudEvent(GuiGraphics guiGraphics) {
            this.guiGraphics = guiGraphics;
        }

        public GuiGraphics guiGraphics() {
            return guiGraphics;
        }

        public static class Render extends HudEvent {

            public Render(GuiGraphics guiGraphics) {
                super(guiGraphics);
            }
        }

        public abstract static class VanillaRender extends HudEvent {
            boolean cancelVanillaRender = false;

            public VanillaRender(GuiGraphics guiGraphics) {
                super(guiGraphics);
            }

            public void cancelVanillaRender() {
                this.cancelVanillaRender = true;
            }

            /**
             * 取消原版的渲染流程
             *
             */
            public boolean isVanillaRenderCanceled() {
                return cancelVanillaRender;
            }

            public static class ExperienceBar extends VanillaRender {
                boolean renderBackground = true;
                boolean renderExpProgressBar = true;
                int barX;
                int barY;
                int barHeight = 5;
                int backgroundBarWidth = 182;
                int progressBarWidth;
                String text;
                int textX;
                int textY;

                public ExperienceBar(GuiGraphics guiGraphics, int barX, int barY, int progressBarWidth, String text, int textX, int textY) {
                    super(guiGraphics);
                    this.barX = barX;
                    this.barY = barY;
                    this.progressBarWidth = progressBarWidth;
                    this.text = text;
                    this.textX = textX;
                    this.textY = textY;
                }

                public int barHeight() {
                    return barHeight;
                }

                public void setBarHeight(int barHeight) {
                    this.barHeight = barHeight;
                }

                public int barY() {
                    return barY;
                }

                public void setBarY(int barY) {
                    this.barY = barY;
                }

                public int backgroundBarWidth() {
                    return backgroundBarWidth;
                }

                public void setBackgroundBarWidth(int backgroundBarWidth) {
                    this.backgroundBarWidth = backgroundBarWidth;
                }

                public int barX() {
                    return barX;
                }

                public void setBarX(int barX) {
                    this.barX = barX;
                }

                public boolean renderBackground() {
                    return renderBackground;
                }

                public void setRenderBackground(boolean renderBackground) {
                    this.renderBackground = renderBackground;
                }

                public boolean renderProgressBar() {
                    return renderExpProgressBar;
                }

                public void setRenderExpProgressBar(boolean renderExpProgressBar) {
                    this.renderExpProgressBar = renderExpProgressBar;
                }

                public int expProgressBarWidth() {
                    return progressBarWidth;
                }

                public void setProgressBarWidth(int progressBarWidth) {
                    this.progressBarWidth = progressBarWidth;
                }

                public String levelText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public int levelTextX() {
                    return textX;
                }

                public void setTextX(int textX) {
                    this.textX = textX;
                }

                public int levelTextY() {
                    return textY;
                }

                public void setTextY(int textY) {
                    this.textY = textY;
                }
            }

            public static class EffectIcon extends VanillaRender {

                public EffectIcon(GuiGraphics guiGraphics) {
                    super(guiGraphics);
                }
            }
        }
    }
}
