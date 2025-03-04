package com.fho4565.brick_lib.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class BInputBox extends EditBox implements IBrickComponent {
    public BInputBox(int x, int y, int width, int height, Component message) {
        super(Minecraft.getInstance().font, x, y, width, height, message);
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
}
