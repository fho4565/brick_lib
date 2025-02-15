package com.fho4565.brick_lib.client.gui;

import com.fho4565.brick_lib.client.gui.layout.EmptyLayout;
import com.fho4565.brick_lib.client.gui.layout.Layout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BScreen extends Screen {
    Layout layout = new EmptyLayout();

    public BScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);

    }
}