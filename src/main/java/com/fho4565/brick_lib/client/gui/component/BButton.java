package com.fho4565.brick_lib.client.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * <p>BrickLib的按钮实现</p>
 * <p>可以显示BImage图片或者文字，当图片可以显示时，不显示文字</p>
 *
 * @author fho4565
 */
public class BButton extends Button implements IBrickComponent{
    private BImage image;
    private boolean showImage = true;

    public BButton(int x, int y, int width, int height, Component message, OnPress pOnPress) {
        this(x, y, width, height, message, pOnPress, pMessageSupplier -> MutableComponent.create(message.getContents()));
    }

    public BButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration) {
        this(x, y, width, height, new ResourceLocation[0], message, onPress, createNarration);
    }

    public BButton(int x, int y, int width, int height, ResourceLocation[] textures, Component message, OnPress onPress) {
        this(x, y, width, height, textures, message, onPress, pMessageSupplier -> Component.literal(""));
    }

    public BButton(int x, int y, int width, int height, ResourceLocation[] textures, Component message, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, message, onPress, createNarration);
        if (textures.length > 0) {
            image = new BImage(textures);
        }
    }

    public BImage image() {
        return image;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (image != null && showImage) {
            image.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
        } else {
            super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    public boolean showImage() {
        return showImage;
    }

    public void setShowImage(boolean show) {
        showImage = show;
    }

    @Override
    public int x() {
        return this.getX();
    }

    @Override
    public int y() {
        return this.getY();
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        this.setX(x);
        this.setY(y);
        this.width = width;
        this.height = height;
    }
}
