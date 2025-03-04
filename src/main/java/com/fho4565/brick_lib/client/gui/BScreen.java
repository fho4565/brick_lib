package com.fho4565.brick_lib.client.gui;

import com.fho4565.brick_lib.client.gui.component.BComponent;
import com.fho4565.brick_lib.client.gui.layout.EmptyLayout;
import com.fho4565.brick_lib.client.gui.layout.Layout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BScreen extends Screen {
    Layout layout = new EmptyLayout();

    public BScreen(Component title) {
        super(title);
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        layout.placeComponents(this.renderables.stream().filter(renderable -> renderable instanceof BComponent).map(renderable -> (BComponent) renderable).toList());
    }
}