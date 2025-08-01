package com.arc_studio.brick_lib.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

/**
 * 在玩家物品栏或者容器槽位渲染物品时的额外渲染
 * */
public abstract class ItemDecorationRender {
    ItemLike itemLike;

    /**
     * @param itemLike 要渲染的物品，null则是所有物品
     * */
    public ItemDecorationRender(ItemLike itemLike) {
        this.itemLike = itemLike;
    }

    @Nullable
    public ItemLike getItem() {
        return itemLike;
    }

    public static ItemDecorationRender empty(){
        return new ItemDecorationRender(null) {
            @Override
            public void render(GuiGraphics guiGraphics, ItemStack stack, Font font, int x, int y, float partialTick) {

            }
        };
    }

    public abstract void render(GuiGraphics guiGraphics, ItemStack stack, Font font, int x, int y, float partialTick);
}
