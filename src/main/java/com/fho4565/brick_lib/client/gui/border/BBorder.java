package com.fho4565.brick_lib.client.gui.border;

import com.fho4565.brick_lib.client.gui.component.BComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;

public abstract class BBorder implements Renderable {
    BComponent component;
    int paddingLeft = 2;
    int paddingRight = 2;
    int paddingTop = 2;
    int paddingBottom = 2;

    public BBorder(BComponent component) {
        this.component = component;
    }
    public BBorder(BComponent component,int padding) {
        this.component = component;
        this.paddingLeft = padding;
        this.paddingRight = padding;
        this.paddingTop = padding;
        this.paddingBottom = padding;
    }

    public int paddingBottom() {
        return paddingBottom;
    }

    public void setInsetBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public int paddingLeft() {
        return paddingLeft;
    }

    public void setInsetLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int paddingRight() {
        return paddingRight;
    }

    public void setInsetRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public int paddingTop() {
        return paddingTop;
    }

    public void setInsetTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBorder(guiGraphics,component.getX(),component.getY(),component.getWidth(),component.getHeight());
    }
    public abstract void renderBorder(GuiGraphics guiGraphics,int x,int y,int width,int height);
}